package com.anurag.emart.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.Constants;
import com.anurag.emart.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView productIconIv;
    private EditText titleEt, descriptionEt, quantityEt, priceEt, discountPriceEt, discountNoteEt;
    private TextView categoryTv;
    private SwitchCompat discountSwitch;
    private Button addProductBtn;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri image_uri = null;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        backBtn = findViewById(R.id.backBtn);
        productIconIv = findViewById(R.id.productIconIv);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        categoryTv = findViewById(R.id.categoryTv);
        quantityEt = findViewById(R.id.quantityEt);
        priceEt = findViewById(R.id.priceEt);
        discountSwitch = findViewById(R.id.discountSwitch);
        discountPriceEt = findViewById(R.id.discountPriceEt);
        discountNoteEt = findViewById(R.id.discountNoteEt);
        addProductBtn = findViewById(R.id.addProductBtn);

        discountPriceEt.setVisibility(View.GONE);
        discountNoteEt.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    discountPriceEt.setVisibility(View.VISIBLE);
                    discountNoteEt.setVisibility(View.VISIBLE);
                } else {
                    discountPriceEt.setVisibility(View.GONE);
                    discountNoteEt.setVisibility(View.GONE);
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

    }

    private String productTitle, productDescription, productCategory, productQuantity, originalPrice, discountPrice, discountNote;
    private boolean discountAvailable = false;

    private void inputData() {
        productTitle = titleEt.getText().toString().trim();
        productDescription = descriptionEt.getText().toString().trim();
        productCategory = categoryTv.getText().toString().trim();
        productQuantity = quantityEt.getText().toString().trim();
        originalPrice = priceEt.getText().toString().trim();
        discountAvailable = discountSwitch.isChecked();

        if (TextUtils.isEmpty(productTitle)) {
            Toast.makeText(this, "Title is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productDescription)) {
            Toast.makeText(this, "Description is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(productCategory)) {
            Toast.makeText(this, "Category is required....!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(originalPrice)) {
            Toast.makeText(this, "Price is necessary....!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (discountAvailable) {

            discountPrice = discountPriceEt.getText().toString().trim();
            discountNote = discountNoteEt.getText().toString().trim();

            if (TextUtils.isEmpty(discountPrice)) {
                Toast.makeText(this, "Discount Price is required....!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            discountPrice = "0";
            discountNote = "";
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        if (user.isEmailVerified()) {
            addProduct();
        }
        else {
            Toast.makeText(AddProductActivity.this, "Verify your Email first then only you can add product to your Shop", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddProductActivity.this, ProfileEditSellerActivity.class));
        }

    }

    private void addProduct() {
        progressDialog.setMessage("Adding Product");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();

        if (image_uri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("productId", "" + timestamp);
            hashMap.put("productTitle", "" + productTitle);
            hashMap.put("productDescription", "" + productDescription);
            hashMap.put("productCategory", "" + productCategory);
            hashMap.put("productQuantity", "" + productQuantity);
            hashMap.put("productIcon", "");
            hashMap.put("originalPrice", "" + originalPrice);
            hashMap.put("discountPrice", "" + discountPrice);
            hashMap.put("discountNote", "" + discountNote);
            hashMap.put("discountAvailable", "" + discountAvailable);
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("uid", "" + firebaseAuth.getUid());

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Product Added Successfully...", Toast.LENGTH_SHORT).show();
                            clearData();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {

            String filePathAndName = "product_images/" + "" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productId", "" + timestamp);
                                hashMap.put("productTitle", "" + productTitle);
                                hashMap.put("productDescription", "" + productDescription);
                                hashMap.put("productCategory", "" + productCategory);
                                hashMap.put("productQuantity", "" + productQuantity);
                                hashMap.put("productIcon", ""+downloadImageUri);
                                hashMap.put("originalPrice", "" + originalPrice);
                                hashMap.put("discountPrice", "" + discountPrice);
                                hashMap.put("discountNote", "" + discountNote);
                                hashMap.put("discountAvailable", "" + discountAvailable);
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("uid", "" + firebaseAuth.getUid());

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, "Product Added Successfully...", Toast.LENGTH_SHORT).show();
                                                clearData();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void clearData() {

        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        quantityEt.setText("");
        priceEt.setText("");
        discountPriceEt.setText("");
        discountNoteEt.setText("");
        productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
        image_uri = null;

    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category")
                .setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String category = Constants.productCategories[i];

                        categoryTv.setText(category);
                    }
                })
                .show();
    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraActivityResultLauncher.launch(intent);

    }

    private void pickImageGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri);
                        Intent data = result.getData();

                        productIconIv.setImageURI(image_uri);
                    } else {
                        Toast.makeText(AddProductActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri);
                        Intent data = result.getData();
                        image_uri = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri);

                        productIconIv.setImageURI(image_uri);
                    } else {
                        Toast.makeText(AddProductActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,

                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera();
                    } else {
                        Toast.makeText(this, "Camera permission are necessary...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickImageGallery();
                    } else {
                        Toast.makeText(this, "Storage permission is necessary...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}
