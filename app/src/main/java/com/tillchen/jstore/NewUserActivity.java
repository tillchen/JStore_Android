package com.tillchen.jstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class NewUserActivity extends AppCompatActivity implements View.OnClickListener {

    RadioButton mRadioButton1; // WhatsApp
    RadioButton mRadioButton2; // Email
    Button mButton; // Start Using Button
    EditText mEditText; // phone

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Toolbar toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        mRadioButton1 = findViewById(R.id.WhatsApp_radioButton);
        mRadioButton2 = findViewById(R.id.email_radioButton);
        mButton = findViewById(R.id.start_button_1);
        mEditText = findViewById(R.id.phone_editText);

        mRadioButton1.setOnClickListener(this);
        mRadioButton2.setOnClickListener(this);
        mButton.setOnClickListener(this);

        mEditText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.WhatsApp_radioButton:
                if (((RadioButton) v).isChecked()) {
                    mEditText.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.email_radioButton:
                if (((RadioButton) v).isChecked()) {
                    mEditText.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.start_button_1:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                // TODO: 0 SAVE the info
                finish();
                break;
            default:
                break;
        }
    }

}
