package com.anurag.emart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anurag.emart.activities.LoginActivity;
import com.anurag.emart.activities.PhoneLoginActivity;
import com.anurag.emart.activities.RegisterUserActivity;

public class ChooseLoginActivity extends AppCompatActivity {

    private Button eLoginBtn,pRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login);

        eLoginBtn = findViewById(R.id.eLoginBtn);
        pRegisterBtn = findViewById(R.id.pRegisterBtn);

        eLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseLoginActivity.this, LoginActivity.class));
            }
        });
        pRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseLoginActivity.this, PhoneLoginActivity.class));
            }
        });
    }
}