package com.tillchen.jstore;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tillchen.jstore.models.User;

public class NewUserActivity extends UtilityActivity implements View.OnClickListener {

    // TODO: 0 Reopen this activity if the user didn't finish and quit unexpectedly

    private static String TAG = "NewUserActivity";

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    RadioButton mRadioButton1; // WhatsApp
    RadioButton mRadioButton2; // Email
    Button mButton; // Start Using Button
    EditText mEditTextPhone;
    EditText mEditTextFullName;
    String mFullName; // the full name that the user entered
    String mPhone; // the phone number that the user entered
    boolean isWhatsApp = false; // whether the user selected WhatsApp
    boolean isEmail = false; // whether the user selected Email
    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mRadioButton1 = findViewById(R.id.WhatsApp_radioButton);
        mRadioButton2 = findViewById(R.id.email_radioButton);
        mButton = findViewById(R.id.start_button_1);
        mEditTextPhone = findViewById(R.id.phone_editText);
        mEditTextFullName = findViewById(R.id.full_name_editText);

        mRadioButton1.setOnClickListener(this);
        mRadioButton2.setOnClickListener(this);
        mButton.setOnClickListener(this);

        mEditTextPhone.setVisibility(View.VISIBLE);

        mEditTextFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        mEditTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mEmail = mAuth.getCurrentUser().getEmail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.WhatsApp_radioButton:
                if (((RadioButton) v).isChecked()) {
                    mEditTextPhone.setVisibility(View.VISIBLE);
                    isWhatsApp = true;
                    isEmail = false;
                }
                break;
            case R.id.email_radioButton:
                if (((RadioButton) v).isChecked()) {
                    mEditTextPhone.setVisibility(View.INVISIBLE);
                    isWhatsApp = false;
                    isEmail = true;
                }
                break;
            case R.id.start_button_1:
                mFullName = mEditTextFullName.getText().toString();
                mPhone = mEditTextPhone.getText().toString();

                hideKeyboard(mEditTextFullName);
                hideKeyboard(mEditTextPhone);

                if (TextUtils.isEmpty(mFullName)) {
                    mEditTextFullName.setError("Full Name can't be empty.");
                    return;
                }

                if (isWhatsApp && TextUtils.isEmpty(mPhone)) {
                    mEditTextPhone.setError("Phone number can't be empty.");
                    return;
                }

                if (!isEmail && !isWhatsApp) {
                    showSnackbar("Please select your preferred way of contact.");
                    return;
                }

                User user = new User(mFullName, isWhatsApp, mPhone, mEmail);
                db.collection(COLLECTION_USERS).document(mEmail).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, mEmail + "is written!");
                        Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, mEmail + "is NOT written!");
                        showSnackbar("Error when writing your data! Please try again.");
                    }
                });
                break;
            default:
                break;
        }
    }

}
