package com.anurag.emart.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ImageViewSellerActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView profileIv, imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9;
    private TextView shopNameTv;
    private Button uploadImagesBtn;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private String[] cameraPermission;
    private String[] storagePermission;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private Uri image_uri1 = null;
    private Uri image_uri2 = null;
    private Uri image_uri3 = null;
    private Uri image_uri4 = null;
    private Uri image_uri5 = null;
    private Uri image_uri6 = null;
    private Uri image_uri7 = null;
    private Uri image_uri8 = null;
    private Uri image_uri9 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_seller);

        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        uploadImagesBtn = findViewById(R.id.uploadImagesBtn);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadMyInfo();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog1();
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog2();
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog3();
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog4();
            }
        });
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog5();
            }
        });
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog6();
            }
        });
        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog7();
            }
        });
        imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog8();
            }
        });
        imageView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog9();
            }
        });
        uploadImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ImageViewSellerActivity.this, "Till Now Not Integrated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImagePickDialog1() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera1();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery1();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera1() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri1);
        cameraActivityResultLauncher1.launch(intent);

    }

    private void pickImageGallery1() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher1.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri1);
                        Intent data = result.getData();

                        imageView1.setImageURI(image_uri1);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri1);
                        Intent data = result.getData();
                        image_uri1 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri1);

                        imageView1.setImageURI(image_uri1);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void showImagePickDialog2() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera2();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery2();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera2() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri2 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri2);
        cameraActivityResultLauncher2.launch(intent);

    }

    private void pickImageGallery2() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher2.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri2);
                        Intent data = result.getData();

                        imageView2.setImageURI(image_uri2);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri2);
                        Intent data = result.getData();
                        image_uri2 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri2);

                        imageView2.setImageURI(image_uri2);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

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

    private void showImagePickDialog3() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera3();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery3();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera3() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri3 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri3);
        cameraActivityResultLauncher3.launch(intent);

    }

    private void pickImageGallery3() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher3.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri3);
                        Intent data = result.getData();

                        imageView3.setImageURI(image_uri3);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri3);
                        Intent data = result.getData();
                        image_uri3 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri3);

                        imageView3.setImageURI(image_uri3);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void showImagePickDialog4() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera4();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery4();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera4() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri4 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri4);
        cameraActivityResultLauncher4.launch(intent);

    }

    private void pickImageGallery4() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher4.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher4 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri4);
                        Intent data = result.getData();

                        imageView4.setImageURI(image_uri4);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher4 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri4);
                        Intent data = result.getData();
                        image_uri4 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri4);

                        imageView4.setImageURI(image_uri4);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void showImagePickDialog5() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera5();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery5();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera5() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri5 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri5);
        cameraActivityResultLauncher5.launch(intent);

    }

    private void pickImageGallery5() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher5.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher5 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri5);
                        Intent data = result.getData();

                        imageView5.setImageURI(image_uri5);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher5 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri5);
                        Intent data = result.getData();
                        image_uri5 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri5);

                        imageView5.setImageURI(image_uri5);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void showImagePickDialog6() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera6();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery6();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera6() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri6 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri6);
        cameraActivityResultLauncher6.launch(intent);

    }

    private void pickImageGallery6() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher6.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher6 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri6);
                        Intent data = result.getData();

                        imageView6.setImageURI(image_uri6);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher6 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri6);
                        Intent data = result.getData();
                        image_uri6 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri6);

                        imageView6.setImageURI(image_uri6);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void showImagePickDialog7() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera7();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery7();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera7() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri7 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri7);
        cameraActivityResultLauncher7.launch(intent);

    }

    private void pickImageGallery7() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher7.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher7 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri7);
                        Intent data = result.getData();

                        imageView7.setImageURI(image_uri7);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher7 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri7);
                        Intent data = result.getData();
                        image_uri7 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri7);

                        imageView7.setImageURI(image_uri7);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void showImagePickDialog8() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera8();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery8();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera8() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri8 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri8);
        cameraActivityResultLauncher8.launch(intent);

    }

    private void pickImageGallery8() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher8.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher8 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri8);
                        Intent data = result.getData();

                        imageView8.setImageURI(image_uri8);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher8 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri8);
                        Intent data = result.getData();
                        image_uri8 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri8);

                        imageView8.setImageURI(image_uri8);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void showImagePickDialog9() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickImageCamera9();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickImageGallery9();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickImageCamera9() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        image_uri9 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri9);
        cameraActivityResultLauncher9.launch(intent);

    }

    private void pickImageGallery9() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher9.launch(intent);

    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher9 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: Picked from camera" + image_uri9);
                        Intent data = result.getData();

                        imageView9.setImageURI(image_uri9);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher9 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + image_uri9);
                        Intent data = result.getData();
                        image_uri9 = data.getData();
                        Log.d(TAG, "onActivityResult: Picked from gallery" + image_uri9);

                        imageView9.setImageURI(image_uri9);
                    } else {
                        Toast.makeText(ImageViewSellerActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String shopName = "" + ds.child("shopName").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();


                            shopNameTv.setText(shopName);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                            } catch (Exception e) {
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}