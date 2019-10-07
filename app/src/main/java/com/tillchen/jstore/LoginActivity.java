package com.tillchen.jstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String ADMIN = "tillchen417@gmail.com"; // admin email

    private Button mSignInButton;
    private Button mAnonymousSignInButton;
    private EditText mEmailEditText;
    private String mUsername; // the username that the user entered
    private String mEmail; // the final email address

    private boolean admin = false; // admin privileges

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        mSignInButton = findViewById(R.id.signin_button);
        mAnonymousSignInButton = findViewById(R.id.anonymous_signin_button);
        mEmailEditText = findViewById(R.id.email_edittext);

        mSignInButton.setOnClickListener(this);
        mAnonymousSignInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_button:
                emailLinkSignIn();
                break;
            case R.id.anonymous_signin_button:
                anonymousSignIn();
            default:
                break;
        }
    }

    private void emailLinkSignIn() {
        // TODO: Add a button to Snackbar that opens OutLook
        Log.i(TAG, "emailLinkSignIn");
        mUsername = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(mUsername)) {
            mEmailEditText.setError("Please enter your Jacobs username, (e.g. ti.chen).");
            return;
        }
        if (!validateUsername(mUsername)) {
            mEmailEditText.setError("Your Jacobs username must contain a dot, (e.g. ti.chen).");
            return;
        }
        if (ADMIN.equals(mUsername)) { // admin
            admin = true;
            mEmail = mUsername;
        }
        else {
            mEmail = mUsername + "@jacobs-university.de";
        }
        String message; // for the toast
        message = "Login email is sent to " + mEmail;
        showSnackbar(message);
    }

    private void anonymousSignIn() {

    }

    private boolean validateUsername(String username) { // the username must contain a dot
        // TODO: Refine the validation.
        return (username.indexOf('.') != -1);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public boolean getAdmin() {
        return admin;
    }
}
