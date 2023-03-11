/**
 * Class: OrderListRecyclerFragment.java
 *
 * Class that represent each order item fragment for an specific user.
 *
 * This class includes a RecyclerViewAdapter where we can set the information per each order item,
 * select which order items the customer wants to pay and cancel each of them.
 *
 * Special Consideration:
 *
 * void cancelOrder(params):
 * ------------------
 * This method will update the status of the order (appointments and self-workout tables).
 *
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.presentation.order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Order;
import com.bawp.coachme.utils.DBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderListRecyclerFragment extends Fragment {

    private static List<Order> orderList;
    private OrderListRecyclerFragment.RecyclerViewAdapter orderListAdapter;
    private RecyclerView recyclerView;

    private static OrdersFragment orderFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderListAdapter = new OrderListRecyclerFragment.RecyclerViewAdapter(orderList,orderFragment);
        recyclerView.setAdapter(orderListAdapter);
        return view;
    }

    public static Fragment newInstance(List<Order> list, OrdersFragment parentFragment) {
        orderList = list;
        orderFragment = parentFragment;
        return new OrderListRecyclerFragment();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder{
        private CardView mCardView;
        private TextView mTxtViewProductTitle;
        private TextView mTxtViewProductDetail;
        private TextView mTxtViewProductPrice;
        private CheckBox mCbSelectedProduct;
        private ImageButton mBtnCancelOrderItem;


        public RecyclerViewHolder(View itemView){
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.order_cardview_layout, container, false));
            mCardView = itemView.findViewById(R.id.card_container);
            mTxtViewProductTitle = itemView.findViewById(R.id.txtViewProductTitle);
            mTxtViewProductDetail = itemView.findViewById(R.id.txtViewProductDetail);
            mTxtViewProductPrice = itemView.findViewById(R.id.txtViewProductPrice);
            mBtnCancelOrderItem = itemView.findViewById(R.id.btnCancelOrder);
            mCbSelectedProduct = itemView.findViewById(R.id.cbSelectedProduct);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<OrderListRecyclerFragment.RecyclerViewHolder>{

        private List<Order> orderList;
        private OrdersFragment parentFragment;
        private DBHelper dbHelper;

        public RecyclerViewAdapter(List<Order> orderList, OrdersFragment parentFragment) {
            this.orderList = orderList;
            this.parentFragment = parentFragment;
            this.dbHelper = new DBHelper(getContext());
        }

        public void cancelOrder(String orderId, int productType, int orderPosition){
            if(productType == 1){

                //Here we have to make 2 updates, first locally in SQL and into Firebase Database,
                //to have both databases available and let free 1 spot of the trainer

                int numRowsUpdated = dbHelper.updateAppointmentStatus(orderId,3);

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
                                Toast.makeText(getContext(),"Appointment has been cancelled",Toast.LENGTH_SHORT).show();
                                updateUIByOrderListSize();
                            }else{
                                Exception error = task.getException();
                                System.out.println("At least one task failed: " + error.getMessage());
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(),"An error occurred while cancelling the self-workout plan",Toast.LENGTH_SHORT).show();
                }
            }else{

                int numRowsUpdated = dbHelper.updateSelfWorkoutPlanByUserStatus(
                        orderId,3);

                if (numRowsUpdated > 0){
                    orderList.remove(orderPosition);
                    notifyDataSetChanged();
                    parentFragment.updateSubtotalAfterCancelling();
                    Toast.makeText(getContext(),"Self-workout purchase has been cancelled",Toast.LENGTH_SHORT).show();
                    updateUIByOrderListSize();
                }else{
                    Toast.makeText(getContext(),"An error occurred while cancelling the self-workout plan",Toast.LENGTH_SHORT).show();
                }
            }
        }

        public void updateUIByOrderListSize(){
            if(orderList.size()>0){
                orderFragment.flOrderFragmentContainer.setVisibility(View.VISIBLE);
                orderFragment.llNoItemsInCart.setVisibility(View.GONE);
            }else{
                orderFragment.flOrderFragmentContainer.setVisibility(View.GONE);
                orderFragment.llNoItemsInCart.setVisibility(View.VISIBLE);
            }
        }

        @NonNull
        @Override
        public OrderListRecyclerFragment.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new OrderListRecyclerFragment.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderListRecyclerFragment.RecyclerViewHolder holder, int position) {

            Drawable drawableFitness = ContextCompat.getDrawable(getContext(),R.drawable.baseline_fitness_center_18);
            Drawable drawableGymnastic = ContextCompat.getDrawable(getContext(),R.drawable.baseline_sports_gymnastics_24);
            holder.mTxtViewProductTitle.setText(orderList.get(position).getProductTitle());
            if (orderList.get(position).getProductType() == 1){
                holder.mTxtViewProductTitle.setCompoundDrawablesWithIntrinsicBounds(drawableGymnastic,null,null,null);
            }else{
                holder.mTxtViewProductTitle.setCompoundDrawablesWithIntrinsicBounds(drawableFitness,null,null,null);
            }

            holder.mTxtViewProductTitle.setCompoundDrawablePadding(8);
            holder.mTxtViewProductDetail.setText(orderList.get(position).getProductDescription());
            holder.mCbSelectedProduct.setChecked(true);

            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            String currencyTotalPrice = formatter.format(orderList.get(position).getTotalPrice());

            holder.mTxtViewProductPrice.setText(currencyTotalPrice);
            holder.mBtnCancelOrderItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        double newSubtotal = orderFragment.subTotal + Double.parseDouble(holder.mTxtViewProductPrice.getText().toString().replace("$",""));
                        orderFragment.subTotal = newSubtotal;
                        orderFragment.calculateTotalPrice();
                        orderList.get(holder.getAdapterPosition()).setSelected(true);
                    }else{
                        double newSubtotal = orderFragment.subTotal - Double.parseDouble(holder.mTxtViewProductPrice.getText().toString().replace("$",""));
                        orderFragment.subTotal = newSubtotal;
                        orderFragment.calculateTotalPrice();
                        orderList.get(holder.getAdapterPosition()).setSelected(false);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList.size();
        }
    }

}
