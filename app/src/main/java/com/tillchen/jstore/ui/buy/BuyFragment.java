package com.tillchen.jstore.ui.buy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.tillchen.jstore.R;

public class BuyFragment extends Fragment {

    private Toolbar mToolbar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_buy, container, false);

        mToolbar = root.findViewById(R.id.buy_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);


        return root;
    }
}