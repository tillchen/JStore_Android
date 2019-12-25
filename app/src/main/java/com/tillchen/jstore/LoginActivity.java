package com.tillchen.jstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class LoginActivity extends UtilityActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final String ADMIN = "tillchen417@gmail.com"; // admin email

    private FirebaseAuth mAuth;
    private Button mSendLinkButton;
    private Button mSignInButton;
    private Button mAnonymousSignInButton;
    private EditText mEmailEditText;
    private ProgressBar mLoginProgressBar;
    private TextView mFooter;
    private TextView mBySigningIn;

    private String mUsername; // the username that the user entered
    private String mEmail; // the final email address
    private SharedPreferences mSharedPreferences; // stores the pending username
    private SharedPreferences.Editor mEditor;
    private String mPendingUsername; // the pending username that's refilled in EditText
    private String mIntentData; // contains the sign-in link
    private boolean validUserName = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initDataViewAndListeners();

        checkIntent(getIntent());
    }

    private void initDataViewAndListeners () {
        Toolbar toolbar = findViewById(R.id.toolbar_details);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getSharedPreferences(PENDING_USERNAME, Context.MODE_PRIVATE);

        mSendLinkButton = findViewById(R.id.sendlink_button);
        mSignInButton = findViewById(R.id.signin_button);
        mAnonymousSignInButton = findViewById(R.id.anonymous_signin_button);
        mEmailEditText = findViewById(R.id.email_editText);
        mLoginProgressBar = findViewById(R.id.login_progressBar);

        mSendLinkButton.setVisibility(View.VISIBLE);
        mSignInButton.setVisibility(View.GONE);
        mLoginProgressBar.setVisibility(View.INVISIBLE);

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
        mFooter = findViewById(R.id.footer_textView);
        mFooter.setMovementMethod(LinkMovementMethod.getInstance());
        mBySigningIn = findViewById(R.id.by_signing_in_textView);
        mBySigningIn.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) { // check if the intent has an email address
        if (intent == null) {
            return;
        }
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
                mEmailEditText.setEnabled(false); // prevent further editing
            }
        }
        else {
            mSendLinkButton.setVisibility(View.VISIBLE);
            mSignInButton.setVisibility(View.GONE);
            mAnonymousSignInButton.setEnabled(true);
        }
    }

    private boolean intentHasEmailLink(@NonNull Intent intent) {
        mIntentData = intent.getStringExtra(EMAIL_LINK);
        if (!TextUtils.isEmpty(mIntentData)) {
            if (mAuth.isSignInWithEmailLink(mIntentData)) {
                Log.i(TAG, "intentHasEmailLink true");
                return true;
            }
        }
        Log.i(TAG, "intentHasEmailLink false: " + mIntentData);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendlink_button:
                onSendLinkClicked();
                break;
            case R.id.signin_button:
                onSignInClicked();
                break;
            case R.id.anonymous_signin_button:
                onAnonymousSignInClicked();
                break;
            default:
                break;
        }
    }

    private void onSendLinkClicked() {
        Log.i(TAG, "onSendLinkClicked");

        handleUsername();

        if (validUserName) {
            sendEmail();
        }

    }

    private void onSignInClicked() {
        Log.i(TAG, "onSignInClicked");

        handlePendingUsername();

        if (!validUserName) {
            return;
        }

        mLoginProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailLink(mEmail, mIntentData).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                mLoginProgressBar.setVisibility(View.INVISIBLE);

                if (task.isSuccessful()) {
                    Log.i(TAG, "onSignInClicked task is successful");
                    AuthResult result = task.getResult();
                    if (result != null && result.getAdditionalUserInfo().isNewUser()) {
                        Intent intent = new Intent(LoginActivity.this, NewUserActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }
                else {
                    Log.w(TAG, "onSignInClicked task failed", task.getException());
                    if (task.getException() instanceof FirebaseAuthActionCodeException) {
                        showSnackbar("Invalid or expired sign-in link. Please quit the app and send a new link.");
                    }
                }
            }
        });
    }


    private void onAnonymousSignInClicked() {
        Log.i(TAG, "onAnonymousSignInClicked");

        mLoginProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        mLoginProgressBar.setVisibility(View.INVISIBLE);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            Intent intent = new Intent(LoginActivity.this, AnonymousNewUserActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            showSnackbar("Authentication failed.");
                        }
                    }
                });

    }

    private void handleUsername() {
        mUsername = mEmailEditText.getText().toString();
        if (TextUtils.isEmpty(mUsername)) {
            Log.i(TAG, "handleUsername: empty input");
            mEmailEditText.setError("Please enter your Jacobs username, (e.g. ti.chen).");
            showSnackbar("Please enter your Jacobs username, (e.g. ti.chen).");
            validUserName = false;
            return;
        }
        if (ADMIN.equals(mUsername)) { // admin
            Log.i(TAG, "handleUsername: entering admin mode");
            mEmail = mUsername;
            validUserName = true;
            return;
        }
        if (!validateUsername()) {
            Log.i(TAG, "handleUsername: illegal input");
            mEmailEditText.setError("Your Jacobs username must contain a dot, no space, and no @, (e.g. ti.chen).");
            validUserName = false;
        }
        else {
            mEmail = mUsername + "@jacobs-university.de";
            validUserName = true;
        }
    }

    private void handlePendingUsername() {
        if (ADMIN.equals(mPendingUsername)) { // admin
            Log.i(TAG, "handlePendingUsername: entering admin mode");
            mEmail = mPendingUsername;
            validUserName = true;
        }
        else if (TextUtils.isEmpty(mPendingUsername)){
            handleUsername();
        }
        else {
            mEmail = mPendingUsername + "@jacobs-university.de";
            validUserName = true;
        }
    }

    private void sendEmail() {
        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(
                        getPackageName(),
                        true,
                        null)
                .setHandleCodeInApp(true)
                .setUrl("https://jstore.xyz")
                .setIOSBundleId("com.tillchen.jstore.ios")
                .build();

        hideKeyboard(mEmailEditText);

        mLoginProgressBar.setVisibility(View.VISIBLE);

        mAuth.sendSignInLinkToEmail(mEmail, settings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        mLoginProgressBar.setVisibility(View.INVISIBLE);

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
    

    private boolean validateUsername() { // the username must contain a dot no space and no @
        return (mUsername.indexOf('.') != -1 && mUsername.indexOf(' ') == -1) && (mUsername.indexOf('@') == -1);
    }



}
