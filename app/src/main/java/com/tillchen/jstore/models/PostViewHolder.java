package com.tillchen.jstore.models;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.tillchen.jstore.MainActivity;
import com.tillchen.jstore.PostDetailsActivity;
import com.tillchen.jstore.R;
import com.tillchen.jstore.UtilityActivity;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "PostViewHolder";

    FirebaseStorage mStorage;

    private String postId;

    private ImageView mItemImageView;
    private TextView mItemTitleTextView;
    private TextView mItemOwnerTextView;
    private TextView mItemPriceTextView;
    private TextView mItemCategoryTextView;
    private TextView mItemPostDateTextView;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        mItemImageView = itemView.findViewById(R.id.item_imageView);
        mItemTitleTextView = itemView.findViewById(R.id.item_title_textView);
        mItemOwnerTextView = itemView.findViewById(R.id.item_owner_textView);
        mItemPriceTextView = itemView.findViewById(R.id.item_price_textView);
        mItemCategoryTextView = itemView.findViewById(R.id.item_category_textView);
        mItemPostDateTextView = itemView.findViewById(R.id.item_post_date_textView);
    }

    public void bind(Post post) {
        mStorage = FirebaseStorage.getInstance();
        mItemTitleTextView.setText(post.getTitle());
        mItemOwnerTextView.setText(itemView.getContext().getResources().getString(R.string.by) + post.getOwnerName());
        mItemPriceTextView.setText(itemView.getContext().getResources().getString(R.string.euro_sign) + post.getPrice());
        mItemCategoryTextView.setText(post.getCategory());
        mItemPostDateTextView.setText(itemView.getContext().getResources().getString(R.string.posted_at)+
                post.getCreationDate().toString().replaceAll("GMT.02:00 ", "").substring(4));
        GlideApp.with(itemView)
                .load(mStorage.getReferenceFromUrl(post.getImageUrl()))
                .into(mItemImageView);
        postId = post.getPostId();
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Item clicked: " + postId);
                Intent intent = new Intent(itemView.getContext(), PostDetailsActivity.class);
                intent.putExtra(UtilityActivity.POST_ID, postId);
                itemView.getContext().startActivity(intent);
            }
        });
    }


}
