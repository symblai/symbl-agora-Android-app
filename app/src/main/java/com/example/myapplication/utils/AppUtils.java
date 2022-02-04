package com.example.myapplication.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.myapplication.R;
import com.example.myapplication.models.ApplicationPreferences;
import com.google.gson.Gson;

public class AppUtils {

    public static void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static ApplicationPreferences getAppPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
        String value = sharedPreferences.getString(context.getString(R.string.app_preferences), context.getString(R.string.empty_json));
        return new Gson().fromJson(value, ApplicationPreferences.class);
    }
}
