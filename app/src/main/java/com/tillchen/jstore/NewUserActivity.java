package com.tillchen.jstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class NewUserActivity extends UtilityActivity implements View.OnClickListener {

    RadioButton mRadioButton1; // WhatsApp
    RadioButton mRadioButton2; // Email
    Button mButton; // Start Using Button
    EditText mEditTextPhone;
    EditText mEditTextFullName;
    String mFullName; // the full name that the user entered
    String mPhone; // the phone number that the user entered
    boolean isWhatsApp = true; // whether the user selected WhatsApp

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.WhatsApp_radioButton:
                if (((RadioButton) v).isChecked()) {
                    mEditTextPhone.setVisibility(View.VISIBLE);
                    isWhatsApp = true;
                }
                break;
            case R.id.email_radioButton:
                if (((RadioButton) v).isChecked()) {
                    mEditTextPhone.setVisibility(View.INVISIBLE);
                    isWhatsApp = false;
                }
                break;
            case R.id.start_button_1:
                mFullName = mEditTextFullName.getText().toString();
                mPhone = mEditTextPhone.getText().toString();

                if (TextUtils.isEmpty(mFullName)) {
                    mEditTextFullName.setError("Full Name can't be empty.");
                    return;
                }

                if (isWhatsApp && TextUtils.isEmpty(mPhone)) {
                    mEditTextPhone.setError("Phone number can't be empty.");
                    return;
                }

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                // TODO: 0 SAVE the info
                if (isWhatsApp) { // TODO: 0 Save the phone number
                    
                }
                finish();
                break;
            default:
                break;
        }
    }

}
