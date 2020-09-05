package com.example.gopal.quikrdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gopal.quikrdemo.adapter.ImageAdapter;
import com.example.gopal.quikrdemo.pojo.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "ProductActivity";
    private EditText mProductNameEditText;
    private EditText mProductPriceEditText;
    private EditText mProductDescriptionEditText;
    private TextView mAddPhotoTextView, mMaxTextView, mCountTextView;
    private Button mPostButton;
    private Uri mDownloadImageUri;
    private ImageView mUploadImage;
    private Spinner mCategorySpinner;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private static final int RC_IMAGE_PICKER = 543;
    private List<String> productImageList;
    private RecyclerView mImageRecyclerView;
    private ImageAdapter mImageAdapter;
    private ProgressDialog mProgressDialog;
    private String mProductCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mProductNameEditText = findViewById(R.id.product_name);
        mProductPriceEditText = findViewById(R.id.product_price);
        mProductDescriptionEditText = findViewById(R.id.product_description);
        mPostButton = findViewById(R.id.post_button);
        mUploadImage = findViewById(R.id.upload_image);
        mCategorySpinner = findViewById(R.id.spinner);
        mImageRecyclerView = findViewById(R.id.image_recycler_view);
        mAddPhotoTextView = findViewById(R.id.add_photo_text_view);
        mMaxTextView = findViewById(R.id.max_text_view);
        mCountTextView = findViewById(R.id.count_text_view);
        getSupportActionBar().setTitle("Post Your Book");

        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("product_images");
        productImageList = new ArrayList<>();

//        Uri uri = Uri.parse("android.resource://com.example.gopal.quikrdemo/drawable/add_image");
//        productImageList.add(uri.toString());

        mImageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mImageAdapter = new ImageAdapter(this, productImageList);
        mImageRecyclerView.setAdapter(mImageAdapter);

        setCategorySpinner();
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = mProductNameEditText.getText().toString();
                String productPrice = mProductPriceEditText.getText().toString();
                String productDescription = mProductDescriptionEditText.getText().toString();
                String productCategory = mProductCategory;

                if (TextUtils.isEmpty(productName)) {
                    mProductNameEditText.setError("Add Product Name");
                    return;
                }
                if (TextUtils.isEmpty(productPrice)) {
                    mProductPriceEditText.setError("Add Product Price");
                    return;
                }
                if (TextUtils.isEmpty(productDescription)) {
                    mProductDescriptionEditText.setError("Add Product Description");
                    return;
                }
                if (productImageList.size() == 0) {
                    Toast.makeText(ProductActivity.this, "Add Product Image", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_name), MODE_PRIVATE);
                String userName = sharedPref.getString(getString(R.string.key_user_name), null);
                String phoneNumber = sharedPref.getString(getString(R.string.key_user_phone_number), null);
                int listedProduct = sharedPref.getInt(getString(R.string.key_count_of_listed_product_by_user), 1005);
                String productId = phoneNumber + listedProduct;

                Product product = new Product(productId, productName, productPrice, productDescription, productCategory, productImageList, userName);
                mFirebaseFirestore.collection("products").add(product);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.key_count_of_listed_product_by_user), listedProduct + 1);
                editor.apply();
                Toast.makeText(ProductActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                finish();


            }
        });
        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, RC_IMAGE_PICKER);
                }
            }
        });

    }

    private void setCategorySpinner() {
        final ArrayList<String> categories = new ArrayList<>();
        categories.add("Book");
        categories.add("Utensil");
        categories.add("Vehichle");
        categories.add("Mobile");
        categories.add("Laptop");
        categories.add("Other stuff");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(dataAdapter);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(ProductActivity.this, "position: " + i, Toast.LENGTH_SHORT).show();
                mProductCategory = categories.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            showProgressDialog("Uploading...");
            Uri selectedImageUri = data.getData();

            /*  Uri aftermath = compressImage_getUri(selectedImageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), aftermath);
                mUploadImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            StorageReference photoRef = mStorageReference.child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(selectedImageUri);
            uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDownloadImageUri = taskSnapshot.getDownloadUrl();
                    productImageList.add(mDownloadImageUri.toString());
                    mImageAdapter.notifyDataSetChanged();
                    if (productImageList.size() == 5) {
                        mUploadImage.setVisibility(View.GONE);
                    }
                    if (productImageList.size() != 0) {
                        if (mAddPhotoTextView.getVisibility() == View.VISIBLE)
                            mAddPhotoTextView.setVisibility(View.GONE);

                        if (mMaxTextView.getVisibility() == View.VISIBLE)
                            mMaxTextView.setVisibility(View.GONE);

                        mCountTextView.setVisibility(View.VISIBLE);
                        String count = "(" + productImageList.size() + "/5) image uploaded";
                        mCountTextView.setText(count);

                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            Toast.makeText(ProductActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }, 1500);

                }

            });
            uploadTask.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e(TAG, "onFailure: ");
                    Toast.makeText(ProductActivity.this, "Couldn't Upload", Toast.LENGTH_SHORT).show();

                }
            });

        }

    }

    private Uri compressImage_getUri(Uri uri){
        Log.e(TAG, "URI before: " + uri);
        Bitmap bitmap = null;
        try { //1  Creating Bitmap from URI
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int mb = 1024*1024;
        Log.e(TAG, "before compress" );
        Log.e(TAG, "onActivityResult: size: " + (bitmap.getByteCount()/mb));
        Log.e(TAG, "onActivityResult: width: " + bitmap.getWidth());
        Log.e(TAG, "onActivityResult: height: " + (bitmap.getHeight()));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Log.e(TAG, "after compress" );
        Log.e(TAG, "onActivityResult: size: " + (bitmap.getByteCount()/mb));
        Log.e(TAG, "onActivityResult: width: " + bitmap.getWidth());
        Log.e(TAG, "onActivityResult: height: " + (bitmap.getHeight()));
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        Log.e(TAG, "URI after: " + Uri.parse(path));
        return Uri.parse(path);
    }

    private void compressImage(String uri){
        Bitmap bitmap = null;
        try { //1  Creating Bitmap from URI
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uri));

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "before compress" );
        Log.e(TAG, "onActivityResult: size: " + (bitmap.getByteCount()));
        Log.e(TAG, "onActivityResult: width: " + bitmap.getWidth());
        Log.e(TAG, "onActivityResult: height: " + (bitmap.getHeight()));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // 2 re-defining the bitmap size
        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
        // 3 Compressing the Bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        //4 Decode the Bitmap
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        // 5 show the compressed Bitmap
//        imageView.setImageBitmap(compressedBitmap);
        Log.e(TAG, "after compress" );
        Log.e(TAG, "onActivityResult: size: " + (compressedBitmap.getByteCount()));
        Log.e(TAG, "onActivityResult: width: " + compressedBitmap.getWidth());
        Log.e(TAG, "onActivityResult: height: " + (compressedBitmap.getHeight()));

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

}
