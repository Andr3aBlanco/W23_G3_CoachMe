/**
 * Class: OrderPaymentOptionsFragment.java
 *
 * Fragment class that will hold the screen with the available payment options.
 *
 * When the fragment is in the onCreate state, it will start the connection with the
 * available payment options. In this version of our mobile app, we are offering
 * Stripe Payment.
 *
 * With Stripe, first we are retrieving/creating the customer in the Stripe database by
 * using the Stripe SDK, then the PaymentSheet object is going to be constructed
 * (including the PaymentIntent object).
 *
 * Note:
 * -----
 * The customer id that comes from Stripe is going to be stored in the Users table in
 * Firebase Database (stripeCustomerId)
 *
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.presentation.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bawp.coachme.BuildConfig;
import com.bawp.coachme.R;

import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.OrderNotifcation;
import com.bawp.coachme.model.Payment;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.User;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderPaymentOptionsFragment extends Fragment {

    private CardView cvStripePaymentOption;
    private final String STRIPE_PUBLISHABLE_KEY = BuildConfig.stripePublishableKey;
    private final String STRIPE_SECRET_KEY = BuildConfig.stripeSecretKey;
    PaymentSheet paymentSheet;
    String customerIDStripe;
    String EphericalKey;
    String ClientSecret;
    ProgressBar pbPaymentOption;
    LinearLayout llOrderLayoutPaymentOption;
    double totalAmount;
    ArrayList<String> orderIdArray;
    ArrayList<Integer> orderTypeArray;
    FirebaseDatabase CoachMeDatabaseInstance;
    DatabaseReference CoachMeDatabaseRef;
    DatabaseReference userRef;
    DatabaseReference triggerNotificationRef;
    User currentCustomer;
    DBHelper dbHelper;



    public OrderPaymentOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            totalAmount = getArguments().getDouble("totalAmount");
            int decimalPlaces = 2;
            totalAmount = Math.round(totalAmount * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
            orderIdArray = getArguments().getStringArrayList("orderIdArray");
            orderTypeArray = getArguments().getIntegerArrayList("orderTypeArray");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_payment_options, container, false);

        CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();
        userRef = CoachMeDatabaseRef.child("users");

        dbHelper = new DBHelper(getContext());

        pbPaymentOption = view.findViewById(R.id.pbPaymentOption);
        llOrderLayoutPaymentOption = view.findViewById(R.id.llOrderLayoutPaymentOption);
        pbPaymentOption.setVisibility(View.VISIBLE);
        llOrderLayoutPaymentOption.setVisibility(View.GONE);

        //Preparing Stripe Payment Flow
        PaymentConfiguration.init(
                getContext(),
                STRIPE_PUBLISHABLE_KEY
        );

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        cvStripePaymentOption = view.findViewById(R.id.cvStripePaymentOption);

        cvStripePaymentOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlow();
            }
        });

        searchCustomerIdStripe();



        return view;
    }

    private void searchCustomerIdStripe(){

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot ds = (DataSnapshot) task.getResult();
                currentCustomer = ds.child(UserSingleton.getInstance().getUserId()).getValue(User.class);

                if (currentCustomer.getStripeCustomerId() == null){
                    Log.d("STRIPE","HELLO");
                    getStripeCustomerID();
                }else{
                    customerIDStripe = currentCustomer.getStripeCustomerId();
                    getEpherical(customerIDStripe);
                }
            }
        });

    }

    private void getStripeCustomerID(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            customerIDStripe = object.getString("id");
                            currentCustomer.setStripeCustomerId(customerIDStripe);
                            userRef.child(UserSingleton.getInstance().getUserId()).setValue(currentCustomer);
                            getEpherical(customerIDStripe);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+STRIPE_SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", currentCustomer.getFirstName()+" "+currentCustomer.getLastName()); // Set the email of the customer
                params.put("email", currentCustomer.getEmail()); // Set the email of the customer
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void getEpherical(String customerID){

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            getClientSecret(customerID,EphericalKey);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+STRIPE_SECRET_KEY);
                header.put("Stripe-Version","2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    private void getClientSecret (String customerID, String ephericalKey ){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            pbPaymentOption.setVisibility(View.GONE);
                            llOrderLayoutPaymentOption.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+STRIPE_SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",customerID);
                params.put("amount",Double.toString(totalAmount).replace(".",""));
                params.put("currency","usd");
                params.put("automatic_payment_methods[enabled]","true");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void PaymentFlow(){
        paymentSheet.presentWithPaymentIntent(
                ClientSecret,
                new PaymentSheet.Configuration("FIT APP",
                        new PaymentSheet.CustomerConfiguration(
                                customerIDStripe,
                                EphericalKey
                        )
                )
        );
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult){
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            //Get the Payment Id (Payment Intent)
            String[] clientSecretStr = ClientSecret.split("_");
            String paymentIntentId = clientSecretStr[0]+"_"+clientSecretStr[1];
            updateOrderAfterPayment(paymentIntentId);
        }else if(paymentSheetResult instanceof  PaymentSheetResult.Canceled){
            Toast.makeText(getContext(),"Payment Cancelled. Please try again later",Toast.LENGTH_SHORT).show();
        }else if(paymentSheetResult instanceof PaymentSheetResult.Failed){
            Toast.makeText(getContext(),"Payment Failed. Please try again later",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOrderAfterPayment(String paymentIntentId){

        pbPaymentOption.setVisibility(View.VISIBLE);
        llOrderLayoutPaymentOption.setVisibility(View.GONE);

        //adding a new payment into the SQL Lite database
        Date newDate = new Date();
        Payment payment = new Payment(paymentIntentId,newDate.getTime(),totalAmount);
        dbHelper.insertPayment(payment);

        //Let's update the payment information for the selfworkout first
        int numAppointmentsToPay = 0;
        for(int i=0;i<orderIdArray.size();i++){
            if (orderTypeArray.get(i) == 2){
                int numRowsUpdated = dbHelper.updateSelfWorkoutAddPayment(
                        orderIdArray.get(i),
                        paymentIntentId,
                        newDate
                );
            }else{
                numAppointmentsToPay++;
            }
        }

        if (numAppointmentsToPay > 0){
            DatabaseReference appRef = CoachMeDatabaseRef.child("appointments");
            List<Task<DataSnapshot>> tasks = new ArrayList<>();
            tasks.add(appRef.get());
            // Create a new task that completes when all tasks in the list complete successfully
            Task<List<DataSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

            allTasks.addOnCompleteListener(new OnCompleteListener<List<DataSnapshot>>() {
                @Override
                public void onComplete(@NonNull Task<List<DataSnapshot>> task) {
                    if(task.isSuccessful()){
                        DataSnapshot appDs = (DataSnapshot) task.getResult().get(0);
                        for(int i=0;i<orderIdArray.size();i++){

                            if (orderTypeArray.get(i) == 1){

                                int numRowsUpdated = dbHelper.updateAppointmentAddPayment(orderIdArray.get(i),
                                        paymentIntentId,
                                        newDate);

                                if (numRowsUpdated > 0){
                                    DataSnapshot ds = appDs.child(orderIdArray.get(i));
                                    Appointment app = ds.getValue(Appointment.class);
                                    app.setPaymentId(paymentIntentId);
                                    app.setPaymentDate(newDate.getTime());
                                    app.setStatus(3); //change to active;
                                    appRef.child(orderIdArray.get(i)).setValue(app);
                                }

                            }

                        }

                        //Send the Notification to the Database
                        sendNotificationTrigger();


                    }else{
                        // At least one task failed
                        Exception error = task.getException();
                        System.out.println("At least one task failed: " + error.getMessage());
                    }
                }
            });
        }else{
            //Send the Notification to the Database
            sendNotificationTrigger();
        }
    }

    private void sendNotificationTrigger(){

        triggerNotificationRef = CoachMeDatabaseRef.child("orderNotifications");

        String notificationDescription = "Purchased successfully! Go to CoachMe and see your new item!";

        OrderNotifcation newNotification = new OrderNotifcation("Purchase Completed!",notificationDescription,
                UserSingleton.getInstance().getUserDeviceToken());
        triggerNotificationRef.push().setValue(newNotification);

        //Move to Cart Detail
        OrderPaymentConfirmedFragment orderConfirmedFragment = new OrderPaymentConfirmedFragment();

        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        // Replace the current fragment with the new one
        fragmentTransaction.replace(R.id.barFrame, orderConfirmedFragment);

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();


    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.barFrame, fragment);
        fragmentTransaction.commit();

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

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    replaceFragment(new OrdersFragment());
                    return true;
                }
                return false;
            }
        });
    }


}