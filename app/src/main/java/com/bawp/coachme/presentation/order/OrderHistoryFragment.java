package com.bawp.coachme.presentation.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
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
    FragmentManager fm;
    Fragment fragment;
    OrderHistoryFragment parentFragment;
    FrameLayout flOrderHistory;
    LinearLayout llNoOrderHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        flOrderHistory = view.findViewById(R.id.orderHistoryFragmentContainer);
        llNoOrderHistory = view.findViewById(R.id.llNoOrderHistory);

        dbHelper = new DBHelper(getContext());

        parentFragment = this;

        List<Payment> orderHistory = dbHelper.getUserOrderHistory();

        if (orderHistory.size() > 0){
            fm = getActivity().getSupportFragmentManager();
            fragment = fm.findFragmentById(R.id.orderHistoryFragmentContainer);
            if (fragment == null){
                fragment = OrderHistoryRecyclerFragment.newInstance(orderHistory, parentFragment );

                fm.beginTransaction()
                        .add(R.id.orderHistoryFragmentContainer,fragment)
                        .commit();
            }else{
                fragment = OrderHistoryRecyclerFragment.newInstance(orderHistory, parentFragment);

                fm.beginTransaction()
                        .replace(R.id.orderHistoryFragmentContainer,fragment)
                        .commit();
            }
            flOrderHistory.setVisibility(View.VISIBLE);
            llNoOrderHistory.setVisibility(View.GONE);
        }else{
            flOrderHistory.setVisibility(View.GONE);
            llNoOrderHistory.setVisibility(View.VISIBLE);
        }



        return view;
    }
}