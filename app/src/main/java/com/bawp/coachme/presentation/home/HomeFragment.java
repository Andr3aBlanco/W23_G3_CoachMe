package com.bawp.coachme.presentation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.utils.DBHelper;

import java.util.List;


public class HomeFragment extends Fragment {

    FrameLayout flAppointments;
    DBHelper dbHelper;
    FragmentManager fm;
    Fragment fragment;
    Fragment fragmentSwp;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        HomeFragment currentFragment = this;

        dbHelper = new DBHelper(getContext());

        flAppointments = view.findViewById(R.id.homeAppFragmentContainer);

        //Getting the current appointments
        int[] statusList = new int[]{3,4};
        List<Appointment> activeAppointments = dbHelper.getAppointmentsByStatusList(statusList);

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

        List<SelfWorkoutPlanByUser> activeSelfWorkouts = dbHelper.getSelfWorkoutPlanByUserByStatus(3);

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

        return view;
    }
}