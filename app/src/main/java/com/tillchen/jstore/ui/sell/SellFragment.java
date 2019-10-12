package com.tillchen.jstore.ui.sell;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tillchen.jstore.MainActivity;
import com.tillchen.jstore.R;

public class SellFragment extends Fragment {

    private EditText mTitleEditText;
    private Spinner mCategorySpinner;
    private Spinner mConditionSpinner;
    private EditText mDescriptionEditText;
    private Button mAddPhotosButton;
    private EditText mPriceEditText;
    private CheckBox mCashCheckBox;
    private CheckBox mBankTransferCheckBox;
    private CheckBox mPayPalCheckBox;
    private CheckBox mMealPlanCheckBox;
    private Button mFinishButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sell, container, false);

        findViews(root);

        mTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ((MainActivity) getActivity()).hideKeyboard(view);
                }
            }
        });

        mDescriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ((MainActivity) getActivity()).hideKeyboard(view);
                }
            }
        });

        mPriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ((MainActivity) getActivity()).hideKeyboard(view);
                }
            }
        });

        return root;
    }

    private void findViews(View root) {
        mTitleEditText = root.findViewById(R.id.title_editText);
        mCategorySpinner = root.findViewById(R.id.category_spinner);
        mConditionSpinner = root.findViewById(R.id.condition_spinner);
        mDescriptionEditText = root.findViewById(R.id.description_editText);
        mAddPhotosButton = root.findViewById(R.id.add_photos_button);
        mPriceEditText = root.findViewById(R.id.price_editText);
        mCashCheckBox = root.findViewById(R.id.cash_checkBox);
        mBankTransferCheckBox = root.findViewById(R.id.bank_transfer_checkBox);
        mPayPalCheckBox = root.findViewById(R.id.paypal_checkBox);
        mMealPlanCheckBox = root.findViewById(R.id.meal_plan_checkBox);
        mFinishButton = root.findViewById(R.id.finish_button);
    }
}