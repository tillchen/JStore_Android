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
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;

import com.tillchen.jstore.R;
import com.tillchen.jstore.UtilityActivity;
import com.tillchen.jstore.models.Post;
import com.tillchen.jstore.models.PostViewHolder;

import java.util.HashMap;
import java.util.Map;

public class BuyFragment extends Fragment {

    private static final String TAG = "BuyFragment";

    private FirebaseFirestore db;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Query mQuery;
    private FirestorePagingAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_buy, container, false);

        mToolbar = root.findViewById(R.id.buy_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        mRecyclerView = root.findViewById(R.id.buy_recyclerView);
        mSwipeRefreshLayout = root.findViewById(R.id.buy_SwipteRefreshLayout);

        db = FirebaseFirestore.getInstance();
        mQuery = db.collection(UtilityActivity.COLLECTION_POSTS)
                .whereEqualTo("sold", false)
                .orderBy("creationDate", Query.Direction.DESCENDING);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUpAdapter();


        return root;
    }

    private void setUpAdapter() {

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(2)
                .setPageSize(10)
                .build();

        FirestorePagingOptions options = new FirestorePagingOptions.Builder<Post>()
                .setLifecycleOwner(this)
                .setQuery(mQuery, config, Post.class)
                .build();

        mAdapter = new FirestorePagingAdapter<Post, PostViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post post) {
                holder.bind(post);
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.item_post, parent, false);
                return new PostViewHolder(view);
            }

            @Override
            protected void onError(@NonNull Exception e) {
                super.onError(e);
                Log.e(TAG, "Error: setUpAdapter: ", e);
            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        mSwipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
                        showSnackbar("Sorry. Something went wrong.");

                        mSwipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }

        };
    }

    private void updateSoldDate() {
        /*
        Map<String, Object> updates = new HashMap<>();
        updates.put("soldDate", FieldValue.serverTimestamp());

        db.collection(UtilityActivity.COLLECTION_POSTS).document("3cc42094-3728-4250-aafa-340b007dec4f")
                .update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        */
    }

    private void showSnackbar(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}