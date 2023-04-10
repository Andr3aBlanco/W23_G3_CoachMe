package com.bawp.coachme.presentation.order;

/**
 * Class: OrdersFragment.java
 *
 * Class that display the order fragment where it holds the list of appointments
 * and self-workout plans a user wants to purchase.
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Order;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;
import com.bawp.coachme.presentation.home.HomeFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    ProgressBar pbOrderList;
    LinearLayout llOrderLayout;
    LinearLayout llNoItemsInCart;
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
    RecyclerView orderListRecyclerView;
    OrdersFragment parentFragment;

    static final float TAX_PERCENTAGE = 0.1f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        //Check if the navbar has been hidden
        BottomAppBar btnNavigationAppBar =  getActivity().findViewById(R.id.bottomNavBarWrapper);
        if (btnNavigationAppBar.getVisibility() == View.GONE){
            btnNavigationAppBar.setVisibility(View.VISIBLE);
        }

        FloatingActionButton btnActionButton = getActivity().findViewById(R.id.floatingAdd);
        if (btnActionButton.getVisibility() == View.GONE){
            btnActionButton.setVisibility(View.VISIBLE);
        }

        parentFragment = this;

        customerId = UserSingleton.getInstance().getUserId();

        txtViewSubTotal = view.findViewById(R.id.txtViewSubTotal);
        txtViewTax = view.findViewById(R.id.txtViewTax);
        txtViewTotal = view.findViewById(R.id.txtViewTotal);
        btnCheckout = view.findViewById(R.id.btnOrderCheckout);
        btnOrderHistory = view.findViewById(R.id.btnOrderHistory);
        orderListRecyclerView = view.findViewById(R.id.orderListRecyclerView);

        llNoItemsInCart = view.findViewById(R.id.llNoItemsInCart);
        pbOrderList = view.findViewById(R.id.pbOrderList);
        llOrderLayout = view.findViewById(R.id.llOrderLayout);
        pbOrderList.setVisibility(View.VISIBLE);
        llOrderLayout.setVisibility(View.GONE);

        orderList = new ArrayList<>();
        dbHelper = new DBHelper(getContext());

        /*
        Get the information of the current appointnments and selfworkout plans
        from the user (pending to be paid)
         */
        updateShoppingCart();

        //Button to display the Order History
        btnOrderHistory.setOnClickListener(View -> {

            OrderHistoryFragment orderHistoryFragment = new OrderHistoryFragment();

            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.barFrame, orderHistoryFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        });

        //Based on the elements selected, this Button will move to the Checkout Fragment
        btnCheckout.setOnClickListener(View -> {

            if (totalPrice >= 1){

                Bundle paymentData = new Bundle();
                paymentData.putDouble("totalAmount",totalPrice);

                //Update the final List of Orders to be sent to the checkout
                updateArrayListOrder();

                paymentData.putStringArrayList("orderIdArray",orderIdArray);
                paymentData.putIntegerArrayList("orderTypeArray",orderTypeArray);

                OrderPaymentOptionsFragment orderPayOpt = new OrderPaymentOptionsFragment();
                orderPayOpt.setArguments(paymentData);

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.barFrame, orderPayOpt);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }else{

                Toast.makeText(getContext(),"Select at least 1 item",Toast.LENGTH_SHORT).show();

            }


        });

        return view;
    }

    private void updateShoppingCart(){

        //1. Getting appointments pending to purchase
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

        //2. Getting selfworkouts pending to purchase
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
            llNoItemsInCart.setVisibility(View.GONE);
        }else{
            llNoItemsInCart.setVisibility(View.VISIBLE);
        }

        //updating the final price value
        calculateTotalPrice();

        //create the recycler view and add the adapter
        orderListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OrderListRecyclerAdapter orderListRecyclerAdapter = new OrderListRecyclerAdapter(orderList, getContext(),parentFragment);
        orderListRecyclerView.setAdapter(orderListRecyclerAdapter);

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

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //In case we are tapping the back button, replace the fragment
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.barFrame, new HomeFragment());
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });
    }

}
