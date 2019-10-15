package com.tillchen.jstore;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class UtilityActivity extends AppCompatActivity {
    /* It contains handy functions and constants for other activities to use.
    * */

    public static final String EMAIL_LINK = "email_link";
    public static final String PENDING_USERNAME = "com.tillchen.jstore.pending_username";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_POSTS = "posts";
    public static final String STORAGE_POSTS = "posts";
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 71;
    public static final String CASH = "cash";
    public static final String BANK_TRANSFER = "bank_transfer";
    public static final String PAYPAL = "paypal";
    public static final String MEAL_PLAN = "meal_plan";
    public static final String AUTHORITY = "com.tillchen.jstore.android.fileprovider";

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
