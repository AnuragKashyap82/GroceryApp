package com.anurag.emart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.PaymentMethodActivity;
import com.anurag.emart.R;
import com.anurag.emart.adapters.AdapterOrderedItem;
import com.anurag.emart.models.ModelOrderedItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailsUsersActivity extends AppCompatActivity {

    private String orderTo, orderId;

    private ImageButton backBtn, writeReviewBtn, paymentBtn;
    private TextView orderIdTv, dateTv, orderStatusTv, shopNameTv, totalItemsTv, amountTv, addressTv, phoneTv, paymentStatusTv;
    private RecyclerView itemsRv;


    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_users);

        backBtn = findViewById(R.id.backBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        itemsRv = findViewById(R.id.itemsRv);
        writeReviewBtn = findViewById(R.id.writeReviewBtn);
        phoneTv = findViewById(R.id.phoneTv);
        paymentStatusTv = findViewById(R.id.paymentStatusTv);
        paymentBtn = findViewById(R.id.paymentBtn);

        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopInfo();
        loadOrderDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(OrderDetailsUsersActivity.this, WriteReviewActivity.class);
                intent1.putExtra("shopUid", orderTo);
                startActivity(intent1);
            }
        });
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderDetailsUsersActivity.this, PaymentMethodActivity.class);
                intent.putExtra("orderCost", amountTv.getText().toString().trim());
                intent.putExtra("orderId", orderIdTv.getText().toString().trim());
                intent.putExtra("shopUid", orderTo);
                startActivity(intent);
            }
        });
    }

    private void loadOrderedItems() {
        orderedItemArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Order").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedItemArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);
                            orderedItemArrayList.add(modelOrderedItem);
                        }
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailsUsersActivity.this, orderedItemArrayList);
                        itemsRv.setAdapter(adapterOrderedItem);

                        totalItemsTv.setText(""+snapshot.getChildrenCount());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Order").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String orderBy = "" + snapshot.child("orderBy").getValue();
                        String orderCost = "" + snapshot.child("orderCost").getValue();
                        String orderId = "" + snapshot.child("orderId").getValue();
                        String orderStatus = "" + snapshot.child("orderStatus").getValue();
                        String orderTime = "" + snapshot.child("orderTime").getValue();
                        String orderTo = "" + snapshot.child("orderTo").getValue();
                        String deliveryFee = "" + snapshot.child("deliveryFee").getValue();
                        String latitude = "" + snapshot.child("latitude").getValue();
                        String longitude = "" + snapshot.child("longitude").getValue();
                        String discount = ""+snapshot.child("discount").getValue();
                        String phone = ""+snapshot.child("phone").getValue();
                        String paymentStatus = ""+snapshot.child("paymentStatus").getValue();

                        if (discount.equals("null") || discount.equals("0")){
                            discount = "& Discount Rs.0";
                        }
                        else {
                            discount = "\n                     & Discount Rs."+discount;
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formattedDate = DateFormat.format("dd/MM/yyyy     hh:mm a", calendar).toString();

                        if (orderStatus.equals("In Progress")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                        } else if (orderStatus.equals("Completed")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                        } else if (orderStatus.equals("Cancelled")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.colorRed));
                        }

                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("Rs."+orderCost + " [ Including delivery Fee Rs."+ deliveryFee+ " "+discount+" ]");
                        dateTv.setText(formattedDate);
                        phoneTv.setText(phone);
                        paymentStatusTv.setText(paymentStatus);
                        findAddress(latitude, longitude);

                        if (paymentStatus.equals("Pending")){
                            paymentStatusTv.setTextColor(getResources().getColor(R.color.colorRed));
                        }else if (paymentStatus.equals("COD")){
                            paymentStatusTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                        } else if (paymentStatus.equals("Completed")){
                            paymentStatusTv.setTextColor(getResources().getColor(R.color.colorGreen));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = "" + snapshot.child("shopName").getValue();
                        shopNameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void findAddress(String latitude, String longitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            String address = addresses.get(0).getAddressLine(0);
            addressTv.setText(address);

        }
        catch(Exception e){

        }
    }
}