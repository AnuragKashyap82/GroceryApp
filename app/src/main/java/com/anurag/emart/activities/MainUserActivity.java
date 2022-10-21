package com.anurag.emart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.ChooseLoginActivity;
import com.anurag.emart.R;
import com.anurag.emart.adapters.AdapterOrderUser;
import com.anurag.emart.adapters.AdapterShop;
import com.anurag.emart.models.ModelOrderUser;
import com.anurag.emart.models.ModelProduct;
import com.anurag.emart.models.ModelShop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private TextView nameTv, emailTv, phoneTv, tabShopsTv, tabOrdersTv;
    private ImageButton moreBtn;
    private ImageView profileIv;
    private RelativeLayout shopsRl, ordersRl, profileEditTl;
    private RecyclerView shopRv, ordersRv;
    private BottomNavigationView bottomNavigationView;


    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelShop> shopList;
    private AdapterShop adapterShop;

    private ArrayList<ModelOrderUser> ordersList;
    private AdapterOrderUser adapterOrderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        nameTv = findViewById(R.id.nameTv);
        profileIv = findViewById(R.id.profileIv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        tabShopsTv = findViewById(R.id.tabShopsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        shopsRl = findViewById(R.id.shopsRl);
        ordersRl = findViewById(R.id.ordersRl);
        shopRv = findViewById(R.id.shopRv);
        profileEditTl = findViewById(R.id.profileEditTl);
        ordersRv = findViewById(R.id.ordersRv);
        moreBtn = findViewById(R.id.moreBtn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        showShopsUI();

        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShopsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrderUI();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingsTv:
                        startActivity(new Intent(MainUserActivity.this, SettingsActivity.class));
                        break;

                    case R.id.shopNameTv:
                        Toast.makeText(MainUserActivity.this, "ShopName  is Clicked", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.profileTv:
                        startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));
                        break;

                    case R.id.logoutTv:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainUserActivity.this);
                        builder.setTitle("Logout")
                                .setMessage("Are you sure want to Logout" + "?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        makeMeOffline();
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();

                        break;
                }
                return true;
            }
        });

        PopupMenu popupMenu = new PopupMenu(MainUserActivity.this, moreBtn);
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Settings");
        popupMenu.getMenu().add("Logout");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Profile") {
                    startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));
                } else if (menuItem.getTitle() == "Settings") {
                    startActivity(new Intent(MainUserActivity.this, SettingsActivity.class));
                } else if (menuItem.getTitle() == "Logout") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainUserActivity.this);
                    builder.setTitle("Logout")
                            .setMessage("Are you sure want to Logout" + "?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    makeMeOffline();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
                return true;
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });
        profileEditTl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));
            }
        });
    }

    private void showShopsUI() {
        shopsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabShopsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void showOrderUI() {
        shopsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);

    }


    private void makeMeOffline() {
        progressDialog.setMessage("Logging out...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            progressDialog.dismiss();
            startActivity(new Intent(MainUserActivity.this, ChooseLoginActivity.class));
            finish();
        } else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();
                            String accountType = "" + ds.child("accountType").getValue();
                            String city = "" + ds.child("city").getValue();

                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_gray).into(profileIv);
                            } catch (Exception e) {
                                profileIv.setImageResource(R.drawable.ic_person_gray);
                            }

                            loadShops(city);
                            loadOrders();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrders() {
        ordersList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = "" + ds.getRef().getKey();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Order");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);

                                            ordersList.add(modelOrderUser);
                                        }
                                        adapterOrderUser = new AdapterOrderUser(MainUserActivity.this, ordersList);

                                        ordersRv.setAdapter(adapterOrderUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShops(String myCity) {

        shopList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            String shopCity = "" + ds.child("city").getValue();

                            if (shopCity.equals(myCity)) {
                                shopList.add(modelShop);
                            }

                            // shopList.add(modelShop);

                        }

                        adapterShop = new AdapterShop(MainUserActivity.this, shopList);
                        shopRv.setAdapter(adapterShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}