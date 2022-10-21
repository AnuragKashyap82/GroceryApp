package com.anurag.emart.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.anurag.emart.databinding.ActivityPhoneLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class PhoneLoginActivity extends AppCompatActivity {

    ActivityPhoneLoginBinding binding;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneBox.requestFocus();
        auth = FirebaseAuth.getInstance();

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private String phoneNumber;
    private void validateData() {

        phoneNumber = binding.phoneBox.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Enter Your Phone Number....", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            Intent intent = new Intent(PhoneLoginActivity.this, OtpActivity.class);
            intent.putExtra("phoneNumber", binding.phoneBox.getText().toString());
            startActivity(intent);
        }
    }
}