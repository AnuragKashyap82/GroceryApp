package com.anurag.emart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.Constants;
import com.anurag.emart.R;
import com.anurag.emart.adapters.AdapterCartItem;
import com.anurag.emart.adapters.AdapterCategories;
import com.anurag.emart.adapters.AdapterProductUser;
import com.anurag.emart.models.ModelCartItem;
import com.anurag.emart.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ShopProductsActivity extends AppCompatActivity {

    private ImageView shopIv;
    private TextView shopNameTv, phoneTv, emailTv, openCloseTv, filteredProductsTv, cartCountTv,
            deliveryFeeTv, addressTv, shopNameToolBar;
    private ImageButton cartBtn, backBtn, filterProductBtn, reviewsBtn, shopDetailsBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;
    private RatingBar ratingBar;
    private LinearLayout callBtn, mapBtn;

    private String shopUid, shopOpen, profileImage;
    private String myLatitude, myLongitude, myPhone;
    private String shopName,shopEmail, shopPhone, shopAddress, shopLatitude, shopLongitude, aboutShop;
    public String deliveryFee;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productList;
    private AdapterProductUser adapterProductUser;

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private EasyDB easyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_products);

        shopIv = findViewById(R.id.shopIv);
        shopNameToolBar = findViewById(R.id.shopNameToolBar);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        cartBtn = findViewById(R.id.cartBtn);
        backBtn = findViewById(R.id.backBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        productsRv = findViewById(R.id.productsRv);
        cartCountTv = findViewById(R.id.cartCountTv);
        reviewsBtn = findViewById(R.id.reviewsBtn);
        shopDetailsBtn = findViewById(R.id.shopDetailsBtn);
        ratingBar = findViewById(R.id.ratingBar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        shopUid = getIntent().getStringExtra("shopUid");
        shopOpen = getIntent().getStringExtra("shopOpen");
        profileImage = getIntent().getStringExtra("profileImage");


        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        getImages();

        easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_ID", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();


        deleteCartData();
        cartCount();

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterProductUser.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCartDialog();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopProductsActivity.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String selected = Constants.productCategories1[i];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("All")) {
                                    loadShopProducts();
                                } else {
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        }).show();
            }
        });
        reviewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopProductsActivity.this, ShopReviewsActivity.class);
                intent.putExtra("shopUid", shopUid);
                startActivity(intent);
            }
        });
        shopDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopProductsActivity.this, ShopDetailsActivity.class);
                intent.putExtra("shopUid", shopUid);
                intent.putExtra("shopOpen", shopOpen);
                intent.putExtra("profileImage", profileImage);
                startActivity(intent);
            }
        });

    }

    private void deleteCartData() {

        easyDB.deleteAllDataFromTable();
    }

    public void cartCount() {
        int count = easyDB.getAllData().getCount();
        if (count == 0) {
            cartCountTv.setVisibility(View.GONE);
        } else {
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText("" + count);
        }
    }

    public double allTotalPrice = 0.00;

    public TextView sTotalTv, dFeeTv, allTotalPriceTv, promoDescriptionTv, discountTv;
    private EditText promoCodeEt;
    public Button applyBtn;

    private void showCartDialog() {
        cartItemList = new ArrayList<>();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);
        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
        promoCodeEt = view.findViewById(R.id.promoCodeEt);
        promoDescriptionTv = view.findViewById(R.id.promoDescriptionTv);
        discountTv = view.findViewById(R.id.discountTv);
        ImageButton validateBtn = view.findViewById(R.id.validateBtn);
        applyBtn = view.findViewById(R.id.applyBtn);
        RecyclerView cartItemRv = view.findViewById(R.id.cartItemRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkOutBtn = view.findViewById(R.id.checkOutBtn);

        if (isPromoCodeApplied) {
            promoDescriptionTv.setVisibility(View.VISIBLE);
            promoDescriptionTv.setText(promoDescription);
            applyBtn.setText("Applied");
            promoCodeEt.setText(promoCode);
            promoDescriptionTv.setText(promoDescription);
        } else {
            promoDescriptionTv.setVisibility(View.GONE);
            applyBtn.setVisibility(View.GONE);
            applyBtn.setText("Apply");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        shopNameTv.setText(shopName);

        EasyDB easyDB = EasyDB.init(this, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_ID", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()) {
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(
                    "" + id,
                    "" + pId,
                    "" + name,
                    "" + price,
                    "" + cost,
                    "" + quantity
            );

            cartItemList.add(modelCartItem);

        }

        adapterCartItem = new AdapterCartItem(this, cartItemList);
        cartItemRv.setAdapter(adapterCartItem);

        if (isPromoCodeApplied) {
            priceWithDiscount();
        } else {
            priceWithoutDiscount();
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                allTotalPrice = 0.00;
            }
        });
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                assert user != null;
                if (user.isEmailVerified()) {
                    if (shopOpen.equals("true")) {
                        if (myLatitude.equals("") || myLatitude.equals("null") || myLongitude.equals("") || myLongitude.equals("null")) {
                            Toast.makeText(ShopProductsActivity.this, "Please enter your address in your Profile before placing Order...!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (myPhone.equals("") || myPhone.equals("null")) {
                            Toast.makeText(ShopProductsActivity.this, "Please enter your Phone no. in your Profile before placing Order...!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (cartItemList.size() == 0) {
                            Toast.makeText(ShopProductsActivity.this, "No item in Cart...!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        submitOrder();
                    } else {
                        Toast.makeText(ShopProductsActivity.this, "Shop is now Closed. Order your product when theShop will reopen", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ShopProductsActivity.this, ShopClosedActivity.class);
                        intent.putExtra("profileImage", profileImage);
                        intent.putExtra("shopName", shopName);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(ShopProductsActivity.this, "Verify your Email first then only you can Confirm your Order", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ShopProductsActivity.this, ProfileEditUserActivity.class));
                }
            }

        });

        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String promotionCode = promoCodeEt.getText().toString().trim();
                if (TextUtils.isEmpty(promotionCode)) {
                    Toast.makeText(ShopProductsActivity.this, "Please enter promo Code...!", Toast.LENGTH_SHORT).show();
                } else {
                    checkCodeAvailability(promotionCode);
                }
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPromoCodeApplied = true;
                applyBtn.setText("Applied");

                priceWithDiscount();
            }
        });
    }

    private void priceWithDiscount() {
        discountTv.setText("Rs." + promoPrice);
        dFeeTv.setText("Rs." + deliveryFee);
        sTotalTv.setText("Rs." + String.format("%.2f", allTotalPrice));
        allTotalPriceTv.setText("Rs." + (allTotalPrice + Double.parseDouble(deliveryFee.replace("Rs.", "")) - Double.parseDouble(promoPrice)));
    }

    private void priceWithoutDiscount() {
        discountTv.setText("Rs.0");
        dFeeTv.setText("Rs." + deliveryFee);
        sTotalTv.setText("Rs." + String.format("%.2f", allTotalPrice));
        allTotalPriceTv.setText("Rs." + (allTotalPrice + Double.parseDouble(deliveryFee.replace("Rs.", ""))));
    }

    public boolean isPromoCodeApplied = false;
    public String promoId, promoTimestamp, promoCode, promoDescription, promoExpDate, promoMinimumOrderPrice, promoPrice;

    private void checkCodeAvailability(String promotionCode) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Checking Promo Code...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        isPromoCodeApplied = false;
        applyBtn.setText("Apply");
        priceWithoutDiscount();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Promotions").orderByChild("promoCode").equalTo(promotionCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            progressDialog.dismiss();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                promoId = "" + ds.child("id").getValue();
                                promoTimestamp = "" + ds.child("timestamp").getValue();
                                promoCode = "" + ds.child("promoCode").getValue();
                                promoDescription = "" + ds.child("description").getValue();
                                promoExpDate = "" + ds.child("expiryDate").getValue();
                                promoMinimumOrderPrice = "" + ds.child("minimumOrderPrice").getValue();
                                promoPrice = "" + ds.child("promoPrice").getValue();

                                checkCodeExpireDate();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ShopProductsActivity.this, "Invalid promo code...!", Toast.LENGTH_SHORT).show();
                            applyBtn.setVisibility(View.GONE);
                            promoDescriptionTv.setVisibility(View.GONE);
                            promoDescriptionTv.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkCodeExpireDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String todayDate = day + "/" + month + "/" + year;
        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = sdFormat.parse(todayDate);
            Date expireDate = sdFormat.parse(promoExpDate);

            if (expireDate.compareTo(currentDate) > 0) {
                checkMinimumOrderPrice();
            } else if (expireDate.compareTo(currentDate) < 0) {
                Toast.makeText(this, "This promotion code is Expired on" + promoExpDate, Toast.LENGTH_SHORT).show();
                applyBtn.setVisibility(View.GONE);
                promoDescriptionTv.setVisibility(View.GONE);
                promoDescriptionTv.setText("");
            } else if (expireDate.compareTo(currentDate) == 0) {

                checkMinimumOrderPrice();
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            applyBtn.setVisibility(View.GONE);
            promoDescriptionTv.setVisibility(View.GONE);
            promoDescriptionTv.setText("");
        }
    }

    private void checkMinimumOrderPrice() {
        if (Double.parseDouble(String.format("%.2f", allTotalPrice)) < Double.parseDouble(promoMinimumOrderPrice)) {
            Toast.makeText(this, "This code is valid for order with minimum amount: Rs." + promoMinimumOrderPrice, Toast.LENGTH_SHORT).show();
            applyBtn.setVisibility(View.GONE);
            promoDescriptionTv.setVisibility(View.GONE);
            promoDescriptionTv.setText("");
        } else {
            applyBtn.setVisibility(View.VISIBLE);
            promoDescriptionTv.setVisibility(View.VISIBLE);
            promoDescriptionTv.setText(promoDescription);
        }
    }

    private void submitOrder() {
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();
        String cost = allTotalPriceTv.getText().toString().trim().replace("Rs.", "");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", "" + timestamp);
        hashMap.put("orderTime", "" + timestamp);
        hashMap.put("orderStatus", "In Progress");
        hashMap.put("orderCost", "" + cost);
        hashMap.put("deliveryFee", "" + deliveryFee);
        hashMap.put("orderBy", "" + firebaseAuth.getUid());
        hashMap.put("orderTo", "" + shopUid);
        hashMap.put("latitude", "" + myLatitude);
        hashMap.put("longitude", "" + myLongitude);
        hashMap.put("phone", "" + myPhone);
        hashMap.put("paymentStatus", "Pending");

        if (isPromoCodeApplied) {
            hashMap.put("discount", "" + promoPrice);
        } else {
            hashMap.put("discount", "0");
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Order");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        for (int i = 0; i < cartItemList.size(); i++) {
                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId", pId);
                            hashMap1.put("name", name);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }
                        progressDialog.dismiss();
                        Toast.makeText(ShopProductsActivity.this, "Order Placed Successfully...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ShopProductsActivity.this, OrderDetailsUsersActivity.class);
                        intent.putExtra("orderTo", shopUid);
                        intent.putExtra("orderId", timestamp);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopProductsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
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

                shopNameToolBar.setText(shopName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShopProducts() {

        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);

                        }
//                         LinearLayoutManager layoutManager = new LinearLayoutManager(ShopDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);

//                         productsRv.setLayoutManager(layoutManager);
//                         productsRv.setLayoutManager(new LinearLayoutManager(MainSellerActivity.this));

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(ShopProductsActivity.this, 2);
                        productsRv.setLayoutManager(gridLayoutManager);


                        adapterProductUser = new AdapterProductUser(ShopProductsActivity.this, productList);
                        productsRv.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getImages(){
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

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        AdapterCategories adapter = new AdapterCategories(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);
    }



}