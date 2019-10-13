package com.tillchen.jstore.ui.buy;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tillchen.jstore.MainActivity;
import com.tillchen.jstore.R;
import com.tillchen.jstore.UtilityActivity;

public class BuyFragment extends Fragment {

    private static final String TAG = "BuyFragment";

    FirebaseFirestore db;
    private Toolbar mToolbar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_buy, container, false);

        mToolbar = root.findViewById(R.id.buy_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        db = FirebaseFirestore.getInstance();

        db.collection(UtilityActivity.COLLECTION_POSTS)
                .whereEqualTo("sold", false)
                .orderBy("creationDate", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Log.i(TAG, document.getId() + "=>" + document.getData());
                    }
                }
                else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        return root;
    }
}