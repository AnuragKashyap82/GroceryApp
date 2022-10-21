package com.anurag.emart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import okhttp3.internal.Version;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.ChooseLoginActivity;
import com.anurag.emart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.concurrent.Executor;

import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(SplashActivity.this, ChooseLoginActivity.class));
                    finish();
                } else {
                    checkUserType();
                }
            }
        }, 1000);
    }

    private void checkUserType() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String accountType = "" + snapshot.child("accountType").getValue();
                        if (accountType.equals("Seller")) {
//                          startActivity(new Intent(SplashActivity.this, AuthenticationSellerActivity.class));
                            executor = ContextCompat.getMainExecutor(SplashActivity.this);
                            biometricPrompt = new BiometricPrompt(SplashActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                                @Override
                                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                    super.onAuthenticationError(errorCode, errString);
                                    Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                    super.onAuthenticationSucceeded(result);
                                    Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SplashActivity.this, MainSellerActivity.class));
                                    finish();
                                }

                                @Override
                                public void onAuthenticationFailed() {
                                    super.onAuthenticationFailed();
                                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                    .setTitle("Biometric login for EMart")
                                    .setSubtitle("Log in using your biometric credential")
                                    .setNegativeButtonText("Use account password")
                                    .build();
                            biometricPrompt.authenticate(promptInfo);

                        } else {
                            //startActivity(new Intent(SplashActivity.this, AuthenticationUserActivity.class));
                            executor = ContextCompat.getMainExecutor(SplashActivity.this);
                            biometricPrompt = new BiometricPrompt(SplashActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                                @Override
                                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                    super.onAuthenticationError(errorCode, errString);
                                    Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                    super.onAuthenticationSucceeded(result);
                                    Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SplashActivity.this, MainUserActivity.class));
                                    finish();
                                }

                                @Override
                                public void onAuthenticationFailed() {
                                    super.onAuthenticationFailed();
                                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            });


                            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                    .setTitle("Biometric login for EMart")
                                    .setSubtitle("Log in using your biometric credential")
                                    .setNegativeButtonText("Use account password")
                                    .build();

                            biometricPrompt.authenticate(promptInfo);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}