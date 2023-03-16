/**
 * Class: OrdersFragment.java
 *
 * Class that display the order fragment where it holds the list of appointments
 * and self-workout plans a user wants to purchase.
 *
 * The information is going to be retrieved from the Firebase Database by looking for
 * the appointments and sel-workout plans that have status 1 (pending).
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

package com.bawp.coachme.presentation.order;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Order;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;


public class OrdersFragment extends Fragment {

    String customerId;
    FragmentManager fm;
    Fragment fragment;
    ProgressBar pbOrderList;
    LinearLayout llOrderLayout;
    LinearLayout llNoItemsInCart;
    FrameLayout flOrderFragmentContainer;
    double subTotal;
    double tax;
    double totalPrice;
    TextView txtViewSubTotal;
    TextView txtViewTax;
    TextView txtViewTotal;
    List<Order> orderList;
    MaterialButton btnCheckout;

    MaterialButton btnOrderHistory;
    ArrayList<String> orderIdArray;
    ArrayList<Integer> orderTypeArray;
    DBHelper dbHelper;

    static final float TAX_PERCENTAGE = 0.1f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of your database helper class
        dbHelper = new DBHelper(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        OrdersFragment currentFragment = this;
        customerId = UserSingleton.getInstance().getUserId();

        txtViewSubTotal = view.findViewById(R.id.txtViewSubTotal);
        txtViewTax = view.findViewById(R.id.txtViewTax);
        txtViewTotal = view.findViewById(R.id.txtViewTotal);
        btnCheckout = view.findViewById(R.id.btnOrderCheckout);
        btnOrderHistory = view.findViewById(R.id.btnOrderHistory);

        llNoItemsInCart = view.findViewById(R.id.llNoItemsInCart);
        flOrderFragmentContainer = view.findViewById(R.id.orderFragmentContainer);
        pbOrderList = view.findViewById(R.id.pbOrderList);
        llOrderLayout = view.findViewById(R.id.llOrderLayout);
        pbOrderList.setVisibility(View.VISIBLE);
        llOrderLayout.setVisibility(View.GONE);

        orderList = new ArrayList<>();
        updateShoppingCart(currentFragment);

        btnOrderHistory.setOnClickListener(View -> {
            OrderHistoryFragment orderHistoryFragment = new OrderHistoryFragment();

            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            // Replace the current fragment with the new one
            fragmentTransaction.replace(R.id.barFrame, orderHistoryFragment);

            // Add the transaction to the back stack
            fragmentTransaction.addToBackStack(null);

            // Commit the transaction
            fragmentTransaction.commit();
        });

        btnCheckout.setOnClickListener(View -> {

            if (totalPrice >= 1){
                //Preparing data into Bundle object
                Bundle paymentData = new Bundle();
                paymentData.putDouble("totalAmount",totalPrice);
                updateArrayListOrder();
                paymentData.putStringArrayList("orderIdArray",orderIdArray);
                paymentData.putIntegerArrayList("orderTypeArray",orderTypeArray);

                OrderPaymentOptionsFragment orderPayOpt = new OrderPaymentOptionsFragment();
                orderPayOpt.setArguments(paymentData);

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.barFrame, orderPayOpt);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }else{
                Toast.makeText(getContext(),"Select at least 1 item",Toast.LENGTH_SHORT).show();
            }


        });

        // Inflate the layout for this fragment
        return view;
    }

    private void updateShoppingCart(OrdersFragment currentFragment){
        //Getting appointments pending to purchase
        List<Appointment> appointments = dbHelper.getAppointmentsByStatus(1);

        for (Appointment appObj : appointments){
            Date bookedDate = new Date(appObj.getBookedDate());
            DateFormat format = new SimpleDateFormat("EEE, dd/MM/yy HH:mm");
            String formattedBookedDate = format.format(bookedDate);
            Trainer trainerObj = dbHelper.getTrainerById(appObj.getTrainerId());

            orderList.add(
                    new Order(appObj.getId(),
                            "TRAINING SESSION with "+trainerObj.getFirstName().toUpperCase(),
                            1,
                            appObj.getServiceType() + " Session\n"+formattedBookedDate,
                            appObj.getTotalPrice(), true)
            );
            subTotal += appObj.getTotalPrice();
        }

        //Getting selfworkouts pending to purchase
        List<SelfWorkoutPlanByUser> selfWorkoutPlanByUsers = dbHelper.getSelfWorkoutPlanByUserByStatus(1);

        for (SelfWorkoutPlanByUser swpByUser: selfWorkoutPlanByUsers){
            orderList.add(new Order(
                    Integer.toString(swpByUser.getId()),
                    "SELF-WORKOUT PLAN",
                    2,
                    "Plan Name:\n"+swpByUser.getSelfworkoutplan().getTitle(),
                    swpByUser.getSelfworkoutplan().getPlanPrice(),
                    true
            ));
            subTotal += swpByUser.getSelfworkoutplan().getPlanPrice();
        }

        if(orderList.size()>0){
            flOrderFragmentContainer.setVisibility(View.VISIBLE);
            llNoItemsInCart.setVisibility(View.GONE);
        }else{
            flOrderFragmentContainer.setVisibility(View.GONE);
            llNoItemsInCart.setVisibility(View.VISIBLE);
        }

        calculateTotalPrice();

        fm = getActivity().getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.orderFragmentContainer);
        if (fragment == null){
            fragment = OrderListRecyclerFragment.newInstance(orderList,currentFragment );

            fm.beginTransaction()
                    .add(R.id.orderFragmentContainer,fragment)
                    .commit();
        }else{
            fragment = OrderListRecyclerFragment.newInstance(orderList,currentFragment);

            fm.beginTransaction()
                    .replace(R.id.orderFragmentContainer,fragment)
                    .commit();
        }

        pbOrderList.setVisibility(View.GONE);
        llOrderLayout.setVisibility(View.VISIBLE);


    }

    public void calculateTotalPrice(){
        tax = subTotal * TAX_PERCENTAGE;
        totalPrice = subTotal + tax;

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        String currencySubTotal = formatter.format(subTotal);
        String currencyTax = formatter.format(tax);
        String currencyTotalPrice = formatter.format(totalPrice);

        txtViewSubTotal.setText(currencySubTotal);
        txtViewTax.setText(currencyTax);
        txtViewTotal.setText(currencyTotalPrice);

    }

    public void updateSubtotalAfterCancelling() {
        subTotal = 0;
        for(Order order: orderList){
            subTotal += order.getTotalPrice();
        }
        calculateTotalPrice();
    }

    public void updateArrayListOrder(){
        orderIdArray = new ArrayList<>();
        orderTypeArray = new ArrayList<>();
        for (Order order: orderList){
            if (order.isSelected()){
                orderIdArray.add(order.getOrderId());
                orderTypeArray.add(order.getProductType());
            }
        }
    }

    public void lookForAppointments(){
        FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();

        DatabaseReference appRef = CoachMeDatabaseRef.child("appointments");
        DatabaseReference userRef = CoachMeDatabaseRef.child("users");

        List<Task<DataSnapshot>> tasks = new ArrayList<>();

        Query queryAppStatus = appRef.orderByChild("status").equalTo(3);

        tasks.add(queryAppStatus.get());
        tasks.add(userRef.get());

        Task<List<DataSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

        allTasks.addOnCompleteListener(new OnCompleteListener<List<DataSnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<DataSnapshot>> task) {

            }
        });


    }

}
