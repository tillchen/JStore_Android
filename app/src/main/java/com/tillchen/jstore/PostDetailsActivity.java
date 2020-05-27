package com.tillchen.jstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.tillchen.jstore.models.GlideApp;
import com.tillchen.jstore.models.Post;
import com.tillchen.jstore.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostDetailsActivity extends UtilityActivity implements View.OnClickListener {
    // TODO: 0 Enable editing

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
    private TextView mSoldAtTextView;
    private TextView mSoldAtContentTextView;
    private Button mEmailButton;
    private Button mWhatsAppButton;
    private ProgressBar mProgressBar;
    private Button mMarkAsSoldButton;
    private Button mDeletePostButton;

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
        mProgressBar = findViewById(R.id.post_details_progressBar);
        mProgressBar.setVisibility(View.GONE);
        mMarkAsSoldButton = findViewById(R.id.post_details_mark_as_sold_button);
        mDeletePostButton = findViewById(R.id.post_details_delete_post_button);
        mSoldAtTextView = findViewById(R.id.post_details_sold_at_textView);
        mSoldAtContentTextView = findViewById(R.id.post_details_sold_at_content_textView);

        mEmailButton.setOnClickListener(this);
        mWhatsAppButton.setOnClickListener(this);
        mMarkAsSoldButton.setOnClickListener(this);
        mDeletePostButton.setOnClickListener(this);
    }

    private void setOwnerVisibility() {
        if (post.isSold()) {
            mMarkAsSoldButton.setEnabled(false);
        }
        mMarkAsSoldButton.setVisibility(View.VISIBLE);
        mDeletePostButton.setVisibility(View.VISIBLE);
        mEmailButton.setVisibility(View.GONE);
        mWhatsAppButton.setVisibility(View.GONE);
    }

    private void setNonOwnerVisibility() {
        mMarkAsSoldButton.setVisibility(View.GONE);
        mDeletePostButton.setVisibility(View.GONE);
        mEmailButton.setVisibility(View.VISIBLE);
        mWhatsAppButton.setVisibility(View.VISIBLE);
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
        mProgressBar.setVisibility(View.VISIBLE);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "getting post succeeded: " + document.getData());
                        post = document.toObject(Post.class);
                        setData();
                    }
                    else {
                        Log.e(TAG, "no such document");
                        showSnackbar("Something went wrong. This post is not in our database. Please go back and refresh.");
                        mEmailButton.setEnabled(false);
                        mDeletePostButton.setEnabled(false);
                        mMarkAsSoldButton.setEnabled(false);
                        mWhatsAppButton.setEnabled(false);
                    }
                }
                else {
                    Log.e(TAG, "getting post failed: ", task.getException());
                }
            }
        });
    }

    private  void getUserFromDB() {
        mProgressBar.setVisibility(View.VISIBLE);
        db.collection(UtilityActivity.COLLECTION_USERS).document(user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mProgressBar.setVisibility(View.GONE);
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
        if (!TextUtils.isEmpty(post.getImageUrl())) {
            GlideApp.with(this)
                    .load(storage.getReferenceFromUrl(post.getImageUrl()))
                    .into(mImageView);
        }
        mTitleTextView.setText(post.getTitle());
        mPriceTextView.setText(getResources().getString(R.string.euro_sign) + post.getPrice());
        mOwnerNameTextView.setText(post.getOwnerName());
        mEmailTextView.setText(post.getOwnerId());
        mCategoryTextView.setText(post.getCategory());
        mConditionTextView.setText(post.getCondition());
        mDateTextView.setText(post.getCreationDate().toString().replaceAll("GMT.0.:00 ", "").substring(4));
        mDescriptionTextView.setText(post.getDescription());
        handlePaymentOptions();
        mPaymentOptionsTextView.setText(mPaymentOptions);
        if (post.isSold()) {
            mSoldAtTextView.setVisibility(View.VISIBLE);
            mSoldAtContentTextView.setVisibility(View.VISIBLE);
            mSoldAtContentTextView.setText(post.getSoldDate().toString().replaceAll("GMT.0.:00 ", "").substring(4));
        }
        else {
            mSoldAtTextView.setVisibility(View.INVISIBLE);
            mSoldAtContentTextView.setVisibility(View.INVISIBLE);
        }
        if (post.isWhatsApp()) {
            mWhatsAppButton.setVisibility(View.VISIBLE);
        }
        else {
            mWhatsAppButton.setVisibility(View.INVISIBLE);
        }
        if (user.isAnonymous()) {
            setNonOwnerVisibility();
        }
        else {
            if (post.getOwnerId().equals(user.getEmail())) {
                setOwnerVisibility();
            }
            else {
                setNonOwnerVisibility();
            }
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
            mPaymentOptions += MEAL_PLAN;
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

            case R.id.post_details_mark_as_sold_button:

                onSoldClicked();

                break;

            case R.id.post_details_delete_post_button:

                onDeleteClicked();

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

    private void onSoldClicked() {
        Log.i(TAG, "onSoldClicked");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_you_sure_mark_as_sold);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateSold();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void updateSold() {
        Map<String, Object> updates = new HashMap<>();
        updates.put(SOLD, true);
        updates.put(SOLD_DATE, FieldValue.serverTimestamp());

        mProgressBar.setVisibility(View.VISIBLE);
        db.collection(COLLECTION_POSTS).document(post.getPostId())
                .update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Log.i(TAG, "updateSold succeeded: " + post.getPostId());
                    showSnackbar("Marked as Sold!");
                    finish();
                }
                else {
                    Log.e(TAG, "updateSold failed: ", task.getException());
                    showSnackbar("Something went wrong. Please try again.");
                }
            }
        });
    }

    private void onDeleteClicked() {
        Log.i(TAG, "onDeleteClicked");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.are_you_sure_delete).setMessage(R.string.irreversible);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePost();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void deletePost() {
        mProgressBar.setVisibility(View.VISIBLE);
        db.collection(COLLECTION_POSTS).document(post.getPostId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProgressBar.setVisibility(View.GONE);
                Log.i(TAG, "deletePost succeeded: " + post.getPostId());
                deletePicture();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "deletePost failed: " + post.getPostId(), e);
                showSnackbar("Something went wrong. Please try again.");
            }
        });
    }

    private void deletePicture() {
        storage.getReference().child("posts").child(post.getPostId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "deletePicture succeeded: " + post.getPostId());
                mProgressBar.setVisibility(View.GONE);
                showSnackbar("Deleted!");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "deletePicture failed: " + post.getPostId());
                mProgressBar.setVisibility(View.GONE);
                showSnackbar("Something went wrong. Please try again.");
            }
        });
    }
}
