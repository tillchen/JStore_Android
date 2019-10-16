package com.tillchen.jstore.ui.sell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tillchen.jstore.MainActivity;
import com.tillchen.jstore.R;
import com.tillchen.jstore.UtilityActivity;
import com.tillchen.jstore.models.Post;
import com.tillchen.jstore.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.tillchen.jstore.UtilityActivity.AUTHORITY;
import static com.tillchen.jstore.UtilityActivity.BANK_TRANSFER;
import static com.tillchen.jstore.UtilityActivity.CASH;
import static com.tillchen.jstore.UtilityActivity.COLLECTION_POSTS;
import static com.tillchen.jstore.UtilityActivity.COLLECTION_USERS;
import static com.tillchen.jstore.UtilityActivity.MEAL_PLAN;
import static com.tillchen.jstore.UtilityActivity.PAYPAL;
import static com.tillchen.jstore.UtilityActivity.REQUEST_IMAGE_CAPTURE;
import static com.tillchen.jstore.UtilityActivity.REQUEST_PICK_IMAGE;

public class SellFragment extends Fragment implements View.OnClickListener {

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
    private ProgressBar mUploadProgressBar;
    private ProgressBar mFinishProgressBar;

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
    private String mDownloadUrl;
    private boolean isReadyToFinish = false;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        initFirebase();

        isImageUploaded = false;
        isReadyToFinish = false;

        if (mUser.isAnonymous()) {
            return inflater.inflate(R.layout.fragment_anonymous_sell, container, false);
        }

        View root = inflater.inflate(R.layout.fragment_sell, container, false);

        findViews(root);

        setVisibility();

        setListeners();

        getUserFromDB();

        return root;
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        mStorageReference = mStorage.getReference().child(UtilityActivity.STORAGE_POSTS);
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
        mUploadProgressBar = root.findViewById(R.id.upload_progressBar);
        mFinishProgressBar = root.findViewById(R.id.sell_finish_progressBar);

    }

    private void setVisibility() {
        mFinishProgressBar.setVisibility(View.INVISIBLE);
        if (isImageUploaded) {
            mPhotoUploadedTextView.setVisibility(View.VISIBLE);
            mUploadProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            mPhotoUploadedTextView.setVisibility(View.INVISIBLE);
            mUploadProgressBar.setVisibility(View.INVISIBLE);
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

                getAndSetData();

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
                if (!isReadyToFinish) {
                    showSnackbar("Sorry some tasks are not finished. Please try again.");
                    break;
                }

                postItem();

                break;

            case R.id.take_photo_button:

                takePhoto();

                break;

            case R.id.add_photo_button:

                addPhoto();

                break;
            default:
                break;
        }
    }

    private void getAndSetData() {
        mTitle = mTitleEditText.getText().toString();
        mDescription = mDescriptionEditText.getText().toString();
        mPrice = mPriceEditText.getText().toString();
        mCategory = mCategorySpinner.getSelectedItem().toString();
        mCondition = mConditionSpinner.getSelectedItem().toString();


        if (mCashCheckBox.isChecked() && !mPaymentOptions.contains(CASH)) {
            mPaymentOptions.add(CASH);
        }
        if (mBankTransferCheckBox.isChecked() && !mPaymentOptions.contains(BANK_TRANSFER)) {
            mPaymentOptions.add(BANK_TRANSFER);
        }
        if (mPayPalCheckBox.isChecked() && !mPaymentOptions.contains(PAYPAL)) {
            mPaymentOptions.add(PAYPAL);
        }
        if (mMealPlanCheckBox.isChecked() && !mPaymentOptions.contains(MEAL_PLAN)) {
            mPaymentOptions.add(MEAL_PLAN);
        }

    }

    private void postItem() {
        Post post = new Post(mUser.getEmail(), user.getFullName(), user.isWhatsApp(), user.getPhoneNumber(),
                mTitle, mCategory, mCondition, mDescription, mDownloadUrl, mPrice, mPaymentOptions);

        mFinishProgressBar.setVisibility(View.VISIBLE);

        db.collection(COLLECTION_POSTS).document(mFileName).set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "post written into DB: " + mFileName);
                        mFinishProgressBar.setVisibility(View.INVISIBLE);
                        showSnackbar("Posted!");
                        reloadFragment();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "post writing failed: " + mFileName, e);
                mFinishProgressBar.setVisibility(View.INVISIBLE);
                showSnackbar("Sorry! Some error occurred. Please try again.");
            }
        });
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Error when creating the image file: ", ex);
                showSnackbar("Error when creating the image file. Please take another photo.");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI;
                if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)) {
                    photoURI = FileProvider.getUriForFile(getActivity(),
                            AUTHORITY,
                            photoFile);
                }
                else {
                    photoURI = Uri.fromFile(photoFile);
                }
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
    }

    private void addPhoto() {
        Intent chooseImageIntent = new Intent();
        chooseImageIntent.setType("image/*");
        chooseImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(chooseImageIntent, "Select a Picture"), REQUEST_PICK_IMAGE);
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

        byte[] data = new byte[0];

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), file);

            ExifInterface exif = new ExifInterface(getActivity().getContentResolver().openInputStream(file));

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateBitmap(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateBitmap(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateBitmap(bitmap, 270);
                    break;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);
            data = out.toByteArray();
        }
        catch (FileNotFoundException fex) {
            Log.e(TAG, "File not found for: " + file, fex);
            showSnackbar("Error: File not found. Please choose another photo.");
        }
        catch (IOException iex) {
            Log.e(TAG, "uploadImage error occurred: ", iex);
            showSnackbar("Error: Something went wrong. Please choose another photo.");
        }

        if (data.length == 0) {
            Log.e(TAG, "uploadImage Error: empty data");
            showSnackbar("Error: Empty file. Please choose another photo.");
            return;
        }

        mFileName = UUID.randomUUID().toString();
        mFileReference = mStorageReference.child(mFileName);
        mFileReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "uploadImage succeeded: " + taskSnapshot.getMetadata().getName());
                isImageUploaded = true;
                mPhotoUploadedTextView.setVisibility(View.VISIBLE);
                mFileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mDownloadUrl = uri.toString();
                        Log.i(TAG, "getDownloadUrl succeeded: " + mDownloadUrl);
                        isReadyToFinish = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getDownloadUrl failed: " + mFileName, e);
                        showSnackbar("Sorry! Some error occurred. Please try again.");
                        isReadyToFinish = false;
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "uploadImage failed: ", e);
                isImageUploaded = false;
                mPhotoUploadedTextView.setVisibility(View.INVISIBLE);
                showSnackbar("Image uploading failed. Please upload again.");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                mUploadProgressBar.setVisibility(View.VISIBLE);
                int progress = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                mUploadProgressBar.setProgress(progress);
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
                    setVisibility();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "deleteOldImage failed: " + mFileName, e);
                    isImageUploaded = true;
                    setVisibility();
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

    private void getUserFromDB() {
        db.collection(COLLECTION_USERS).document(mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i(TAG, "getting user succeeded: " + document.getData());
                        user = document.toObject(User.class);
                        isReadyToFinish = true;
                    }
                    else {
                        Log.e(TAG, "no such document");
                        showSnackbar("Something went wrong. You are not in our database. Please sign in again.");
                        isReadyToFinish = false;
                    }
                }
                else {
                    Log.e(TAG, "getting user failed: ", task.getException());
                }
            }
        });
    }

    private void reloadFragment() {
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_sell);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void showSnackbar(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

}