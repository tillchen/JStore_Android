package com.tillchen.jstore.ui.sell;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tillchen.jstore.MainActivity;
import com.tillchen.jstore.R;

public class SellFragment extends Fragment implements View.OnClickListener {

    // TODO: 0 Restore the entered info when switching back

    private static final int TITLE_LENGTH_LIMIT = 50;
    private static final int DESCRIPTION_LENGTH_LIMIT = 300;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Toolbar mToolbar;
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

    private String mTitle;
    private String mDescription;
    private String mPrice;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser.isAnonymous()) {
            return inflater.inflate(R.layout.fragment_anonymous_sell, container, false);
        }

        View root = inflater.inflate(R.layout.fragment_sell, container, false);

        findViews(root);

        setListeners();

        return root;
    }

    private void findViews(View root) {
        mToolbar = root.findViewById(R.id.sell_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
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

    private void setListeners() {

        mAddPhotosButton.setOnClickListener(this);
        mFinishButton.setOnClickListener(this);
        mCashCheckBox.setOnClickListener(this);
        mBankTransferCheckBox.setOnClickListener(this);
        mPayPalCheckBox.setOnClickListener(this);
        mMealPlanCheckBox.setOnClickListener(this);

        mTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    ((MainActivity) getActivity()).hideKeyboard(view);
                }
            }
        });

        mDescriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    ((MainActivity) getActivity()).hideKeyboard(view);
                }
            }
        });

        mPriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    ((MainActivity) getActivity()).hideKeyboard(view);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_button:

                mTitle = mTitleEditText.getText().toString();
                mDescription = mDescriptionEditText.getText().toString();
                mPrice = mPriceEditText.getText().toString();

                if (TextUtils.isEmpty(mTitle)) {
                    mTitleEditText.setError("Title can't be empty.");
                }
                else if (mTitle.length() > TITLE_LENGTH_LIMIT) {
                    mTitleEditText.setError("Title can't contain more than 50 characters.");
                }

                if (TextUtils.isEmpty(mDescription)) {
                    mDescriptionEditText.setError("Description can't be empty");
                }
                else if (mDescription.length() > DESCRIPTION_LENGTH_LIMIT) {
                    mDescriptionEditText.setError("Description can't contain more than 300 characters.");
                }

                if (TextUtils.isEmpty(mPrice)) {
                    mPriceEditText.setError("Price can't be empty");
                }

                break;
            default:
                break;
        }
    }


}