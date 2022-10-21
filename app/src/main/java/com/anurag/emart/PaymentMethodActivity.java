package com.anurag.emart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.activities.MainUserActivity;
import com.anurag.emart.activities.OrderDetailsSellerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PaymentMethodActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private ImageView codBtn, paytmBtn;
    private TextView amountPayable;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        firebaseAuth = FirebaseAuth.getInstance();

        backBtn = findViewById(R.id.backBtn);
        codBtn = findViewById(R.id.codBtn);
        paytmBtn = findViewById(R.id.paytmBtn);
        amountPayable = findViewById(R.id.amountPayable);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(true);

        String orderCost = getIntent().getStringExtra("orderCost");
        amountPayable.setText(orderCost);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        paytmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethodActivity.this);
                builder.setTitle("Payment Method")
                        .setMessage("Yow will be re directed to the Paytm payment please Continue to proceed")
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(PaymentMethodActivity.this, "Paytm Gateway is not Integrated now. Please choose the Cash on delivery option.....!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Toast.makeText(PaymentMethodActivity.this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
        codBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPaymentStatusDetails();
            }
        });
    }

    private void editPaymentStatusDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentMethodActivity.this);
        builder.setTitle("Payment Method")
                .setMessage("Are you sure want to Pay at Delivery")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("paymentStatus", "COD");

                        String orderId = getIntent().getStringExtra("orderId");
                        String shopUid = getIntent().getStringExtra("shopUid");

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Order");
                        ref.child(orderId)
                                .updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(PaymentMethodActivity.this, "Order is now Confirmed. Pay at Delivery...!", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PaymentMethodActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(PaymentMethodActivity.this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}