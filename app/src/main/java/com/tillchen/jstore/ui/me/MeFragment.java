package com.tillchen.jstore.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import com.tillchen.jstore.models.MeItem;
import com.tillchen.jstore.models.MeItemAdapter;

import java.util.ArrayList;

public class MeFragment extends Fragment {

    private static final String TAG = "MeFragment";

    private FirebaseUser user;

    private Toolbar mAnonymousToolbar;
    private Button mAnonymousSignOutButton;
    private ListView mListView;

    private MeItemAdapter mMeItemAdapter;

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
            findViews(root);
            setData(root);
            return root;
        }
    }

    private void findViews(@NonNull View root) {
        mListView = root.findViewById(R.id.me_listView);
    }

    private void setData(View root) {
        ArrayList<MeItem> arrayList = new ArrayList<MeItem>();
        mMeItemAdapter = new MeItemAdapter(getActivity(), arrayList);
        mListView.setAdapter(mMeItemAdapter);
        MeItem meItem1 = new MeItem(getResources().getString(R.string.active_posts), R.drawable.posts);
        MeItem meItem2 = new MeItem(getResources().getString(R.string.sold_items), R.drawable.euro);
        MeItem meItem3 = new MeItem(getResources().getString(R.string.notification_settings), R.drawable.notifications);
        mMeItemAdapter.add(meItem1);
        mMeItemAdapter.add(meItem2);
        mMeItemAdapter.add(meItem3);
    }

    private void setAnonymous(@NonNull View root) {
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