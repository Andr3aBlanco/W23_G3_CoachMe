package com.bawp.coachme.presentation.order;

/**
 * Class: OrderPaymentConfirmedFragment.java
 *
 * Fragment class that will hold the screen of the successful purchase.
 *
 * This class includes an animation element that comes from the Lottie package (Airbnb)
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.bawp.coachme.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OrderPaymentConfirmedFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_payment_confirmed, container, false);

        //Hiding the navbar
        BottomAppBar btnNavigationAppBar =  getActivity().findViewById(R.id.bottomNavBarWrapper);
        btnNavigationAppBar.setVisibility(View.GONE);

        FloatingActionButton btnActionButton = getActivity().findViewById(R.id.floatingAdd);
        btnActionButton.setVisibility(View.GONE);

        // Inflate the layout for this fragment
        LottieAnimationView animationView = view.findViewById(R.id.animation_view);
        animationView.setAnimation("check_mark_animation.json");


        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                //Move to Cart Detail
                OrdersFragment ordersFragment = new OrdersFragment();

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.barFrame, ordersFragment);

                // Commit the transaction
                fragmentTransaction.commit();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        animationView.playAnimation();

        return view;
    }
}