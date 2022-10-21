package com.anurag.emart.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.FilterProductUser;
import com.anurag.emart.R;
import com.anurag.emart.activities.ShopProductsActivity;
import com.anurag.emart.models.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user, parent, false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {

        ModelProduct modelProduct = productList.get(position);
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();

        holder.titleTv.setText(productTitle);
        holder.discountNoteTv.setText(discountNote);
        holder.descriptionTv.setText(productDescription);
        holder.originalPriceTv.setText("Rs."+originalPrice);
        holder.discountedPriceTv.setText("Rs."+discountPrice);

        if (discountAvailable.equals("true")){
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_add_shopping_primary).into(holder.productIconIv);
        }
        catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_add_shopping_primary);
        }

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQuantityDialog(modelProduct);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;

    private void showQuantityDialog(ModelProduct modelProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);

        ImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView pQuantityTv = view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountedNoteTv = view.findViewById(R.id.discountedNoteTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv = view.findViewById(R.id.priceDiscountedTv);
        TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageView decrementBtn = view.findViewById(R.id.decrementBtn);
        ImageView incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        String productId = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String description= modelProduct.getProductDescription();
        String discountNote = modelProduct.getDiscountNote();
        String image = modelProduct.getProductIcon();

        String price;
        if (modelProduct.getDiscountAvailable().equals("true")){
            price = modelProduct.getDiscountPrice();
            discountedNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
        else {
            discountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price = modelProduct.getOriginalPrice();
        }

        cost = Double.parseDouble(price.replaceAll("Rs.", ""));
        finalCost = Double.parseDouble(price.replaceAll("Rs.", ""));
        quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_cart_gray).into(productIv);
        }
        catch (Exception e){
            productIv.setImageResource(R.drawable.ic_cart_gray);
        }

        titleTv.setText(""+ title);
        pQuantityTv.setText(""+ productQuantity);
        descriptionTv.setText(""+ description);
        discountedNoteTv.setText(""+ discountNote);
        quantityTv.setText(""+ quantity);
        originalPriceTv.setText("Rs."+ modelProduct.getOriginalPrice());
        priceDiscountedTv.setText("Rs."+ modelProduct.getDiscountPrice());
        finalPriceTv.setText("Rs."+ finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalCost = finalCost + cost;
                quantity++;

                finalPriceTv.setText("Rs."+finalCost);
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity>1){
                    finalCost = finalCost - cost;
                    quantity--;

                    finalPriceTv.setText("Rs."+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("Rs.", "");
                String quantity = quantityTv.getText().toString().trim();

                addToCart(productId, title, priceEach, totalPrice, quantity);

                dialog.dismiss();
            }
        });
    }

    private  int itemId = 1;
    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_ID", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_ID", itemId)
                .addData("Item_PID", productId)
                .addData("Item_Name", title)
                .addData("Item_Price_Each", priceEach)
                .addData("Item_Price", price)
                .addData("Item_Quantity", quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();

        ((ShopProductsActivity)context).cartCount();

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterProductUser(this, filterList);

        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

       private ImageView productIconIv;
       private TextView discountNoteTv, titleTv, descriptionTv, addToCartTv,
               discountedPriceTv, originalPriceTv;


        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            discountNoteTv = itemView.findViewById(R.id.discountNoteTv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv = itemView.findViewById(R.id.originalPriceTv);

        }
    }

}
