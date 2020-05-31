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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tillchen.jstore.models.User;

import java.util.HashMap;
import java.util.Map;

public class NewUserActivity extends UtilityActivity implements View.OnClickListener {

    private static final String TAG = "NewUserActivity";

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    RadioButton mRadioButton1; // WhatsApp
    RadioButton mRadioButton2; // Email
    Button mButton; // Start Using Button
    TextView mPlusSign;
    EditText mEditTextCountryCode;
    EditText mEditTextPhone;
    EditText mEditTextFullName;
    ProgressBar mProgressBar;

    String mFullName; // the full name that the user entered
    String mCountryCode;
    String mPhone; // the phone number that the user entered
    boolean isWhatsApp = false; // whether the user selected WhatsApp
    boolean isEmail = false; // whether the user selected Email
    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        findViews();

        setVisible();

        setListeners();

        mEmail = mAuth.getCurrentUser().getEmail();
    }

    private void findViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_details);
        setSupportActionBar(toolbar);
        mRadioButton1 = findViewById(R.id.WhatsApp_radioButton);
        mRadioButton2 = findViewById(R.id.email_radioButton);
        mButton = findViewById(R.id.start_button_1);
        mPlusSign = findViewById(R.id.plus_sign_textView);
        mEditTextCountryCode = findViewById(R.id.country_code_editText);
        mEditTextPhone = findViewById(R.id.phone_editText);
        mEditTextFullName = findViewById(R.id.full_name_editText);
        mProgressBar = findViewById(R.id.new_user_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void setVisible() {
        mPlusSign.setVisibility(View.VISIBLE);
        mEditTextPhone.setVisibility(View.VISIBLE);
        mEditTextCountryCode.setVisibility(View.VISIBLE);
    }

    private void setInvisible() {
        mPlusSign.setVisibility(View.INVISIBLE);
        mEditTextPhone.setVisibility(View.INVISIBLE);
        mEditTextCountryCode.setVisibility(View.INVISIBLE);
    }

    private void setListeners() {
        mRadioButton1.setOnClickListener(this);
        mRadioButton2.setOnClickListener(this);
        mButton.setOnClickListener(this);

        mEditTextFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mEditTextCountryCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.WhatsApp_radioButton:
                if (((RadioButton) v).isChecked()) {
                    setVisible();
                    isWhatsApp = true;
                    isEmail = false;
                }
                break;

            case R.id.email_radioButton:
                if (((RadioButton) v).isChecked()) {
                    setInvisible();
                    isWhatsApp = false;
                    isEmail = true;
                }
                break;

            case R.id.start_button_1:

                if (!checkFields()) {
                    break;
                }

                addUserToDB();

                break;
            default:
                break;
        }
    }


    private boolean checkFields() {
        mFullName = mEditTextFullName.getText().toString();
        mCountryCode = mEditTextCountryCode.getText().toString();
        mPhone = mEditTextPhone.getText().toString();

        hideKeyboard(mEditTextFullName);
        hideKeyboard(mEditTextPhone);

        if (TextUtils.isEmpty(mFullName)) {
            mEditTextFullName.setError("Full Name can't be empty.");
            return false;
        }

        if (isWhatsApp && TextUtils.isEmpty(mCountryCode)) {
            mEditTextCountryCode.setError("Country code can't be empty.");
            return false;
        }

        if (isWhatsApp && TextUtils.isEmpty(mPhone)) {
            mEditTextPhone.setError("Phone number can't be empty.");
            return false;
        }

        if (!isEmail && !isWhatsApp) {
            showSnackbar("Please select your preferred way of contact.");
            return false;
        }

        return true;
    }

    private void addUserToDB() {
        User user = new User(mFullName, isWhatsApp, mCountryCode + mPhone, mEmail);

        mProgressBar.setVisibility(View.VISIBLE);
        db.collection(COLLECTION_USERS).document(mEmail).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, mEmail + "is written!");
                addCreationDate();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, mEmail + "is NOT written!");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        showSnackbar("Error when writing your data! Please try again.");
                    }
        });
    }

    private void addCreationDate() {
        Map<String, Object> updates = new HashMap<>();
        updates.put(User.CREATIONDATE, FieldValue.serverTimestamp());

        db.collection(UtilityActivity.COLLECTION_USERS).document(mEmail)
                .update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    Log.i(TAG, "creationDate updated: " + mEmail);
                    Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Log.e(TAG, "creationDate failed ", task.getException());
                    showSnackbar("Error when writing your data! Please try again!");
                }
            }
        });
    }

}
