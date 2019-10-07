package com.tillchen.jstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String ADMIN = "tillchen417@gmail.com"; // admin email

    private Button mSignInButton;
    private Button mAnonymousSignInButton;
    private EditText mEmailEditText;
    private String mUsername; // the username that the user entered
    private String mEmail; // the final email address

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
        // TODO: Add Username validation.
        // TODO: Change to SnackBar with a button that opens OutLook
        Log.i(TAG, "emailLinkSignIn");
        mUsername = mEmailEditText.getText().toString();
        String message; // for the toast
        if ("".equals(mUsername)) {
            message = "Please enter your Jacobs username, (e.g. ti.chen).";
        }
        else {
            mEmail = mUsername + "@jacobs-university.de";
            if (ADMIN.equals(mUsername)) { // admin
                mEmail = mUsername;
            }
            message = "Login email is sent to " + mEmail;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void anonymousSignIn() {

    }
}
