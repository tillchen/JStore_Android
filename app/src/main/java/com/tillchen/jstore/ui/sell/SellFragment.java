package com.tillchen.jstore.ui.sell;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tillchen.jstore.MainActivity;
import com.tillchen.jstore.R;
import com.tillchen.jstore.UtilityActivity;
import com.tillchen.jstore.models.Post;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.tillchen.jstore.UtilityActivity.COLLECTION_POSTS;
import static com.tillchen.jstore.UtilityActivity.REQUEST_IMAGE_CAPTURE;
import static com.tillchen.jstore.UtilityActivity.REQUEST_PICK_IMAGE;

public class SellFragment extends Fragment implements View.OnClickListener {

    // TODO: 0 Restore the entered info when switching back

    private static final String TAG = "SellFragment";

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private StorageReference mFileReference;
    private FirebaseFirestore db;

    private Toolbar mToolbar;
    private EditText mTitleEditText;
    private Spinner mCategorySpinner;
    private Spinner mConditionSpinner;
    private EditText mDescriptionEditText;
    private Button mTakePhotoButton;
    private Button mAddPhotoButton;
    private EditText mPriceEditText;
    private CheckBox mCashCheckBox;
    private CheckBox mBankTransferCheckBox;
    private CheckBox mPayPalCheckBox;
    private CheckBox mMealPlanCheckBox;
    private Button mFinishButton;
    private TextView mPhotoUploadedTextView;

    private String mTitle;
    private String mDescription;
    private String mPrice;
    private String mCategory;
    private String mCondition;
    private ArrayList<String> mPaymentOptions;
    private String mCurrentPhotoPath;
    private boolean isImageUploaded = false;
    private String mFileName;
    private Uri mChosenPhotoPath;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageReference = mStorage.getReference().child(UtilityActivity.STORAGE_POSTS);
        isImageUploaded = false;

        if (mUser.isAnonymous()) {
            return inflater.inflate(R.layout.fragment_anonymous_sell, container, false);
        }

        View root = inflater.inflate(R.layout.fragment_sell, container, false);

        findViews(root);

        setVisibility();

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
        mTakePhotoButton = root.findViewById(R.id.take_photo_button);
        mAddPhotoButton = root.findViewById(R.id.add_photo_button);
        mPriceEditText = root.findViewById(R.id.price_editText);
        mCashCheckBox = root.findViewById(R.id.cash_checkBox);
        mBankTransferCheckBox = root.findViewById(R.id.bank_transfer_checkBox);
        mPayPalCheckBox = root.findViewById(R.id.paypal_checkBox);
        mMealPlanCheckBox = root.findViewById(R.id.meal_plan_checkBox);
        mFinishButton = root.findViewById(R.id.finish_button);
        mPhotoUploadedTextView = root.findViewById(R.id.photo_uploaded_textView);
        mPaymentOptions = new ArrayList<String>();
    }

    private void setVisibility() {
        if (isImageUploaded) {
            mPhotoUploadedTextView.setVisibility(View.VISIBLE);
        }
        else {
            mPhotoUploadedTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setListeners() {

        mTakePhotoButton.setOnClickListener(this);
        mAddPhotoButton.setOnClickListener(this);
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
                mCategory = mCategorySpinner.getSelectedItem().toString();
                mCondition = mConditionSpinner.getSelectedItem().toString();


                if (mCashCheckBox.isChecked()) {
                    mPaymentOptions.add(getResources().getString(R.string.cash));
                }
                if (mBankTransferCheckBox.isChecked()) {
                    mPaymentOptions.add(getResources().getString(R.string.bank_transfer));
                }
                if (mPayPalCheckBox.isChecked()) {
                    mPaymentOptions.add(getResources().getString(R.string.PayPal));
                }
                if (mMealPlanCheckBox.isChecked()) {
                    mPaymentOptions.add(getResources().getString(R.string.meal_plan));
                }

                if (TextUtils.isEmpty(mTitle)) {
                    mTitleEditText.setError("Title can't be empty.");
                    break;
                }

                if (TextUtils.isEmpty(mDescription)) {
                    mDescriptionEditText.setError("Description can't be empty");
                    break;
                }

                if (TextUtils.isEmpty(mPrice)) {
                    mPriceEditText.setError("Price can't be empty");
                    break;
                }

                if (mPaymentOptions.size() == 0) {
                    showSnackbar("You must choose at least 1 payment option.");
                    break;
                }

                if (!isImageUploaded) {
                    showSnackbar("You must upload a photo or wait until the uploading is finished.");
                    break;
                }

                Post post = new Post(mUser.getEmail(),mTitle, mCategory, mCondition, mDescription,
                        mFileReference.getDownloadUrl().toString(), mPrice, mPaymentOptions);
                db.collection(COLLECTION_POSTS).document(mFileName).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "post written into DB: " + mFileName);
                        showSnackbar("Posted!");
                        // TODO: 0 Reset the fragment
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "post writing failed: " + mFileName, e);
                        showSnackbar("Sorry! Some error occurred. Please try again.");
                    }
                });
                break;

            case R.id.take_photo_button:

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.i(TAG, "Error when creating the image file: ", ex);
                        showSnackbar("Error when creating the image file. Please take another photo.");
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.tillchen.jstore.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File f = new File(mCurrentPhotoPath);
                        Uri contentUri = Uri.fromFile(f);
                        mediaScanIntent.setData(contentUri);
                        getActivity().sendBroadcast(mediaScanIntent);
                    }
                }
                else {
                    showSnackbar("You must have a camera app to take a photo. Try ADD PHOTO instead.");
                }
                break;

            case R.id.add_photo_button:
                Intent chooseImageIntent = new Intent();
                chooseImageIntent.setType("image/*");
                chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(chooseImageIntent, "Select a Picture"), REQUEST_PICK_IMAGE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                deleteOldImage();
                Uri file = Uri.fromFile(new File(mCurrentPhotoPath));
                uploadImage(file);
            }
        }
        else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                deleteOldImage();
                mChosenPhotoPath = data.getData();
                uploadImage(mChosenPhotoPath);
            }
        }
    }

    private void uploadImage(Uri file) {
        mFileName = UUID.randomUUID().toString();
        mFileReference = mStorageReference.child(mFileName);
        // TODO: 1 Add a progress bar
        mFileReference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "uploadImage succeeded: " + taskSnapshot.getMetadata().getName());
                isImageUploaded = true;
                mPhotoUploadedTextView.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "uploadImage failed: ", e);
                isImageUploaded = false;
                mPhotoUploadedTextView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void deleteOldImage() {
        if (isImageUploaded && !TextUtils.isEmpty(mFileName)) {
            mStorageReference.child(mFileName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "deleteOldImage succeeded: " + mFileName);
                    isImageUploaded = false;
                    mPhotoUploadedTextView.setVisibility(View.INVISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "deleteOldImage failed: " + mFileName, e);
                    isImageUploaded = true;
                    mPhotoUploadedTextView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showSnackbar(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

}