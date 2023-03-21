package com.bawp.coachme.presentation.selfworkout;

import android.animation.Animator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.utils.DBHelper;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.List;

public class SelfworkoutSessionCompletedFragment extends Fragment {

    int selfWorkoutPlanUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selfWorkoutPlanUser = getArguments().getInt("selfWorkoutPlanUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selfworkout_session_completed, container, false);

        // Inflate the layout for this fragment
        LottieAnimationView animationView = view.findViewById(R.id.animation_view);
        animationView.setAnimation("session_completed_anim.json");

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                //Moving to the Exercises List Fragment

                Bundle dataToPass = new Bundle();
                dataToPass.putInt("workoutUserId",selfWorkoutPlanUser);

                SelfworkoutFragment selfworkoutFragment = new SelfworkoutFragment();
                selfworkoutFragment.setArguments(dataToPass);

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.barFrame, selfworkoutFragment);

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