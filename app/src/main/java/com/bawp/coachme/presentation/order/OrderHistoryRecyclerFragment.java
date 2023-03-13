package com.bawp.coachme.presentation.order;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Order;
import com.bawp.coachme.model.Payment;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.presentation.home.HomeAppRecyclerFragment;
import com.bawp.coachme.presentation.home.HomeFragment;
import com.bawp.coachme.utils.DBHelper;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryRecyclerFragment extends Fragment {

    private static List<Payment> paymentList;
    private OrderHistoryRecyclerFragment.RecyclerViewAdapter orderHistoryListAdapter;
    private RecyclerView recyclerView;

    private static OrderHistoryFragment ordersFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderHistoryListAdapter = new OrderHistoryRecyclerFragment.RecyclerViewAdapter(paymentList,ordersFragment);
        recyclerView.setAdapter(orderHistoryListAdapter);
        return view;
    }

    public static Fragment newInstance(List<Payment> list, OrderHistoryFragment parentFragment) {
        paymentList = list;
        ordersFragment = parentFragment;
        return new OrderHistoryRecyclerFragment();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtViewOrderNumber;
        TextView mTxtViewOrderDate;
        TextView mTxtViewTotalPrice;
        LinearLayout llTableOrderDetail;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.order_history_item_layout, container, false));
            mTxtViewOrderNumber = itemView.findViewById(R.id.txtViewOrderNumber);
            mTxtViewOrderDate = itemView.findViewById(R.id.txtViewOrderDate);
            mTxtViewTotalPrice = itemView.findViewById(R.id.txtViewTotalPrice);
            llTableOrderDetail = itemView.findViewById(R.id.llTableOrderDetail);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<OrderHistoryRecyclerFragment.RecyclerViewHolder>{

        private List<Payment> paymentList;
        private OrderHistoryFragment parentFragment;

        private LinearLayout addItemIntoTable(String title, String description){
            LinearLayout linearLayout = new LinearLayout(getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(0,4,0,0);

            linearLayout.setLayoutParams(params);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);


            TextView textView1 = new TextView(getContext());
            textView1.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0.5f));
            textView1.setText(title);
            textView1.setTextSize(13);
            textView1.setTextColor(Color.parseColor("#200E32"));
            textView1.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_regular));
            textView1.setGravity(Gravity.CENTER);
            textView1.setPadding(10, 0, 0, 0);

            TextView textView2 = new TextView(getContext());
            textView2.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0.5f));
            textView2.setText(description);
            textView2.setTextSize(13);
            textView2.setTextColor(Color.parseColor("#200E32"));
            textView2.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_regular));
            textView2.setGravity(Gravity.CENTER);
            textView2.setPadding(10, 0, 0, 0);

            linearLayout.addView(textView1);
            linearLayout.addView(textView2);

            return linearLayout;
        }

        public RecyclerViewAdapter(List<Payment> paymentList, OrderHistoryFragment parentFragment) {
            this.paymentList = paymentList;
            this.parentFragment = parentFragment;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new OrderHistoryRecyclerFragment.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
            int orderId = position + 1;
            String formattedNumber = String.format("%04d", orderId);
            String orderTitle = "ORDER Nº "+ formattedNumber;

            DateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy");
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
    }

}
