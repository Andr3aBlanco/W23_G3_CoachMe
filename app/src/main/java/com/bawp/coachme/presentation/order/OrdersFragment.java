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

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Order;
import com.bawp.coachme.model.User;
import com.bawp.coachme.presentation.order.OrderListRecyclerFragment;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class OrdersFragment extends Fragment {

    FragmentManager fm;
    Fragment fragment;
    ProgressBar pbOrderList;
    LinearLayout llOrderLayout;
    double subTotal;
    TextView txtViewSubTotal;
    TextView txtViewTax;
    TextView txtViewTotal;
    List<Order> orderList;

    static final double TAX_PERCENTAGE = 0.1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("HELLO","ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        String userId = UserSingleton.getInstance().getUserId();

        subTotal = 0;

        OrdersFragment currentFragment = this;

        txtViewSubTotal = view.findViewById(R.id.txtViewSubTotal);
        txtViewTax = view.findViewById(R.id.txtViewTax);
        txtViewTotal = view.findViewById(R.id.txtViewTotal);

        pbOrderList = view.findViewById(R.id.pbOrderList);
        llOrderLayout = view.findViewById(R.id.llOrderLayout);
        pbOrderList.setVisibility(View.VISIBLE);
        llOrderLayout.setVisibility(View.GONE);

        orderList = new ArrayList<>();

        FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();
        DatabaseReference appRef = CoachMeDatabaseRef.child("appointments");
        DatabaseReference userRef = CoachMeDatabaseRef.child("users");

        List<Task<DataSnapshot>> tasks = new ArrayList<>();

        // Add a task to retrieve the data from the posts node
        Query queryAppointment = appRef.orderByChild("customerId").equalTo(userId);
        tasks.add(queryAppointment.get());
        tasks.add(userRef.get());

        // Create a new task that completes when all tasks in the list complete successfully
        Task<List<DataSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

        allTasks.addOnCompleteListener(new OnCompleteListener<List<DataSnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<DataSnapshot>> task) {
                if (task.isSuccessful()) {
                    // All tasks completed successfully
                    DataSnapshot appResults = task.getResult().get(0);
                    DataSnapshot userResults = task.getResult().get(1);

                    for(DataSnapshot ds: appResults.getChildren()){
                        //Getting Appointment Object
                        Appointment appObj = ds.getValue(Appointment.class);
                        //Getting User Trainer
                        User trainerObj = userResults.child(appObj.getTrainerId()).getValue(User.class);

                        if(appObj.getStatus() == 1){
                            orderList.add(
                                    new Order(ds.getKey(),
                                            "TRAINING SESSION with "+trainerObj.getFirstName().toUpperCase(),
                                            1,
                                            appObj.getServiceType() + " at "+appObj.getBookedDate(),
                                            appObj.getTotalPrice())
                            );
                            subTotal += appObj.getTotalPrice();
                        }
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

        // Inflate the layout for this fragment
        return view;
    }

    public void calculateTotalPrice(){
        double tax;
        double totalPrice;
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

}