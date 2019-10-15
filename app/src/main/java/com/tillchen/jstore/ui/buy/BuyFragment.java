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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tillchen.jstore.MainActivity;
import com.tillchen.jstore.R;
import com.tillchen.jstore.UtilityActivity;

import java.util.HashMap;
import java.util.Map;

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

        /* How to update soldDate:
        Map<String, Object> updates = new HashMap<>();
        updates.put("soldDate", FieldValue.serverTimestamp());

        db.collection(UtilityActivity.COLLECTION_POSTS).document("3cc42094-3728-4250-aafa-340b007dec4f")
                .update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        */

        return root;
    }
}