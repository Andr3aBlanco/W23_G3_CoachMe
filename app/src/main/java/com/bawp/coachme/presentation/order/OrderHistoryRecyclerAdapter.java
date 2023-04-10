package com.bawp.coachme.presentation.order;

/**
 * Class: OrderHistoryRecyclerAdapter.java
 *
 * Recycler View Adapter that will handle all the generation of each past order element
 * a user did in the past
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Payment;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.presentation.home.HomeAppRecyclerAdapter;
import com.bawp.coachme.utils.DBHelper;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryRecyclerAdapter extends RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>{

    List<Payment> paymentList;
    Context context;
    DBHelper dbHelper;

    public OrderHistoryRecyclerAdapter(List<Payment> paymentList, Context context){
        this.paymentList = paymentList;
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.order_history_item_layout, parent, false);
        OrderHistoryRecyclerAdapter.OrderHistoryViewHolder holder = new OrderHistoryRecyclerAdapter.OrderHistoryViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        int orderId = position + 1;
        String formattedNumber = String.format("%04d", orderId);
        String orderTitle = "ORDER Nº "+ formattedNumber;

        DateFormat format = new SimpleDateFormat("EEEE, yyyy-MM-dd");
        DateFormat formatDetail = new SimpleDateFormat("dd/MM (HH:mm)");
        String formattedPaymentDate = "Payment Date\n"+format.format(paymentList.get(position).getPaymentDate());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        String currencyTotalPrice = formatter.format(paymentList.get(position).getFinalPrice());

        holder.mTxtViewOrderDate.setText(formattedPaymentDate);
        holder.mTxtViewOrderNumber.setText(orderTitle);
        holder.mTxtViewTotalPrice.setText(currencyTotalPrice);
        //Adding Appointments
        for(Appointment app : paymentList.get(position).getAppointmentList()){
            Date bookedDate = new Date(app.getBookedDate());
            String formattedBookedDate = formatDetail.format(bookedDate);
            LinearLayout linearLayout = addItemIntoTable("Appointment",app.getServiceType()+" - "+formattedBookedDate);
            holder.llTableOrderDetail.addView(linearLayout);
        }
        //Adding Self workouts
        for(SelfWorkoutPlanByUser swp : paymentList.get(position).getSelfWorkoutPlanByUserList()){
            LinearLayout linearLayout = addItemIntoTable("Self Workout Plan",swp.getSelfworkoutplan().getTitle());
            holder.llTableOrderDetail.addView(linearLayout);
        }
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder{

        TextView mTxtViewOrderNumber;
        TextView mTxtViewOrderDate;
        TextView mTxtViewTotalPrice;
        LinearLayout llTableOrderDetail;

        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtViewOrderNumber = itemView.findViewById(R.id.txtViewOrderNumber);
            mTxtViewOrderDate = itemView.findViewById(R.id.txtViewOrderDate);
            mTxtViewTotalPrice = itemView.findViewById(R.id.txtViewTotalPrice);
            llTableOrderDetail = itemView.findViewById(R.id.llTableOrderDetail);
        }
    }

    private LinearLayout addItemIntoTable(String title, String description){
        LinearLayout linearLayout = new LinearLayout(context);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0,4,0,0);

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        TextView textView1 = new TextView(context);
        textView1.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.5f));
        textView1.setText(title);
        textView1.setTextSize(13);
        textView1.setTextColor(Color.parseColor("#200E32"));
        textView1.setTypeface(ResourcesCompat.getFont(context, R.font.poppins_regular));
        textView1.setGravity(Gravity.CENTER);
        textView1.setPadding(10, 0, 0, 0);

        TextView textView2 = new TextView(context);
        textView2.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.5f));
        textView2.setText(description);
        textView2.setTextSize(13);
        textView2.setTextColor(Color.parseColor("#200E32"));
        textView2.setTypeface(ResourcesCompat.getFont(context, R.font.poppins_regular));
        textView2.setGravity(Gravity.CENTER);
        textView2.setPadding(10, 0, 0, 0);

        linearLayout.addView(textView1);
        linearLayout.addView(textView2);

        return linearLayout;
    }

}
