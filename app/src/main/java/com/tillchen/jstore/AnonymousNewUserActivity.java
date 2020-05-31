package com.tillchen.jstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AnonymousNewUserActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_new_user);

        mButton = findViewById(R.id.start_button_2);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button_2:
                onStartButtonClicked();
                break;
            default:
                break;
        }
    }

    private void onStartButtonClicked() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
