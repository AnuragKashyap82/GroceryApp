package com.anurag.emart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShopDetailsActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView shopNameTv, phoneTv, emailTv, openCloseTv,
            deliveryFeeTv, addressTv, shopDetailsTv;

    private FirebaseAuth firebaseAuth;

    private ImageButton backBtn, imageViewBtn;
    private RatingBar ratingBar;
    private LinearLayout callBtn, mapBtn;
    private String shopUid, shopOpen, profileImage;
    private String myLatitude, myLongitude, myPhone;

    private String shopName, shopEmail, shopPhone, shopAddress, shopLatitude, shopLongitude, aboutShop;
    public String deliveryFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

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
        shopDetailsTv = findViewById(R.id.shopDetailsTv);
        imageViewBtn = findViewById(R.id.imageViewBtn);

        shopUid = getIntent().getStringExtra("shopUid");
        shopOpen = getIntent().getStringExtra("shopOpen");
        profileImage = getIntent().getStringExtra("profileImage");

        firebaseAuth = FirebaseAuth.getInstance();

        loadShopDetails();
        loadMyInfo();
        loadReviews();

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
        imageViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ShopDetailsActivity.this, "See The Images Of The Shop....!", Toast.LENGTH_SHORT).show();
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
                            myPhone = "" + ds.child("phone").getValue();
                            myLatitude = "" + ds.child("latitude").getValue();
                            myLongitude = "" + ds.child("longitude").getValue();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid)
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
                aboutShop = "" + snapshot.child("aboutUs").getValue();
                String profileImage = "" + snapshot.child("profileImage").getValue();
                String shopOpen = "" + snapshot.child("shopOpen").getValue();

                shopNameTv.setText(shopName);
                shopDetailsTv.setText(aboutShop);
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
    private float ratingSum = 0;

    private void loadReviews() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            float rating = Float.parseFloat("" + ds.child("ratings").getValue());
                            ratingSum = ratingSum + rating;

                        }
                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum / numberOfReviews;

                        ratingBar.setRating(avgRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}