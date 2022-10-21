package com.anurag.emart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ShopDetailsSellerActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView shopNameTv, phoneTv, emailTv, openCloseTv,
            deliveryFeeTv, addressTv;
    private EditText shopDetailsEt;
    private FirebaseAuth firebaseAuth;

    private ImageButton backBtn, imageAddBtn;
    private RatingBar ratingBar;
    private LinearLayout callBtn, mapBtn;
    private String shopUid, shopOpen, profileImage;
    private String myLatitude, myLongitude, myPhone;

    private ProgressDialog progressDialog;
    private Button updateBtn;

    private String shopName, shopEmail, shopPhone, shopAddress, shopLatitude, shopLongitude;
    public String deliveryFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details_seller);

        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        backBtn = findViewById(R.id.backBtn);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        ratingBar = findViewById(R.id.ratingBar);
        shopDetailsEt = findViewById(R.id.shopDetailsEt);
        updateBtn = findViewById(R.id.updateBtn);
        imageAddBtn = findViewById(R.id.imageAddBtn);


        firebaseAuth = FirebaseAuth.getInstance();

        loadShopDetails();
        loadMyInfo();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPhone();
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });
        imageAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopDetailsSellerActivity.this, ImageViewSellerActivity.class));
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateShopDetails();
            }

            private void updateShopDetails() {
                progressDialog.setMessage("Updating Profile...");
                progressDialog.show();

                String shopDetails;
                shopDetails = shopDetailsEt.getText().toString().trim();

                if (shopDetailsEt != null){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("aboutUs", "" + shopDetails);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ShopDetailsSellerActivity.this, "About Us Updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ShopDetailsSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            }
        });
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String address = "" + ds.child("address").getValue();
                            String deliveryFee = "" + ds.child("deliveryFee").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String shopName = "" + ds.child("shopName").getValue();
                            String shopDetails = "" + ds.child("aboutUs").getValue();

                            phoneTv.setText(phone);
                            shopDetailsEt.setText(shopDetails);
                            emailTv.setText(email);
                            addressTv.setText(address);
                            shopNameTv.setText(shopName);
                            deliveryFeeTv.setText("Rs."+deliveryFee);


                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(shopIv);
                            } catch (Exception e) {
                                shopIv.setImageResource(R.drawable.ic_person_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "" + snapshot.child("name").getValue();
                shopName = "" + snapshot.child("shopName").getValue();
                shopName = "" + snapshot.child("shopName").getValue();
                shopEmail = "" + snapshot.child("email").getValue();
                shopPhone = "" + snapshot.child("phone").getValue();
                shopLatitude = "" + snapshot.child("latitude").getValue();
                shopAddress = "" + snapshot.child("address").getValue();
                shopLongitude = "" + snapshot.child("longitude").getValue();
                deliveryFee = "" + snapshot.child("deliveryFee").getValue();
                String profileImage = "" + snapshot.child("profileImage").getValue();
                String shopOpen = "" + snapshot.child("shopOpen").getValue();

                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                phoneTv.setText(shopPhone);
                deliveryFeeTv.setText("Delivery Fee: Rs." + deliveryFee);
                addressTv.setText(shopAddress);

                if (shopOpen.equals("true")) {
                    openCloseTv.setText("Open");
                } else {
                    openCloseTv.setText("Closed");
                }
                try {
                    Picasso.get().load(profileImage).into(shopIv);
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void openMap() {
        String address = "https://maps.google.com/maps?saddr=" + myLatitude + "," + myLongitude + "&daddr=" + shopLatitude + "," + shopLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(shopPhone))));
        Toast.makeText(this, "" + shopPhone, Toast.LENGTH_SHORT).show();
    }
}