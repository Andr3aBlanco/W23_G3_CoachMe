package com.bawp.coachme.presentation.order;

/**
 * Class: OrderHistoryFragment.java
 *
 * Fragment that will show the entire list of Orders. Each
 * order element is going to be displayed thanks to the OrderHistoryRecyclerAdapter class
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Order;
import com.bawp.coachme.model.Payment;
import com.bawp.coachme.utils.DBHelper;

import java.util.List;


public class OrderHistoryFragment extends Fragment {

    DBHelper dbHelper;
    LinearLayout llNoOrderHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        llNoOrderHistory = view.findViewById(R.id.llNoOrderHistory);

        dbHelper = new DBHelper(getContext());

        List<Payment> orderHistory = dbHelper.getUserOrderHistory();

        RecyclerView orderHistoryRecyclerView = view.findViewById(R.id.orderHistoryRecyclerView);

        if (orderHistory.size() > 0){
            orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            OrderHistoryRecyclerAdapter orderHistoryRecyclerAdapter = new OrderHistoryRecyclerAdapter(orderHistory,getContext());
            orderHistoryRecyclerView.setAdapter(orderHistoryRecyclerAdapter);

            llNoOrderHistory.setVisibility(View.GONE);
        }else{
            orderHistoryRecyclerView.setVisibility(View.GONE);
            llNoOrderHistory.setVisibility(View.VISIBLE);
        }



        return view;
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