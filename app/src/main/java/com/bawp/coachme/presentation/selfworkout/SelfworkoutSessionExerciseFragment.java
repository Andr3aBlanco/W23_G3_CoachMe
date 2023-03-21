package com.bawp.coachme.presentation.selfworkout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bawp.coachme.MainActivity;
import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.utils.DBHelper;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.List;

public class SelfworkoutSessionExerciseFragment extends Fragment {

    List<SelfWorkoutSessionLog> exercisesLog;
    int sessionId;
    ProgressBar pbSelfworkoutSessionExercises;
    LinearLayout llSelfworkoutSessionExercises;
    ProgressBar pbSessionProgress;
    FragmentManager fm;
    Fragment fragment;
    Button btnBackToSessionTypes;
    DBHelper dbHelper;
    RecyclerView selfworkoutExercisesRecyclerView;

    public SelfworkoutSessionExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exercisesLog = (List<SelfWorkoutSessionLog>) getArguments().getSerializable("exercisesLog");
            sessionId = getArguments().getInt("sessionId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selfworkout_session_exercise, container, false);

        dbHelper = new DBHelper(getContext());

        //Check if the navbar has been hidden
        BottomAppBar btnNavigationAppBar =  getActivity().findViewById(R.id.bottomNavBarWrapper);
        if (btnNavigationAppBar.getVisibility() == View.GONE){
            btnNavigationAppBar.setVisibility(View.VISIBLE);
        }

        FloatingActionButton btnActionButton = getActivity().findViewById(R.id.floatingAdd);
        if (btnActionButton.getVisibility() == View.GONE){
            btnActionButton.setVisibility(View.VISIBLE);
        }

        selfworkoutExercisesRecyclerView = view.findViewById(R.id.selfworkoutExercisesRecyclerView);
        pbSelfworkoutSessionExercises = view.findViewById(R.id.pbSelfworkoutSessionExercises);
        llSelfworkoutSessionExercises = view.findViewById(R.id.llSelfworkoutSessionExercises);
        pbSessionProgress = view.findViewById(R.id.pbSessionProgress);

        pbSelfworkoutSessionExercises.setVisibility(View.VISIBLE);
        llSelfworkoutSessionExercises.setVisibility(View.GONE);

        btnBackToSessionTypes = view.findViewById(R.id.btnBackToSessionTypes);

        calculateProgressBar();

        selfworkoutExercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SelfworkoutSessionExRecyclerAdapter swpAdapter = new SelfworkoutSessionExRecyclerAdapter(exercisesLog, getContext(), new SelfworkoutSessionExRecyclerAdapter.SetOnItemClickListener() {
            @Override
            public void onClickItem(int i) {
                SelfWorkoutSessionLog exerciseDetail = exercisesLog.get(i);

                Bundle dataToPass = new Bundle();
                dataToPass.putSerializable("exerciseDetail",(Serializable) exerciseDetail);
                dataToPass.putInt("sessionId",sessionId);

                SelfworkoutExerciseDetailFragment selfworkoutExerciseDetailFragment = new SelfworkoutExerciseDetailFragment();
                selfworkoutExerciseDetailFragment.setArguments(dataToPass);

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.barFrame, selfworkoutExerciseDetailFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack("self-workout-session-exercise-detail");

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        selfworkoutExercisesRecyclerView.setAdapter(swpAdapter);

        btnBackToSessionTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelfWorkoutSession currentSession = dbHelper.getSessionById(sessionId);

                SelfWorkoutSessionType sessionType = currentSession.getSelfworkoutSessionType();
                SelfWorkoutPlan selfWorkoutPlan = sessionType.getSelfWorkoutPlan();
                List<SelfWorkoutSessionType> selfWorkoutSessionTypes = dbHelper.getSessionTypesByPlanId(selfWorkoutPlan.getId());
                boolean isNewSession = false;
                int selfworkoutUserId = currentSession.getSelfWorkoutPlanByUser().getId();

                Bundle passDataToFragment = new Bundle();
                passDataToFragment.putSerializable("sessionTypesList",(Serializable) selfWorkoutSessionTypes);
                passDataToFragment.putSerializable("sessionObj",(Serializable) currentSession ) ;
                passDataToFragment.putSerializable("workoutUserId",selfworkoutUserId ) ;
                passDataToFragment.putBoolean("isNewSession",isNewSession);

                SelfworkoutSessionTypeFragment selfworkoutSessionTypeFragment = new SelfworkoutSessionTypeFragment();
                selfworkoutSessionTypeFragment.setArguments(passDataToFragment);

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.barFrame, selfworkoutSessionTypeFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack("self-workout-session-types-options");

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        pbSelfworkoutSessionExercises.setVisibility(View.GONE);
        llSelfworkoutSessionExercises.setVisibility(View.VISIBLE);

        return view;
    }

    private void calculateProgressBar(){

        int totalExercises = exercisesLog.size();
        int currentProgress = 0;

        for(SelfWorkoutSessionLog ssl : exercisesLog){
            if (ssl.getSessionExerciseStatus() == 3){
                currentProgress++;
            }
        }

        pbSessionProgress.setProgress(currentProgress);
        pbSessionProgress.setMax(totalExercises);

    }
}