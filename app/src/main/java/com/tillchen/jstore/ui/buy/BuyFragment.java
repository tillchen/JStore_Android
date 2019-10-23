package com.tillchen.jstore.ui.buy;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;

import com.tillchen.jstore.R;
import com.tillchen.jstore.UtilityActivity;
import com.tillchen.jstore.models.Post;
import com.tillchen.jstore.models.PostViewHolder;

public class BuyFragment extends Fragment {

    private static final String TAG = "BuyFragment";

    private FirebaseFirestore db;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Query mQuery;
    private FirestorePagingAdapter mAdapter;
    private Spinner mCategorySpinner;
    private String mCategory;
    private ChipGroup mChipGroup;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_buy, container, false);

        db = FirebaseFirestore.getInstance();

        setUpQuery();

        findViews(root);

        setUpListeners();

        setUpAdapter();

        return root;
    }

    private void setUpQuery() {
        mQuery = db.collection(UtilityActivity.COLLECTION_POSTS)
                .whereEqualTo(UtilityActivity.SOLD, false)
                .orderBy(UtilityActivity.CREATION_DATE, Query.Direction.DESCENDING);
    }

    private void findViews(View root) {
        mToolbar = root.findViewById(R.id.buy_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mRecyclerView = root.findViewById(R.id.buy_recyclerView);
        mSwipeRefreshLayout = root.findViewById(R.id.buy_SwipteRefreshLayout);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mCategorySpinner = root.findViewById(R.id.buy_category_spinner);
        mChipGroup = root.findViewById(R.id.buy_filter_chipGroup);
    }

    private void setUpListeners() {
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) { // all categories
                    setUpQuery();
                    setUpAdapter();
                } else {
                    mCategory = mCategorySpinner.getSelectedItem().toString();
                    mQuery = db.collection(UtilityActivity.COLLECTION_POSTS)
                            .whereEqualTo(UtilityActivity.SOLD, false)
                            .whereEqualTo(UtilityActivity.CATEGORY, mCategory)
                            .orderBy(UtilityActivity.CREATION_DATE, Query.Direction.DESCENDING);
                    setUpAdapter();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // nothing
            }
        });
        mChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                switch (i) {
                    case R.id.price_up_chip:
                        mQuery = db.collection(UtilityActivity.COLLECTION_POSTS)
                                .whereEqualTo(UtilityActivity.SOLD, false)
                                .orderBy(UtilityActivity.PRICE, Query.Direction.ASCENDING);
                        setUpAdapter();
                        break;
                    case R.id.price_down_chip:
                        mQuery = db.collection(UtilityActivity.COLLECTION_POSTS)
                                .whereEqualTo(UtilityActivity.SOLD, false)
                                .orderBy(UtilityActivity.PRICE, Query.Direction.DESCENDING);
                        setUpAdapter();
                        break;
                    case R.id.date_up_chip:
                        mQuery = db.collection(UtilityActivity.COLLECTION_POSTS)
                                .whereEqualTo(UtilityActivity.SOLD, false)
                                .orderBy(UtilityActivity.CREATION_DATE, Query.Direction.ASCENDING);
                        setUpAdapter();
                        break;
                    case -1: // unchecked
                        setUpQuery();
                        setUpAdapter();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
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
                        showSnackbar("Sorry. Something went wrong. Retrying.");
                        retry();
                        break;

                    case FINISHED:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }

        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        // Refresh Action on Swipe Refresh Layout
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refresh();
            }
        });
    }


    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}