package com.anurag.emart.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.R;
import com.anurag.emart.activities.ShopProductsActivity;
import com.anurag.emart.models.ModelCartItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cartitem, parent, false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, @SuppressLint("RecyclerView") int position) {

        ModelCartItem modelCartItem = cartItems.get(position);

        String id = modelCartItem.getId();
        String pId = modelCartItem.getpId();
        String title = modelCartItem.getName();
        String cost = modelCartItem.getCost();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();

        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText(""+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");
        holder.itemPriceEachTv.setText(""+price);

        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_ID", new String[]{"text", "unique"}))
                        .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price_Each", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Price", new String[]{"text", "not null"}))
                        .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1, id);
                Toast.makeText(context, "Removed from Cart", Toast.LENGTH_SHORT).show();

                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();


                double subTotalWithoutDiscount = ((ShopProductsActivity)context).allTotalPrice;
                double subTotalAfterProductRemove = subTotalWithoutDiscount - Double.parseDouble(cost.replace("Rs.", ""));
                ((ShopProductsActivity)context).allTotalPrice = subTotalAfterProductRemove;
                ((ShopProductsActivity)context).sTotalTv.setText("Rs." + String.format("%.2f" , ((ShopProductsActivity)context).allTotalPrice));

                double deliveryFee = Double.parseDouble(((ShopProductsActivity)context).deliveryFee.replace("Rs." , ""));

                if (((ShopProductsActivity)context).isPromoCodeApplied){

                    double promoPrice = Double.parseDouble(((ShopProductsActivity)context).promoPrice);

                    if (subTotalAfterProductRemove < Double.parseDouble(((ShopProductsActivity)context).promoMinimumOrderPrice)){
                        Toast.makeText(context, "This code is valid for order with minimum amount: Rs."+((ShopProductsActivity)context).promoMinimumOrderPrice, Toast.LENGTH_SHORT).show();
                        ((ShopProductsActivity)context).applyBtn.setVisibility(View.GONE);
                        ((ShopProductsActivity)context).promoDescriptionTv.setVisibility(View.GONE);
                        ((ShopProductsActivity)context).promoDescriptionTv.setText("");
                        ((ShopProductsActivity)context).discountTv.setText("Rs.0");
                        ((ShopProductsActivity)context).isPromoCodeApplied = false;
                        ((ShopProductsActivity)context).allTotalPriceTv.setText("Rs." + String.format("%.2f", Double.parseDouble(String.format("%.2f" , subTotalAfterProductRemove + deliveryFee))));

                    }
                    else {
                        ((ShopProductsActivity)context).applyBtn.setVisibility(View.VISIBLE);
                        ((ShopProductsActivity)context).promoDescriptionTv.setVisibility(View.VISIBLE);
                        ((ShopProductsActivity)context).promoDescriptionTv.setText(((ShopProductsActivity)context).promoDescription);
                        ((ShopProductsActivity)context).isPromoCodeApplied = true;
                        ((ShopProductsActivity)context).allTotalPriceTv.setText("Rs." + String.format("%.2f", Double.parseDouble(String.format("%.2f" , subTotalAfterProductRemove + deliveryFee - promoPrice))));
                    }
                }
                else {
                    ((ShopProductsActivity)context).allTotalPriceTv.setText("Rs." + String.format("%.2f", Double.parseDouble(String.format("%.2f" , subTotalAfterProductRemove + deliveryFee))));
                }

                ((ShopProductsActivity)context).cartCount();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{

        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv,
                itemQuantityTv, itemRemoveTv;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);



        }
    }

}
