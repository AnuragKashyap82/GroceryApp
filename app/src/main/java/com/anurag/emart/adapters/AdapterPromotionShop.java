package com.anurag.emart.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anurag.emart.R;
import com.anurag.emart.activities.AddPromotionCodeActivity;
import com.anurag.emart.models.ModelPromotion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterPromotionShop extends RecyclerView.Adapter<AdapterPromotionShop.HolderPromotionShop>{

    private Context context;
    private ArrayList<ModelPromotion> promotionArrayList;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    public AdapterPromotionShop(Context context, ArrayList<ModelPromotion> promotionArrayList) {
        this.context = context;
        this.promotionArrayList = promotionArrayList;

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPromotionShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context). inflate(R.layout.row_promotion_shop, parent, false);
        return new HolderPromotionShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPromotionShop holder, int position) {
        ModelPromotion modelPromotion = promotionArrayList.get(position);
        String id = modelPromotion.getTimestamp();
        String timestamp = modelPromotion.getTimestamp();
        String description = modelPromotion.getDescription();
        String promoCode = modelPromotion.getPromoCode();
        String promoPrice = modelPromotion.getPromoPrice();
        String expiryDate = modelPromotion.getExpiryDate();
        String minimumOrderPrice = modelPromotion.getMinimumOrderPrice();

        holder.descriptionTv.setText(description);
        holder.promoPriceTv.setText(promoPrice);
        holder.minimumOrderPriceTv.setText(minimumOrderPrice);
        holder.promoCodeTv.setText("Code: "+promoCode);
        holder.expiryDateTv.setText("Expiry Date: "+expiryDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDeleteDialog(modelPromotion, holder);
            }
        });
    }

    private void editDeleteDialog(ModelPromotion modelPromotion, HolderPromotionShop holder) {
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i ==0){
                            editPromoCode(modelPromotion);
                        }
                        else if (i == 1){
                            deletePromoCode(modelPromotion);
                        }
                    }
                })
                .show();
    }

    private void deletePromoCode(ModelPromotion modelPromotion) {
        progressDialog.setMessage("Deleting Promotion Code...!");
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Promotions").child(modelPromotion.getId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Deleted...!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void editPromoCode(ModelPromotion modelPromotion) {
        Intent intent = new Intent(context, AddPromotionCodeActivity.class);
        intent.putExtra("promoId", modelPromotion.getId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return promotionArrayList.size();
    }

    class HolderPromotionShop extends RecyclerView.ViewHolder{

        private ImageView iconTv;
        private TextView promoCodeTv, promoPriceLabelTv, promoPriceTv, minimumOrderPriceTv, expiryDateTv, descriptionTv;

        public HolderPromotionShop(@NonNull View itemView) {
            super(itemView);

            iconTv = itemView.findViewById(R.id.iconTv);
            promoCodeTv = itemView.findViewById(R.id.promoCodeTv);
            promoPriceLabelTv = itemView.findViewById(R.id.promoPriceLabelTv);
            promoPriceTv = itemView.findViewById(R.id.promoPriceTv);
            minimumOrderPriceTv = itemView.findViewById(R.id.minimumOrderPriceTv);
            expiryDateTv = itemView.findViewById(R.id.expiryDateTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
        }
    }
}
