package com.bawp.coachme.presentation.home;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Payment;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.presentation.feedback.AppHistoryListFragment;
import com.bawp.coachme.presentation.selfworkout.SelfworkoutFragment;
import com.bawp.coachme.presentation.selfworkout.SelfworkoutSessionTypeFragment;
import com.bawp.coachme.presentation.trainermap.TrainerSearchFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: HomeFragment.java
 *
 * Main fragment after the loading splash screen finished. It has the list
 * of current appointments and self-workout plans purchased.
 *
 * A user can book new appointments, see previous appointments, buy new
 * self-workout plans and see the detail of each item (appointments & self-workout plans)
 *
 * @author Luis Miguel Miranda / Andrea Blanco
 * @version 1.0
 */

public class HomeFragment extends Fragment {

    DBHelper dbHelper;
    ProgressBar pbHomeFragment;
    LinearLayout llHomeFragment;
    LinearLayout llNoSwpAvailable;
    LinearLayout llNoAppAvailable;
    Button btnGoToWorkoutMktp;
    Button btnViewPreviousAppointments;
    Button btnBookNewApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DBHelper(getContext());

        llNoSwpAvailable = view.findViewById(R.id.llNoSwpAvailable);
        llNoAppAvailable = view.findViewById(R.id.llNoAppsAvailable);

        pbHomeFragment = view.findViewById(R.id.pbHomeFragment);
        llHomeFragment = view.findViewById(R.id.llHomeFragmentLayout);

        pbHomeFragment.setVisibility(View.VISIBLE);
        llHomeFragment.setVisibility(View.GONE);

        //Getting the current appointments
        int[] statusList = new int[]{3,4};
        List<Appointment> activeAppointments = dbHelper.getAppointmentsByStatusList(statusList);
        RecyclerView homeAppRecyclerView = view.findViewById(R.id.homeAppRecyclerView);

        if (activeAppointments.size()>0){
            homeAppRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            HomeAppRecyclerAdapter appRecyclerAdapter = new HomeAppRecyclerAdapter(activeAppointments, getContext(), new HomeAppRecyclerAdapter.SetOnItemClickListener() {
                @Override
                public void setOnItemClick(int i) {
                    Log.d("INFO","This will include the method to display the detail of the appointment");
                }
            });
            homeAppRecyclerView.setAdapter(appRecyclerAdapter);
            llNoAppAvailable.setVisibility(View.GONE);
        }else{
            homeAppRecyclerView.setVisibility(View.GONE);
            llNoAppAvailable.setVisibility(View.VISIBLE);
        }

        List<SelfWorkoutPlanByUser> activeSelfWorkouts = dbHelper.getSelfWorkoutPlanByUserByStatus(3);
        RecyclerView homeSwpRecyclerView = view.findViewById(R.id.homeSwpRecyclerView);

        if (activeSelfWorkouts.size()>0){
            //Display into recycler view
            homeSwpRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            HomeSwpRecyclerAdapter swpRecyclerAdapter = new HomeSwpRecyclerAdapter(activeSelfWorkouts, getContext(), new HomeSwpRecyclerAdapter.SetOnItemClickListener() {
                @Override
                public void onItemClick(int i) {
                    Bundle passDataToFragment = new Bundle();
                    passDataToFragment.putSerializable("workoutUserId",activeSelfWorkouts.get(i).getId());

                    SelfworkoutFragment selfworkoutFragment = new SelfworkoutFragment();
                    selfworkoutFragment.setArguments(passDataToFragment);

                    FragmentManager fm = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();

                    // Replace the current fragment with the new one
                    fragmentTransaction.replace(R.id.barFrame, selfworkoutFragment);

                    // Add the transaction to the back stack
                    fragmentTransaction.addToBackStack("self-workout-main");

                    // Commit the transaction
                    fragmentTransaction.commit();
                }
            });
            homeSwpRecyclerView.setAdapter(swpRecyclerAdapter);

            llNoSwpAvailable.setVisibility(View.GONE);
        }else{
            homeSwpRecyclerView.setVisibility(View.GONE);
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


        btnViewPreviousAppointments = view.findViewById(R.id.btnViewPreviousAppointments);
        btnViewPreviousAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the previous appointments

                List<Appointment> prevAppointments = dbHelper.getAppointmentsByCustomerIdAndStatus(UserSingleton.getInstance().getUserId(), 5);

                System.out.println("OnClick for PrevApp Size: " + prevAppointments.size());
                if(prevAppointments.size() > 0 ){

                    // Load the prevApp fragment
                    AppHistoryListFragment historyFragment = new AppHistoryListFragment();
                    FragmentManager fm = getParentFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    // Pass the List
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("PREVAPPLIST", new ArrayList<>(prevAppointments));
                    historyFragment.setArguments(bundle);

                    //Replace with App Recycler
                    ft.replace(R.id.barFrame, historyFragment);
                    ft.addToBackStack("Appointment-History");
                    ft.commit();

                }else{

                    Toast.makeText(getContext(), "No previous appointments available", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnBookNewApp = view.findViewById(R.id.btnHomeBookNew);

        btnBookNewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainerSearchFragment trainerSearchFragment = new TrainerSearchFragment();
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                ft.replace(R.id.barFrame, trainerSearchFragment);
                ft.addToBackStack("Trainer-Search");
                ft.commit();
            }
        });


        return view; // chnage this back to view
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