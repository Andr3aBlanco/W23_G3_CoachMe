package com.bawp.coachme.presentation.selfworkout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.model.SelfWorkoutSessionType;

import java.util.List;

public class SelfworkoutSessionExerciseFragment extends Fragment {

    List<SelfWorkoutSessionLog> exercisesLog;
    int sessionId;
    ProgressBar pbSelfworkoutSessionExercises;
    LinearLayout llSelfworkoutSessionExercises;
    ProgressBar pbSessionProgress;
    FragmentManager fm;
    Fragment fragment;

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

        SelfworkoutSessionExerciseFragment currentFragment = this;

        pbSelfworkoutSessionExercises = view.findViewById(R.id.pbSelfworkoutSessionExercises);
        llSelfworkoutSessionExercises = view.findViewById(R.id.llSelfworkoutSessionExercises);
        pbSessionProgress = view.findViewById(R.id.pbSessionProgress);

        pbSelfworkoutSessionExercises.setVisibility(View.VISIBLE);
        llSelfworkoutSessionExercises.setVisibility(View.GONE);

        calculateProgressBar();

        fm = getActivity().getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.sessionExerciseFragmentContainer);
        if (fragment == null){
            fragment = SelfworkoutSessionExRecyclerFragment.newInstance(exercisesLog,sessionId,currentFragment );

            fm.beginTransaction()
                    .add(R.id.sessionExerciseFragmentContainer,fragment)
                    .commit();
        }else{
            fragment = SelfworkoutSessionExRecyclerFragment.newInstance(exercisesLog,sessionId,currentFragment);

            fm.beginTransaction()
                    .replace(R.id.sessionExerciseFragmentContainer,fragment)
                    .commit();
        }

        pbSelfworkoutSessionExercises.setVisibility(View.GONE);
        llSelfworkoutSessionExercises.setVisibility(View.VISIBLE);

        return view;
    }

    private void calculateProgressBar(){

        int totalExercises = exercisesLog.size();
        Log.d("HELLO",Integer.toString(totalExercises));
        int currentProgress = 0;

        for(SelfWorkoutSessionLog ssl : exercisesLog){
            if (ssl.getSessionExerciseStatus() == 2){
                currentProgress++;
            }
        }

        pbSessionProgress.setProgress(currentProgress);
        pbSessionProgress.setMax(totalExercises);

    }
}