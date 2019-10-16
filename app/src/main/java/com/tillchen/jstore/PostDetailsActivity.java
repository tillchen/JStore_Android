package com.tillchen.jstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.tillchen.jstore.models.GlideApp;
import com.tillchen.jstore.models.Post;

import java.util.ArrayList;

public class PostDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailsActivity";

    public static final String CASH = "Cash ";
    public static final String BANK_TRANSFER = "Bank Transfer ";
    public static final String PAYPAL = "PayPal ";
    public static final String MEAL_PLAN = "Meal Plan ";

    FirebaseFirestore db;
    DocumentReference ref;
    FirebaseStorage storage;

    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mPriceTextView;
    private TextView mOwnerNameTextView;
    private TextView mCategoryTextView;
    private TextView mConditionTextView;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mPaymentOptionsTextView;
    private Button mEmailButton;
    private Button mWhatsAppButton;

    private String mPostID;
    private String mPaymentOptions;

    private Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        findViews();

        getPostFromDB();

    }

    private void findViews() {
        mImageView = findViewById(R.id.post_details_imageView);
        mTitleTextView = findViewById(R.id.post_details_title_textView);
        mPriceTextView = findViewById(R.id.post_details_price_textView);
        mOwnerNameTextView = findViewById(R.id.post_details_owner_name_textView);
        mCategoryTextView = findViewById(R.id.post_details_category_content_textView);
        mConditionTextView = findViewById(R.id.post_details_condition_content_textView);
        mDateTextView = findViewById(R.id.post_details_date_textView);
        mDescriptionTextView = findViewById(R.id.post_details_description_textView);
        mPaymentOptionsTextView = findViewById(R.id.post_details_payment_options_content_textView);
        mEmailButton = findViewById(R.id.post_details_send_email_button);
        mWhatsAppButton = findViewById(R.id.post_details_whatsapp_button);
    }

    private void getPostFromDB() {
        db = FirebaseFirestore.getInstance();
        mPostID = getIntent().getStringExtra(UtilityActivity.POST_ID);
        ref = db.collection(UtilityActivity.COLLECTION_POSTS).document(mPostID);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "getting post succeeded: " + document.getData());
                        post = document.toObject(Post.class);
                        setData();
                    }
                    else {
                        Log.e(TAG, "no such document");
                        showSnackbar("Something went wrong. The post is not in our database.");
                    }
                }
                else {
                    Log.e(TAG, "getting post failed: ", task.getException());
                }
            }
        });
    }

    private void setData() {
        storage = FirebaseStorage.getInstance();
        GlideApp.with(this)
                .load(storage.getReferenceFromUrl(post.getImageUrl()))
                .into(mImageView);
        mTitleTextView.setText(post.getTitle());
        mPriceTextView.setText(post.getPrice());
        mOwnerNameTextView.setText(post.getOwnerName());
        mCategoryTextView.setText(post.getCategory());
        mConditionTextView.setText(post.getCondition());
        mDateTextView.setText(post.getCreationDate().toString().replaceAll("GMT.02:00 ", "").substring(4));
        mDescriptionTextView.setText(post.getDescription());
        handlePaymentOptions();
        mPaymentOptionsTextView.setText(mPaymentOptions);
        if (post.isWhatsApp()) {
            mWhatsAppButton.setVisibility(View.VISIBLE);
        }
        else {
            mWhatsAppButton.setVisibility(View.INVISIBLE);
        }
    }

    private void handlePaymentOptions() {
        ArrayList<String> arrayList = post.getPaymentOptions();
        if (arrayList.contains(UtilityActivity.CASH)) {
            mPaymentOptions += CASH;
        }
        if (arrayList.contains((UtilityActivity.BANK_TRANSFER))) {
            mPaymentOptions += BANK_TRANSFER;
        }
        if (arrayList.contains(UtilityActivity.PAYPAL)) {
            mPaymentOptions += PAYPAL;
        }
        if (arrayList.contains((UtilityActivity.MEAL_PLAN))) {
            mPaymentOptions += MEAL_PLAN;
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
