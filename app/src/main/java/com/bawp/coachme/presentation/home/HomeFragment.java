package com.bawp.coachme.presentation.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Payment;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.presentation.selfworkout.SelfworkoutSessionTypeFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class HomeFragment extends Fragment {

    DBHelper dbHelper;
    FragmentManager fm;
    Fragment fragment;
    Fragment fragmentSwp;
    ProgressBar pbHomeFragment;
    LinearLayout llHomeFragment;
    FrameLayout flAppointments;
    FrameLayout flSelfWorkouts;
    LinearLayout llNoSwpAvailable;
    LinearLayout llNoAppAvailable;
    Button btnGoToWorkoutMktp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        HomeFragment currentFragment = this;

        dbHelper = new DBHelper(getContext());

        flAppointments = view.findViewById(R.id.homeAppFragmentContainer);
        flSelfWorkouts = view.findViewById(R.id.homeSwpFragmentContainer);
        llNoSwpAvailable = view.findViewById(R.id.llNoSwpAvailable);
        llNoAppAvailable = view.findViewById(R.id.llNoAppsAvailable);

        pbHomeFragment = view.findViewById(R.id.pbHomeFragment);
        llHomeFragment = view.findViewById(R.id.llHomeFragmentLayout);

        pbHomeFragment.setVisibility(View.VISIBLE);
        llHomeFragment.setVisibility(View.GONE);

        //Getting the current appointments
        int[] statusList = new int[]{3,4};
        List<Appointment> activeAppointments = dbHelper.getAppointmentsByStatusList(statusList);

        if (activeAppointments.size()>0){
            //Display into recycler fragment
            fm = getActivity().getSupportFragmentManager();
            fragment = fm.findFragmentById(R.id.homeAppFragmentContainer);
            if (fragment == null){
                fragment = HomeAppRecyclerFragment.newInstance(activeAppointments,currentFragment );

                fm.beginTransaction()
                        .add(R.id.homeAppFragmentContainer,fragment)
                        .commit();
            }else{
                fragment = HomeAppRecyclerFragment.newInstance(activeAppointments,currentFragment);

                fm.beginTransaction()
                        .replace(R.id.homeAppFragmentContainer,fragment)
                        .commit();
            }
            flAppointments.setVisibility(View.VISIBLE);
            llNoAppAvailable.setVisibility(View.GONE);
        }else{
            flAppointments.setVisibility(View.GONE);
            llNoAppAvailable.setVisibility(View.VISIBLE);
        }

        List<SelfWorkoutPlanByUser> activeSelfWorkouts = dbHelper.getSelfWorkoutPlanByUserByStatus(3);

        if (activeSelfWorkouts.size()>0){
            //Display into recycler fragment
            fm = getActivity().getSupportFragmentManager();
            fragmentSwp = fm.findFragmentById(R.id.homeSwpFragmentContainer);
            if (fragmentSwp == null){
                fragmentSwp = HomeSwpRecyclerFragment.newInstance(activeSelfWorkouts,currentFragment );

                fm.beginTransaction()
                        .add(R.id.homeSwpFragmentContainer,fragmentSwp)
                        .commit();
            }else{
                fragmentSwp = HomeSwpRecyclerFragment.newInstance(activeSelfWorkouts,currentFragment);

                fm.beginTransaction()
                        .replace(R.id.homeSwpFragmentContainer,fragmentSwp)
                        .commit();
            }
            flSelfWorkouts.setVisibility(View.VISIBLE);
            llNoSwpAvailable.setVisibility(View.GONE);
        }else{
            flSelfWorkouts.setVisibility(View.GONE);
            llNoSwpAvailable.setVisibility(View.VISIBLE);
        }

        btnGoToWorkoutMktp = view.findViewById(R.id.btnGoToWorkoutMktp);

        btnGoToWorkoutMktp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SelfWorkoutPlan> selfWorkoutPlanList = dbHelper.getSelfWorkoutPlanAvailable(UserSingleton.getInstance().getUserId());

                if (selfWorkoutPlanList.size() > 0){
                    HomeSelfWorkoutMarketplaceFragment marketplaceFragment = new HomeSelfWorkoutMarketplaceFragment();

                    FragmentManager fm = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();

                    // Replace the current fragment with the new one
                    fragmentTransaction.replace(R.id.barFrame, marketplaceFragment);

                    // Add the transaction to the back stack
                    fragmentTransaction.addToBackStack("self-workout-marketplace");

                    // Commit the transaction
                    fragmentTransaction.commit();
                }else{
                    Toast.makeText(getContext(),"No Self Workout Plans available for you :(",Toast.LENGTH_SHORT).show();
                }


            }
        });

        pbHomeFragment.setVisibility(View.GONE);
        llHomeFragment.setVisibility(View.VISIBLE);


        return view;
    }
}