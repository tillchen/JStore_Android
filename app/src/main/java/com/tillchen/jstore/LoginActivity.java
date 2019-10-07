package com.tillchen.jstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        mAuth = FirebaseAuth.getInstance();

        mSignInButton = findViewById(R.id.signin_button);
        mAnonymousSignInButton = findViewById(R.id.anonymous_signin_button);
        mEmailEditText = findViewById(R.id.email_edittext);

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
        Log.i(TAG, "emailLinkSignIn");
        handleUsername();
        ActionCodeSettings settings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(
                        getPackageName(),
                        true,
                        null)
                .setHandleCodeInApp(true)
                .setUrl("https://jstore.xyz") // TODO: 0 Check if this is correct
                .build();

        hideKeyboard(mEmailEditText);

        mAuth.sendSignInLinkToEmail(mEmail, settings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.i(TAG, "Link sent");
                            showSnackbar("Sign-in link sent to " + mEmail);
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


    private void anonymousSignIn() {

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
