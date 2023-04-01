/**
 * Class: SelfworkoutExerciseCompletedFragment.java
 *
 * Fragment that will display an animation when one exercise has been completed
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.presentation.selfworkout;

import android.animation.Animator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.DBHelper;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.List;

public class SelfworkoutExerciseCompletedFragment extends Fragment {


    DBHelper dbHelper;
    int sessionId;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sessionId = getArguments().getInt("sessionId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selfworkout_exercise_completed, container, false);

        dbHelper = new DBHelper(getContext());

        //Remove the bottomBar
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
                //Let's validate if all exercises have been completed
                List<SelfWorkoutSessionLog> exercisesLog =dbHelper.getSessionLogs(sessionId);
                //Moving to the Exercises List Fragment
                Bundle dataToPass = new Bundle();
                dataToPass.putSerializable("exercisesLog",(Serializable) exercisesLog);
                dataToPass.putInt("sessionId",sessionId);

                SelfworkoutSessionExerciseFragment selfworkoutSessionExerciseFragment = new SelfworkoutSessionExerciseFragment();
                selfworkoutSessionExerciseFragment.setArguments(dataToPass);

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.barFrame, selfworkoutSessionExerciseFragment);

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
                    return true;
                }
                return false;
            }
        });
    }
}