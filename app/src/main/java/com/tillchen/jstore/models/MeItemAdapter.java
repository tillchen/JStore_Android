package com.tillchen.jstore.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tillchen.jstore.R;

import java.util.ArrayList;

public class MeItemAdapter extends ArrayAdapter<MeItem> {

    public MeItemAdapter(Context context, ArrayList<MeItem> meItems) {
        super(context, 0, meItems);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        MeItem meItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_me, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.item_me_imageView);
        TextView textView = convertView.findViewById(R.id.item_me_textView);
        imageView.setImageResource(meItem.getImage());
        textView.setText(meItem.getTitle());
        return convertView;
    }

}
