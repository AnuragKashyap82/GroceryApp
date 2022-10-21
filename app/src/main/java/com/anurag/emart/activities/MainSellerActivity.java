package com.anurag.emart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.ChooseLoginActivity;
import com.anurag.emart.adapters.AdapterCategories;
import com.anurag.emart.adapters.AdapterOrderShop;
import com.anurag.emart.adapters.AdapterProductSeller;
import com.anurag.emart.Constants;
import com.anurag.emart.models.ModelOrderShop;
import com.anurag.emart.models.ModelProduct;
import com.anurag.emart.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.content.ContentValues.TAG;

public class MainSellerActivity extends AppCompatActivity {

    private TextView nameTv, shopNameTv, emailTv, tabProductsTv,
            tabOrdersTv, filteredProductsTv, filteredOrdersTv;
    private EditText searchProductsEt;
    private ImageButton addProductBtn, filterProductBtn, filterOrderBtn, moreBtn;
    private ImageView profileIv;
    private RelativeLayout productsRl, ordersRl, profileEditTl;
    private RecyclerView productsRv, ordersRv, recyclerView;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        nameTv = findViewById(R.id.nameTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        emailTv = findViewById(R.id.emailTv);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabOrdersTv = findViewById(R.id.tabOrdersTv);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        searchProductsEt = findViewById(R.id.searchProductsEt);
        addProductBtn = findViewById(R.id.addProductBtn);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        profileIv = findViewById(R.id.profileIv);
        productsRl = findViewById(R.id.productsRl);
        ordersRl = findViewById(R.id.ordersRl);
        productsRv = findViewById(R.id.productsRv);
        profileEditTl = findViewById(R.id.profileEditTl);
        filteredOrdersTv = findViewById(R.id.filteredOrdersTv);
        filterOrderBtn = findViewById(R.id.filterOrderBtn);
        ordersRv = findViewById(R.id.ordersRv);
        moreBtn = findViewById(R.id.moreBtn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerView);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();

        loadAllOrders();

        showProductsUI();

        getImages();

        searchProductsEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterProductSeller.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));
            }
        });
        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrdersUI();
            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selected = Constants.productCategories1[i];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("All")) {
                                    loadAllProducts();
                                } else {
                                    loadFilteredProducts(selected);
                                }
                            }
                        }).show();
            }
        });
        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] options = {"All", "In Progress", "Completed", "Cancelled"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Orders:")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    filteredOrdersTv.setText("All Orders");
                                    adapterOrderShop.getFilter().filter("");
                                } else {
                                    String optionsClicked = options[i];
                                    filteredOrdersTv.setText("" + optionsClicked + " Orders");
                                    adapterOrderShop.getFilter().filter(optionsClicked);
                                }
                            }
                        })
                        .show();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settingsTv:
                        startActivity(new Intent(MainSellerActivity.this, SettingsActivity.class));
                        break;

                    case R.id.shopNameTv:
                        Toast.makeText(MainSellerActivity.this, "ShopName Navigation is Clicked", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.profileTv:
                        startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
                        break;

                    case R.id.logoutTv:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
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


        PopupMenu popupMenu = new PopupMenu(MainSellerActivity.this, moreBtn);
        popupMenu.getMenu().add("Promotion Codes");
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Reviews");
        popupMenu.getMenu().add("Settings");
        popupMenu.getMenu().add("About Us");
        popupMenu.getMenu().add("Logout");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle() == "Promotion Codes") {
                    startActivity(new Intent(MainSellerActivity.this, PromotionCodesActivity.class));
                } else if (menuItem.getTitle() == "Profile") {
                    startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
                } else if (menuItem.getTitle() == "Reviews") {
                    Intent intent = new Intent(MainSellerActivity.this, ShopReviewsActivity.class);
                    intent.putExtra("shopUid", "" + firebaseAuth.getUid());
                    startActivity(intent);
                } else if (menuItem.getTitle() == "Settings") {
                    startActivity(new Intent(MainSellerActivity.this, SettingsActivity.class));
                } else if (menuItem.getTitle() == "About Us") {
                    startActivity(new Intent(MainSellerActivity.this, ShopDetailsSellerActivity.class));
                } else if (menuItem.getTitle() == "Logout") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
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
                startActivity(new Intent(MainSellerActivity.this, ProfileEditSellerActivity.class));
            }
        });
    }

    private void loadAllOrders() {
        orderShopArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Order")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderShopArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);
                            orderShopArrayList.add(modelOrderShop);
                        }
                        adapterOrderShop = new AdapterOrderShop(MainSellerActivity.this, orderShopArrayList);
                        ordersRv.setAdapter(adapterOrderShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        productList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String productCategory = "" + ds.child("productCategory").getValue();
                            if (selected.equals(productCategory)) {

                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);

                            }

                        }
                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProducts() {
        productList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        productList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
//                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainSellerActivity.this, LinearLayoutManager.HORIZONTAL, false);
//                        productsRv.setLayoutManager(layoutManager);

                        // productsRv.setLayoutManager(new LinearLayoutManager(MainSellerActivity.this));

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainSellerActivity.this, 2);
                        productsRv.setLayoutManager(gridLayoutManager);

                        adapterProductSeller = new AdapterProductSeller(MainSellerActivity.this, productList);
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showProductsUI() {
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorBlack));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void showOrdersUI() {
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.colorWhite));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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
                        Toast.makeText(MainSellerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            progressDialog.dismiss();
            startActivity(new Intent(MainSellerActivity.this, ChooseLoginActivity.class));
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
                            String accountType = "" + ds.child("accountType").getValue();
                            String email = "" + ds.child("email").getValue();
                            String shopName = "" + ds.child("shopName").getValue();
                            String profileImage = "" + ds.child("profileImage").getValue();

                            nameTv.setText(name);
                            shopNameTv.setText(shopName);
                            emailTv.setText(email);

                            try {
                                Picasso.get().load(profileImage).placeholder(R.drawable.ic_store_gray).into(profileIv);
                            } catch (Exception e) {
                                profileIv.setImageResource(R.drawable.ic_store_gray);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getImages() {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mNames.add("All Categories");

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mNames.add("Beverages");

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mNames.add("Beauty & Personal Care");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mNames.add("Baby Kids");


        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Breakfast & dairy");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mNames.add("Chocolates");

        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
        mNames.add("Cookings Needs");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mNames.add("Frozen Foods");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Fruits");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Pet Cakes");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Pharmacy");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mNames.add("Vegetables");

        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mNames.add("Others");

        initRecyclerView();

    }

    private void initRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        AdapterCategories adapter = new AdapterCategories(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
    }


}