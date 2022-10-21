package com.anurag.emart.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anurag.emart.R;
import com.squareup.picasso.Picasso;

public class ShopClosedActivity extends AppCompatActivity {

    private String profileImage, shopName;
    private ImageView profileIv;
    private Button shopBtn;
    private TextView shopNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_closed);

        profileIv = findViewById(R.id.profileIv);
        shopBtn = findViewById(R.id.shopBtn);
        shopNameTv = findViewById(R.id.shopNameTv);

        profileImage = getIntent().getStringExtra("profileImage");
        shopName = getIntent().getStringExtra("shopName");

        try {
            Picasso.get().load(profileImage).into(profileIv);
        }
        catch (Exception e){

        }
        shopNameTv.setText(shopName);
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopClosedActivity.this, MainUserActivity.class));
                finishAffinity();
            }
        });
    }
}