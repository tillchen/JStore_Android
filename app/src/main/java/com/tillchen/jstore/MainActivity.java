package com.tillchen.jstore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends UtilityActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private String mIntentData = "Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.nav_view).bringToFront();

        checkUser();
        setupNavController();

    }


    private boolean intentHasEmailLink(@Nullable Intent intent) {
        if (intent != null && intent.getData() != null) {
            mIntentData = intent.getData().toString();
            if (mAuth.isSignInWithEmailLink(mIntentData)) {
                Log.i(TAG, "intentHasEmailLink true");
                return true;
            }
        }
        Log.i(TAG, "intentHasEmailLink false: " + mIntentData);
        return false;
    }


    private void setupNavController() {
        Log.i(TAG, "");
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void checkUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Check if the user has logged in. If not, start LoginActivity.
        if (user == null) { // user not logged in
            Log.i(TAG, "User not logged in, start LoginActivity");
            Intent intent = new Intent(this, LoginActivity.class);
            if (intentHasEmailLink(getIntent())) {
                intent.putExtra(EMAIL_LINK, mIntentData);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // auto removes LoginActivity from the back stack when jumping back to MainActivity
            startActivity(intent);
            finish(); // remove MainActivity from the back stack
        }
        else if (!user.isAnonymous()){
            // Check if the user entered basic info to the DB. If not, start NewUserActivity
            DocumentReference docRef = db.collection(COLLECTION_USERS).document(user.getEmail());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.i(TAG, "checkUser document exists");
                        }
                        else {
                            Log.i(TAG, "checkUser document does NOT exist, starting NewUserActivity");
                            Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else {
                        Log.e(TAG, "checkUser get failed with ", task.getException());
                    }
                }
            });
        }
    }

}
