package com.example.myapplication.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class AppUtils {

    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
