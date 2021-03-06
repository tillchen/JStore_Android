package com.tillchen.jstore.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tillchen.jstore.ActivePostsActivity;
import com.tillchen.jstore.R;
import com.tillchen.jstore.SoldItemsActivity;

import java.util.ArrayList;

public class MeItemAdapter extends ArrayAdapter<MeItem> {

    public static final int ACTIVE_POSTS = 0;
    public static final int SOLD_ITEMS = 1;
    public static final int NOTIFICATION_SETTINGS = 2;

    public MeItemAdapter(Context context, ArrayList<MeItem> meItems) {
        super(context, 0, meItems);
    }

    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        MeItem meItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_me, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.item_me_imageView);
        TextView textView = convertView.findViewById(R.id.item_me_textView);
        imageView.setImageResource(meItem.getImage());
        textView.setText(meItem.getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position) {
                    case ACTIVE_POSTS:
                        Intent intent = new Intent(getContext(), ActivePostsActivity.class);
                        getContext().startActivity(intent);
                        break;
                    case SOLD_ITEMS:
                        Intent intent1 = new Intent(getContext(), SoldItemsActivity.class);
                        getContext().startActivity(intent1);
                        break;
                    case NOTIFICATION_SETTINGS: // TODO: 0
                        Toast.makeText(getContext(), "Feature Coming Soon", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }

            }
        });

        return convertView;
    }

}
