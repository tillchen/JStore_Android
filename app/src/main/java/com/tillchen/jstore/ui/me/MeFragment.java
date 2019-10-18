package com.tillchen.jstore.ui.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tillchen.jstore.LoginActivity;
import com.tillchen.jstore.MainActivity;
import com.tillchen.jstore.R;
import com.tillchen.jstore.models.MeItem;
import com.tillchen.jstore.models.MeItemAdapter;
import com.tillchen.jstore.models.User;

import java.util.ArrayList;

import static com.tillchen.jstore.UtilityActivity.COLLECTION_USERS;

public class MeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MeFragment";

    private FirebaseUser user;
    private FirebaseFirestore db;

    private Toolbar mAnonymousToolbar;
    private Button mAnonymousSignOutButton;
    private TextView mDateTextView;
    private RadioButton mRadioButton1; // WhatsApp
    private RadioButton mRadioButton2; // Email
    private TextView mPlusSign;
    private EditText mEditTextPhone;
    private EditText mEditTextFullName;
    private Button mSaveButton;
    private Button mSignOutButton;

    private ListView mListView;

    private MeItemAdapter mMeItemAdapter;

    boolean isWhatsApp = false; // whether the user selected WhatsApp
    boolean isEmail = false; // whether the user selected Email

    private User mUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user.isAnonymous()) {
            View root = inflater.inflate(R.layout.fragment_anonymous_me, container, false);
            setAnonymous(root);
            return root;
        }
        else {
            View root = inflater.inflate(R.layout.fragment_me, container, false);
            findViews(root);
            setListeners();
            setListView(root);
            getUserFromDB();
            return root;
        }
    }

    private void findViews(@NonNull View root) {
        mListView = root.findViewById(R.id.me_listView);
        mPlusSign = root.findViewById(R.id.me_plus_sign_textView);
        mEditTextPhone = root.findViewById(R.id.me_phone_editText);
        mEditTextFullName = root.findViewById(R.id.me_full_name_editText);
        mRadioButton1 = root.findViewById(R.id.me_WhatsApp_radioButton);
        mRadioButton2 = root.findViewById(R.id.me_email_radioButton);
        mSaveButton = root.findViewById(R.id.me_save_button);
        mSignOutButton = root.findViewById(R.id.me_sign_out_button);
    }

    private void getUserFromDB() {
        db.collection(COLLECTION_USERS).document(user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "getting user succeeded: " + document.getData());
                        mUser = document.toObject(User.class);
                        setData();
                    }
                    else {
                        Log.e(TAG, "no such document");
                        showSnackbar("Something went wrong. You are not in our database. Please sign in again.");
                    }
                }
                else {
                    Log.e(TAG, "getting user failed: ", task.getException());
                    showSnackbar("Something went wrong. Please try again later.");
                }
            }
        });
    }

    private void setVisible() {
        mPlusSign.setVisibility(View.VISIBLE);
        mEditTextPhone.setVisibility(View.VISIBLE);
    }

    private void setInvisible() {
        mPlusSign.setVisibility(View.INVISIBLE);
        mEditTextPhone.setVisibility(View.INVISIBLE);
    }

    private void setListeners() {
        mRadioButton1.setOnClickListener(this);
        mRadioButton2.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        mEditTextFullName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((MainActivity)getActivity()).hideKeyboard(v);
                }
            }
        });


        mEditTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((MainActivity)getActivity()).hideKeyboard(v);
                }
            }
        });

    }

    private void setListView(View root) {
        ArrayList<MeItem> arrayList = new ArrayList<MeItem>();
        mMeItemAdapter = new MeItemAdapter(getActivity(), arrayList);
        mListView.setAdapter(mMeItemAdapter);
        MeItem meItem1 = new MeItem(getResources().getString(R.string.active_posts), R.drawable.posts);
        MeItem meItem2 = new MeItem(getResources().getString(R.string.sold_items), R.drawable.euro);
        MeItem meItem3 = new MeItem(getResources().getString(R.string.notification_settings), R.drawable.notifications);
        mMeItemAdapter.add(meItem1);
        mMeItemAdapter.add(meItem2);
        mMeItemAdapter.add(meItem3);
    }

    private void setData() {
        mEditTextFullName.setText(mUser.getFullName());
        if (mUser.isWhatsApp()) {
            setVisible();
            mRadioButton1.setChecked(true);
            mRadioButton2.setChecked(false);
            mEditTextPhone.setText(mUser.getPhoneNumber());
        }
        else {
            setInvisible();
            mRadioButton2.setChecked(true);
            mRadioButton1.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.me_WhatsApp_radioButton:
                if (((RadioButton) v).isChecked()) {
                    setVisible();
                    isWhatsApp = true;
                    isEmail = false;
                }
                break;

            case R.id.me_email_radioButton:
                if (((RadioButton) v).isChecked()) {
                    setInvisible();
                    isWhatsApp = false;
                    isEmail = true;
                }
                break;
            default:
                break;
        }
    }

    private void setAnonymous(@NonNull View root) {
        mAnonymousToolbar = root.findViewById(R.id.anonymous_me_toolbar);
        mAnonymousSignOutButton = root.findViewById(R.id.anonymous_sign_out_button);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mAnonymousToolbar);
        mAnonymousSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "AnonymousSignOutButton clicked. Signing out.");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Log.i(TAG, "starting LoginActivity");
                startActivity(intent);
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