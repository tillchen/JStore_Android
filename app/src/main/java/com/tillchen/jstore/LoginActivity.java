package com.tillchen.jstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class LoginActivity extends UtilityActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String ADMIN = "tillchen417@gmail.com"; // admin email

    private FirebaseAuth mAuth;
    private Button mSendLinkButton;
    private Button mSignInButton;
    private Button mAnonymousSignInButton;
    private EditText mEmailEditText;
    private String mUsername; // the username that the user entered
    private String mEmail; // the final email address
    private SharedPreferences mSharedPreferences; // stores the pending username
    private SharedPreferences.Editor mEditor;
    private String mPendingUsername; // the pending username that's refilled in EditText

    private boolean admin = false; // admin privileges

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initDataViewAndListeners();

        checkIntent(getIntent());
    }

    private void initDataViewAndListeners () {
        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getSharedPreferences(PENDING_USERNAME, Context.MODE_PRIVATE);

        mSendLinkButton = findViewById(R.id.sendlink_button);
        mSignInButton = findViewById(R.id.signin_button);
        mAnonymousSignInButton = findViewById(R.id.anonymous_signin_button);
        mEmailEditText = findViewById(R.id.email_edittext);

        mSendLinkButton.setVisibility(View.VISIBLE);
        mSignInButton.setVisibility(View.GONE);

        mSendLinkButton.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);
        mAnonymousSignInButton.setOnClickListener(this);
        mEmailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        // TODO: 2 Disable anonymous button when text is entered (onTextChanged)

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) { // check if the intent has an email address
        if (intentHasEmailLink(intent)) {
            mSendLinkButton.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.VISIBLE);
            mAnonymousSignInButton.setEnabled(false);
            mPendingUsername = mSharedPreferences.getString(PENDING_USERNAME, "");
            if (TextUtils.isEmpty(mPendingUsername)) {
                Log.i(TAG, "checkIntent mPendingUsername empty");
                mEmailEditText.setHint(R.string.reenter_username);
            }
            else {
                Log.i(TAG, "checkIntent mPendingUsername " + mUsername);
                mEmailEditText.setText(mPendingUsername, TextView.BufferType.NORMAL);
            }
        }
        else {
            mSendLinkButton.setVisibility(View.VISIBLE);
            mSignInButton.setVisibility(View.GONE);
            mAnonymousSignInButton.setEnabled(true);
        }
    }

    private boolean intentHasEmailLink(@Nullable Intent intent) {
        String intentData = "Default";
        if (intent != null && intent.getStringExtra(EMAIL_LINK) != null) {
            intentData = intent.getStringExtra(EMAIL_LINK);
            if (mAuth.isSignInWithEmailLink(intentData)) {
                Log.i(TAG, "intentHasEmailLink true");
                return true;
            }
        }
        Log.i(TAG, "intentHasEmailLink false: " + intentData);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendlink_button:
                onSendLinkClicked();
                break;
            case R.id.signin_button:
                
            case R.id.anonymous_signin_button:
                onAnonymousSignInClicked();
            default:
                break;
        }
    }

    private void onSendLinkClicked() {
        Log.i(TAG, "onSendLinkClicked");

        handleUsername();

        sendEmail();

    }

    private void onSignInClicked() {
        Log.i(TAG, "onSignInClicked");
    }


    private void onAnonymousSignInClicked() {

    }

    private void handleUsername() {
        // TODO: 3 Add a button to Snackbar that opens OutLook
        mUsername = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(mUsername)) {
            Log.i(TAG, "handleUsername: empty input");
            mEmailEditText.setError("Please enter your Jacobs username, (e.g. ti.chen).");
            return;
        }
        if (!validateUsername(mUsername)) {
            Log.i(TAG, "handleUsername: illegal input");
            mEmailEditText.setError("Your Jacobs username must contain a dot and no space, (e.g. ti.chen).");
            return;
        }
        if (ADMIN.equals(mUsername)) { // admin
            Log.i(TAG, "handleUsername: entering admin mode");
            admin = true;
            mEmail = mUsername;
        }
        else {
            mEmail = mUsername + "@jacobs-university.de";
        }
    }

    private void sendEmail() {
        // TODO: 1 Add ProgressBar
        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(
                        getPackageName(),
                        true,
                        null)
                .setHandleCodeInApp(true)
                .setUrl("https://jstore.xyz")
                .build();

        hideKeyboard(mEmailEditText);

        mAuth.sendSignInLinkToEmail(mEmail, settings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.i(TAG, "Link sent");
                            showSnackbar("Sign-in link sent to " + mEmail);
                            mEditor = mSharedPreferences.edit();
                            mEditor.putString(PENDING_USERNAME, mUsername);
                            mEditor.apply();
                        } else {
                            Exception e = task.getException();
                            Log.w(TAG, "Could not send link", e);
                            showSnackbar("Failed to send link to " + mEmail);

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                mEmailEditText.setError("Invalid email address.");
                            }
                        }
                    }
                });
    }
    

    private boolean validateUsername(String username) { // the username must contain a dot and not space
        // TODO: 1 Refine the validation.
        return (username.indexOf('.') != -1) && (username.indexOf(' ') == -1);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public boolean getAdmin() {
        return admin;
    }
}
