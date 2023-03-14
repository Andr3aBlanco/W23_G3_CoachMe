package com.bawp.coachme.presentation.selfworkout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.bawp.coachme.ProfileFragment;
import com.bawp.coachme.R;
import com.bawp.coachme.StatsFragment;
import com.bawp.coachme.databinding.ActivityMainBinding;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.presentation.home.HomeFragment;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.DBHelper;

import java.io.Serializable;
import java.util.List;

public class SelfworkoutExerciseCompletedActivity extends AppCompatActivity {

    private int sessionId;
    private ActivityMainBinding binding;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfworkout_exercise_completed);

        sessionId = getIntent().getIntExtra("sessionId",0);
        dbHelper = new DBHelper(this);

        // Inflate the layout for this fragment
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("check_mark_animation.json");

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                fragmentBinding();
                moveToExerciseLogFragment();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        animationView.playAnimation();


    }

    private void fragmentBinding(){
        //bind
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.menu_home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.menu_orders:
                    replaceFragment(new OrdersFragment());

                    break;

                case R.id.menu_profile:
                    replaceFragment(new ProfileFragment());

                    break;

                case R.id.menu_stats:
                    replaceFragment(new StatsFragment());

                    break;

                case R.id.menu_add:
                    replaceFragment(new HomeFragment());

                    break;

            }

            return true;
        });
    }

    private void moveToExerciseLogFragment(){

        List<SelfWorkoutSessionLog> exercisesLog = dbHelper.getSessionLogs(sessionId);

        SelfworkoutSessionExerciseFragment fragment = new SelfworkoutSessionExerciseFragment();
        Bundle dataToPass = new Bundle();
        dataToPass.putSerializable("exercisesLog",(Serializable) exercisesLog);
        dataToPass.putInt("sessionId",sessionId);

        fragment.setArguments(dataToPass);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.barFrame, fragment)
                .addToBackStack(null)
                .commit();

    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.barFrame, fragment);
        fragmentTransaction.commit();

    }
}