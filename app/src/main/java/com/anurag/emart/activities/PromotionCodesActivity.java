package com.anurag.emart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anurag.emart.R;
import com.anurag.emart.adapters.AdapterPromotionShop;
import com.anurag.emart.models.ModelPromotion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PromotionCodesActivity extends AppCompatActivity {

    private ImageButton backBtn, addPromoBtn, filterBtn;
    private TextView filteredTv;
    private RecyclerView promoRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelPromotion> promotionArrayList;
    private AdapterPromotionShop adapterPromotionShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pormotion_codes);

        backBtn = findViewById(R.id.backBtn);
        addPromoBtn = findViewById(R.id.addPromoBtn);
        filteredTv = findViewById(R.id.filteredTv);
        filterBtn = findViewById(R.id.filterBtn);
        promoRv = findViewById(R.id.promoRv);

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllPromoCodes();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        addPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(PromotionCodesActivity.this, AddPromotionCodeActivity.class));
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog();
            }
        });
    }

    private void filterDialog() {
        String[] options = {"All", "Expired", "Not Expired"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Promotion Codes")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i==0){
                            filteredTv.setText("All Promotion Codes");
                            loadAllPromoCodes();
                        }
                       else if (i==1){
                            filteredTv.setText("Expired Promotion Codes");
                            loadExpiredPromoCodes();
                        }
                       else   if (i==2){
                            filteredTv.setText("Not Expired Promotion Codes");
                            loadNotExpiredPromoCodes();
                        }
                    }
                })
                .show();
    }

    private void loadAllPromoCodes(){
        promotionArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);
                            promotionArrayList.add(modelPromotion);
                        }
                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadExpiredPromoCodes(){
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day +"/"+month +"/"+ year;

        promotionArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                            String expDate = modelPromotion.getExpiryDate();

                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate = simpleDateFormat.parse(todayDate);
                                Date expiryDate = simpleDateFormat.parse(expDate);

                                if (expiryDate.compareTo(currentDate) > 0){

                                }
                                else if(expiryDate.compareTo(currentDate) < 0){

                                    promotionArrayList.add(modelPromotion);

                                }
                                else if(expiryDate.compareTo(currentDate) == 0){

                                }
                            }
                            catch (Exception e){

                            }


                        }
                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadNotExpiredPromoCodes(){
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day +"/"+month +"/"+ year;

        promotionArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Promotions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        promotionArrayList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPromotion modelPromotion = ds.getValue(ModelPromotion.class);

                            String expDate = modelPromotion.getExpiryDate();

                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date currentDate = simpleDateFormat.parse(todayDate);
                                Date expiryDate = simpleDateFormat.parse(expDate);

                                if (expiryDate.compareTo(currentDate) > 0){

                                    promotionArrayList.add(modelPromotion);

                                }
                                else if(expiryDate.compareTo(currentDate) < 0){

                                }
                                else if(expiryDate.compareTo(currentDate) == 0){

                                    promotionArrayList.add(modelPromotion);

                                }
                            }
                            catch (Exception e){

                            }


                        }
                        adapterPromotionShop = new AdapterPromotionShop(PromotionCodesActivity.this, promotionArrayList);
                        promoRv.setAdapter(adapterPromotionShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}