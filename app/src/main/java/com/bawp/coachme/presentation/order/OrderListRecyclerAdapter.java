package com.bawp.coachme.presentation.order;

/**
 * Class: OrderListRecyclerAdapter.java
 *
 * Recycler View Adapter that will handle all the operations related to each
 * order item (including the visualization in the fragment)
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Order;
import com.bawp.coachme.presentation.home.HomeSwpRecyclerAdapter;
import com.bawp.coachme.utils.DBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderListRecyclerAdapter extends RecyclerView.Adapter<OrderListRecyclerAdapter.OrderListViewHolder>{

    List<Order> orderList;
    Context context;
    DBHelper dbHelper;
    OrdersFragment parentFragment;

    public OrderListRecyclerAdapter(List<Order> orderList, Context context, OrdersFragment parentFragment){
        this.orderList = orderList;
        this.context = context;
        this.dbHelper = new DBHelper(context);
        this.parentFragment = parentFragment;
    }

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.order_cardview_layout, parent, false);
        OrderListRecyclerAdapter.OrderListViewHolder holder = new OrderListRecyclerAdapter.OrderListViewHolder(view);

        holder.mCbSelectedProduct.setChecked(true);
        holder.mBtnCancelOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cancel Order");
                builder.setMessage("Are you sure you want to remove this item? This will cancel the appointment/self-workout");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int productType = orderList.get(holder.getAdapterPosition()).getProductType();
                        cancelOrder(orderList.get(holder.getAdapterPosition()).getOrderId(),productType,holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.mCbSelectedProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(holder.mCbSelectedProduct.isChecked()){
                    double newSubtotal = parentFragment.subTotal + Double.parseDouble(holder.mTxtViewProductPrice.getText().toString().replace("$",""));
                    parentFragment.subTotal = newSubtotal;
                    parentFragment.calculateTotalPrice();
                    orderList.get(holder.getAdapterPosition()).setSelected(true);
                }else{
                    double newSubtotal = parentFragment.subTotal - Double.parseDouble(holder.mTxtViewProductPrice.getText().toString().replace("$",""));
                    parentFragment.subTotal = newSubtotal;
                    parentFragment.calculateTotalPrice();
                    orderList.get(holder.getAdapterPosition()).setSelected(false);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int position) {
        Drawable drawableFitness = ContextCompat.getDrawable(context,R.drawable.baseline_fitness_center_18);
        Drawable drawableGymnastic = ContextCompat.getDrawable(context,R.drawable.baseline_sports_gymnastics_24);
        int color = ContextCompat.getColor(context, R.color.gymColorDark);
        drawableFitness.setTint(color);
        drawableGymnastic.setTint(color);

        holder.mTxtViewProductTitle.setText(orderList.get(position).getProductTitle());
        if (orderList.get(position).getProductType() == 1){
            holder.mTxtViewProductTitle.setCompoundDrawablesWithIntrinsicBounds(drawableGymnastic,null,null,null);
        }else{
            holder.mTxtViewProductTitle.setCompoundDrawablesWithIntrinsicBounds(drawableFitness,null,null,null);
        }

        holder.mTxtViewProductTitle.setCompoundDrawablePadding(8);
        holder.mTxtViewProductDetail.setText(orderList.get(position).getProductDescription());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        String currencyTotalPrice = formatter.format(orderList.get(position).getTotalPrice());

        holder.mTxtViewProductPrice.setText(currencyTotalPrice);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder{

        private TextView mTxtViewProductTitle;
        private TextView mTxtViewProductDetail;
        private TextView mTxtViewProductPrice;
        private CheckBox mCbSelectedProduct;
        private ImageButton mBtnCancelOrderItem;

        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtViewProductTitle = itemView.findViewById(R.id.txtViewProductTitle);
            mTxtViewProductDetail = itemView.findViewById(R.id.txtViewProductDetail);
            mTxtViewProductPrice = itemView.findViewById(R.id.txtViewProductPrice);
            mBtnCancelOrderItem = itemView.findViewById(R.id.btnCancelOrder);
            mCbSelectedProduct = itemView.findViewById(R.id.cbSelectedProduct);
        }
    }

    public void cancelOrder(String orderId, int productType, int orderPosition){
        if(productType == 1){

            //Here we have to make 2 updates, first locally in SQL and into Firebase Database,
            //to have both databases available and let free 1 spot of the trainer

            int numRowsUpdated = dbHelper.updateAppointmentStatus(orderId,2);

            if (numRowsUpdated>0){
                FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
                DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();
                DatabaseReference appRef = CoachMeDatabaseRef.child("appointments");
                Task appointmentQuery = appRef.child(orderId).get();

                appointmentQuery.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            DataSnapshot dsResult = (DataSnapshot) task.getResult();
                            Appointment appObj = dsResult.getValue(Appointment.class);
                            appObj.setStatus(2); //cancelled
                            appObj.setPaymentDate(0);
                            appObj.setPaymentId(null);
                            appRef.child(orderId).setValue(appObj);
                            orderList.remove(orderPosition);
                            notifyDataSetChanged();
                            parentFragment.updateSubtotalAfterCancelling();
                            Toast.makeText(context,"Appointment has been cancelled",Toast.LENGTH_SHORT).show();
                            updateUIByOrderListSize();
                        }else{
                            Exception error = task.getException();
                            System.out.println("At least one task failed: " + error.getMessage());
                        }
                    }
                });
            }else{
                Toast.makeText(context,"An error occurred while cancelling the self-workout plan",Toast.LENGTH_SHORT).show();
            }
        }else{

            int numRowsUpdated = dbHelper.updateSelfWorkoutPlanByUserStatus(
                    orderId,2);

            if (numRowsUpdated > 0){
                orderList.remove(orderPosition);
                notifyDataSetChanged();
                parentFragment.updateSubtotalAfterCancelling();
                Toast.makeText(context,"Self-workout purchase has been cancelled",Toast.LENGTH_SHORT).show();
                updateUIByOrderListSize();
            }else{
                Toast.makeText(context,"An error occurred while cancelling the self-workout plan",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateUIByOrderListSize(){
        if(orderList.size()>0){
            parentFragment.llNoItemsInCart.setVisibility(View.GONE);
        }else{
            parentFragment.llNoItemsInCart.setVisibility(View.VISIBLE);
        }
    }
}
