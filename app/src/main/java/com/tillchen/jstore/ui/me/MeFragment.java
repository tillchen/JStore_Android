package com.tillchen.jstore.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tillchen.jstore.LoginActivity;
import com.tillchen.jstore.R;

public class MeFragment extends Fragment {

    private static final String TAG = "MeFragment";

    private FirebaseUser user;

    private Toolbar mAnonymousToolbar;
    private Button mAnonymousSignOutButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isAnonymous()) {
            View root = inflater.inflate(R.layout.fragment_anonymous_me, container, false);
            setAnonymous(root);
            return root;
        }
        else {
            View root = inflater.inflate(R.layout.fragment_me, container, false);
            return root;
        }
    }

    private void setAnonymous(View root) {
        mAnonymousToolbar = root.findViewById(R.id.anonymous_me_toolbar);
        mAnonymousSignOutButton = root.findViewById(R.id.anonymous_sign_out_button);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mAnonymousToolbar);
        mAnonymousSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "AnonymousSignOutButton clicked. Signing out.");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Log.i(TAG, "starting LoginActivity");
                startActivity(intent);
            }
        });
    }
}