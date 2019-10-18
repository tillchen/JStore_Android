package com.tillchen.jstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.tillchen.jstore.models.GlideApp;
import com.tillchen.jstore.models.Post;
import com.tillchen.jstore.models.User;

import java.util.ArrayList;
import java.util.List;


public class PostDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO: 0 Different functions if the user is the owner

    private static final String TAG = "PostDetailsActivity";

    public static final String CASH = "Cash ";
    public static final String BANK_TRANSFER = "Bank Transfer ";
    public static final String PAYPAL = "PayPal ";
    public static final String MEAL_PLAN = "Meal Plan ";

    FirebaseFirestore db;
    DocumentReference ref;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseUser user;

    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mPriceTextView;
    private TextView mOwnerNameTextView;
    private TextView mEmailTextView;
    private TextView mCategoryTextView;
    private TextView mConditionTextView;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mPaymentOptionsTextView;
    private Button mEmailButton;
    private Button mWhatsAppButton;

    private String mPostID;
    private String mPaymentOptions = "";

    private Post post;
    private User mUser;
    private boolean gotUser = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        findViews();

        getPostFromDB();

        if (!user.isAnonymous()) {
            getUserFromDB();
        }

    }

    private void findViews() {
        mImageView = findViewById(R.id.post_details_imageView);
        mTitleTextView = findViewById(R.id.post_details_title_textView);
        mPriceTextView = findViewById(R.id.post_details_price_textView);
        mOwnerNameTextView = findViewById(R.id.post_details_owner_name_textView);
        mEmailTextView = findViewById(R.id.post_details_email_content_textView);
        mCategoryTextView = findViewById(R.id.post_details_category_content_textView);
        mConditionTextView = findViewById(R.id.post_details_condition_content_textView);
        mDateTextView = findViewById(R.id.post_details_date_textView);
        mDescriptionTextView = findViewById(R.id.post_details_description_textView);
        mPaymentOptionsTextView = findViewById(R.id.post_details_payment_options_content_textView);
        mEmailButton = findViewById(R.id.post_details_send_email_button);
        mWhatsAppButton = findViewById(R.id.post_details_whatsapp_button);

        mEmailButton.setOnClickListener(this);
        mWhatsAppButton.setOnClickListener(this);
    }

    private String checkIntent() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            if (uri.toString().contains("posts")) {
                return uri.getLastPathSegment();
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    private void getPostFromDB() {

        String result = checkIntent();
        if (TextUtils.isEmpty(result)) {
            mPostID = getIntent().getStringExtra(UtilityActivity.POST_ID);
        }
        else {
            mPostID = result;
        }
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

    private  void getUserFromDB() {
        db.collection(UtilityActivity.COLLECTION_USERS).document(user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "getting user succeeded: " + document.getData());
                        mUser = document.toObject(User.class);
                        gotUser = true;
                    }
                    else {
                        Log.e(TAG, "no such document");
                        showSnackbar("Something went wrong. You are not in our database.");
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
        mPriceTextView.setText(getResources().getString(R.string.euro_sign) + post.getPrice());
        mOwnerNameTextView.setText(post.getOwnerName());
        mEmailTextView.setText(post.getOwnerId());
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
            mPaymentOptions += CASH + "    ";
        }
        if (arrayList.contains((UtilityActivity.BANK_TRANSFER))) {
            mPaymentOptions += BANK_TRANSFER + "    ";
        }
        if (arrayList.contains(UtilityActivity.PAYPAL)) {
            mPaymentOptions += PAYPAL + "    ";
        }
        if (arrayList.contains((UtilityActivity.MEAL_PLAN))) {
            mPaymentOptions += MEAL_PLAN + "    ";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_details_send_email_button:

                if(!user.isAnonymous() && !gotUser) {
                    showSnackbar("Something went wrong. Please try again.");
                    break;
                }

                sendEmail();

                break;

            case R.id.post_details_whatsapp_button:

                if(!user.isAnonymous() && !gotUser) {
                    showSnackbar("Something went wrong. Please try again.");
                    break;
                }

                textOnWhatsApp();

                break;

            default:
                break;
        }
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {post.getOwnerId()}); // recipients
        String title = "[JStore] " + post.getTitle();
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        String text = "Dear " + post.getOwnerName() + ",\n\n Hi! I'm contacting you by clicking on the" +
                " Email button of JStore. I'm interested in the following item:\nhttps://jstore.xyz/posts/"
                + post.getPostId() + "\n\nSincerely,\n\n" + (user.isAnonymous() ? "" : mUser.getFullName());
        intent.putExtra(Intent.EXTRA_TEXT, text);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (!isIntentSafe) {
            showSnackbar("Sorry. You don't have an Email app.");
            return;
        }

        startActivity(intent);
    }

    private void textOnWhatsApp() {
        String text = "[JStore] " + post.getTitle() + "\n\nHi! I'm contacting you by clicking on the " +
                "WhatsApp button of JStore. My name is " + (user.isAnonymous() ? "" : mUser.getFullName()) +
                " and I'm interested in the following item:\nhttps://jstore.xyz/posts/" + post.getPostId();
        String encodedText = Uri.encode(text);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + post.getPhoneNumber()
                + "?text=" + encodedText));
        startActivity(intent);
    }


    public void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
