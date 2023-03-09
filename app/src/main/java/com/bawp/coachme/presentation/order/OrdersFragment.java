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
import com.bawp.coachme.model.User;
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
    ArrayList<String> orderIdArray;
    ArrayList<Integer> orderTypeArray;

    static final float TAX_PERCENTAGE = 0.1f;

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

        llNoItemsInCart = view.findViewById(R.id.llNoItemsInCart);
        flOrderFragmentContainer = view.findViewById(R.id.orderFragmentContainer);
        pbOrderList = view.findViewById(R.id.pbOrderList);
        llOrderLayout = view.findViewById(R.id.llOrderLayout);
        pbOrderList.setVisibility(View.VISIBLE);
        llOrderLayout.setVisibility(View.GONE);

        orderList = new ArrayList<>();
        updateShoppingCart(currentFragment);

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
        FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();

        DatabaseReference appRef = CoachMeDatabaseRef.child("appointments");
        DatabaseReference userRef = CoachMeDatabaseRef.child("users");
        DatabaseReference swpByRef = CoachMeDatabaseRef.child("selfWorkoutPlans");
        DatabaseReference swpByUserRef = CoachMeDatabaseRef.child("selfWorkoutPlansByUser");

        List<Task<DataSnapshot>> tasks = new ArrayList<>();
        subTotal = 0;

        // Add a task to retrieve the data from the posts node
        Query queryAppointment = appRef.orderByChild("customerId").equalTo(UserSingleton.getInstance().getUserId());
        Query querySwpByUser = swpByUserRef.orderByChild("customerId").equalTo(UserSingleton.getInstance().getUserId());

        tasks.add(queryAppointment.get());
        tasks.add(userRef.get());
        tasks.add(swpByRef.get());
        tasks.add(querySwpByUser.get());

        // Create a new task that completes when all tasks in the list complete successfully
        Task<List<DataSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

        allTasks.addOnCompleteListener(new OnCompleteListener<List<DataSnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<DataSnapshot>> task) {
                if (task.isSuccessful()) {
                    // All tasks completed successfully
                    DataSnapshot appResults = task.getResult().get(0);
                    DataSnapshot userResults = task.getResult().get(1);
                    DataSnapshot swpResults = task.getResult().get(2);
                    DataSnapshot swpByUserResults = task.getResult().get(3);

                    //Appointment info
                    for(DataSnapshot ds: appResults.getChildren()){
                        //Getting Appointment Object
                        Appointment appObj = ds.getValue(Appointment.class);
                        //Getting User Trainer
                        User trainerObj = userResults.child(appObj.getTrainerId()).getValue(User.class);

                        if(appObj.getStatus() == 1){

                            Date bookedDate = new Date(appObj.getBookedDate());
                            DateFormat format = new SimpleDateFormat("EEE, dd/MM/yy HH:mm");
                            String formattedBookedDate = format.format(bookedDate);
                            orderList.add(
                                    new Order(ds.getKey(),
                                            "TRAINING SESSION with "+trainerObj.getFirstName().toUpperCase(),
                                            1,
                                            appObj.getServiceType() + " Session\n"+formattedBookedDate,
                                            appObj.getTotalPrice(), true)
                            );
                            subTotal += appObj.getTotalPrice();
                        }
                    }

                    //Self-workout plan
                    for(DataSnapshot ds: swpByUserResults.getChildren()){
                        SelfWorkoutPlanByUser swpByUser = ds.getValue(SelfWorkoutPlanByUser.class);

                        SelfWorkoutPlan swpItem = swpResults.child(swpByUser.getSelfworkoutplanId()).getValue(SelfWorkoutPlan.class);
                        if(swpByUser.getStatus() == 1){
                            orderList.add(new Order(
                                    swpByUser.getSelfworkoutplanId(),
                                    "SELF-WORKOUT PLAN",
                                    2,
                                    swpItem.getTitle(),
                                    swpItem.getPlanPrice(),
                                    true
                            ));
                            subTotal += swpItem.getPlanPrice();
                        }
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

                } else {
                    // At least one task failed
                    Exception error = task.getException();
                    System.out.println("At least one task failed: " + error.getMessage());
                }
            }
        });
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

}
