package com.example.myapplication.listener;

import static com.example.myapplication.constants.SwipeDirection.DOWN;
import static com.example.myapplication.constants.SwipeDirection.LEFT;
import static com.example.myapplication.constants.SwipeDirection.RIGHT;
import static com.example.myapplication.constants.SwipeDirection.UP;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SimpleGestureDetector extends SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private final GestureDetector detector;
    private final SimpleGestureListener simpleGestureListener;

    public SimpleGestureDetector(Activity context, SimpleGestureListener simpleGestureListener) {
        this.detector = new GestureDetector(context, this);
        this.simpleGestureListener = simpleGestureListener;
    }

    public void onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float yDistance = e2.getY() - e1.getY();
            float xDistance = e2.getX() - e1.getX();
            if (Math.abs(xDistance) > Math.abs(yDistance)) {
                if (Math.abs(xDistance) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (xDistance > 0) {
                        this.simpleGestureListener.onSwipe(RIGHT);
                    } else {
                        this.simpleGestureListener.onSwipe(LEFT);
                    }
                    result = true;
                }
            } else if (Math.abs(yDistance) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (yDistance > 0) {
                    this.simpleGestureListener.onSwipe(DOWN);
                } else {
                    this.simpleGestureListener.onSwipe(UP);
                }
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();  // TODO :- Log exception
        }
        return result;
    }
}
