package com.anurag.emart.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anurag.emart.activities.OrderDetailsUsersActivity;
import com.anurag.emart.R;
import com.anurag.emart.models.ModelOrderUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser>{

    private Context context;
    private ArrayList<ModelOrderUser> orderUserList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> orderUserList) {
        this.context = context;
        this.orderUserList = orderUserList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_order_user, parent, false);
        return new HolderOrderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {

        ModelOrderUser modelOrderUser = orderUserList.get(position);
        String orderId = modelOrderUser.getOrderId();
        String orderBy = modelOrderUser.getOrderBy();
        String orderCost = modelOrderUser.getOrderCost();
        String orderStatus = modelOrderUser.getOrderStatus();
        String orderTime = modelOrderUser.getOrderTime();
        String orderTo = modelOrderUser.getOrderTo();

        loadShopInfo(modelOrderUser, holder);

        holder.amountTv.setText("Amount: Rs." +orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("OrderID:"+ orderId);
        if (orderStatus.equals("In Progress")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else if (orderStatus.equals("Completed")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        else if (orderStatus.equals("Cancelled")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formattedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.dateTv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", orderTo);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });
    }

    private void loadShopInfo(ModelOrderUser modelOrderUser, HolderOrderUser holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderUser.getOrderTo())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = ""+snapshot.child("shopName").getValue();
                        holder.shopNameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderUserList.size();
    }

    class HolderOrderUser extends RecyclerView.ViewHolder{

        private TextView orderIdTv, dateTv, shopNameTv, amountTv, statusTv;


        public HolderOrderUser(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);

        }
    }

}
