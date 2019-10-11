package com.tillchen.jstore;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class UtilityActivity extends AppCompatActivity {
    /* It contains handy functions and constants for other activities to use.
    * */

    public static String EMAIL_LINK = "email_link";
    public static String PENDING_USERNAME = "com.tillchen.jstore.pending_username";
    public static String COLLECTION_USERS = "users";

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

}
