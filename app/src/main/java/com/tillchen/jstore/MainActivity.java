package com.tillchen.jstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupNavController();

        checkUser();

    }

    private void setupNavController() {
        BottomNavigationView navView = findViewById(R.id.nav_view);

        /* Currently unused
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_buy, R.id.navigation_sell, R.id.navigation_me)
                .build();
        */

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, navController);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void checkUser() {
        // Check if the user has logged in. If not, start PasswordlessLoginActivity.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { // user not logged in
            Log.i(TAG, "User not logged in, start PasswordlessLoginActivity");
            Intent intent = new Intent(this, PasswordlessLoginActivity.class);
            startActivity(intent);
            finish(); // remove MainActivity from the back stack
        }
    }

}
