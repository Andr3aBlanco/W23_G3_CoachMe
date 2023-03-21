package com.bawp.coachme.presentation.selfworkout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlanExercise;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.presentation.order.OrderPaymentOptionsFragment;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelfworkoutSessionTypeFragment extends Fragment {

    List<SelfWorkoutSessionType> selfWorkoutSessionTypes;
    SelfWorkoutSession currentSession;
    Boolean isNewSession;
    int selfworkoutUserId;
    ProgressBar pbSelfworkoutSessionType;
    LinearLayout llSelfworkoutSessionType;
    Button btnBackToWorkoutMain;
    RecyclerView sessionTypeRecyclerView;
    DBHelper dbHelper;

    public SelfworkoutSessionTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selfWorkoutSessionTypes = (List<SelfWorkoutSessionType>) getArguments().getSerializable("sessionTypesList");
            currentSession = (SelfWorkoutSession) getArguments().getSerializable("sessionObj");
            isNewSession = getArguments().getBoolean("isNewSession");
            selfworkoutUserId = getArguments().getInt("workoutUserId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_selfworkout_session_type, container, false);

        btnBackToWorkoutMain = view.findViewById(R.id.btnBackToWorkoutMain);
        sessionTypeRecyclerView = view.findViewById(R.id.sessionTypeRecyclerView);

        pbSelfworkoutSessionType = view.findViewById(R.id.pbSelfworkoutSessionType);
        llSelfworkoutSessionType = view.findViewById(R.id.llSelfworkoutSessionType);

        pbSelfworkoutSessionType.setVisibility(View.VISIBLE);
        llSelfworkoutSessionType.setVisibility(View.GONE);

        dbHelper = new DBHelper(getContext());

        sessionTypeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SelfworkoutSessionTypeRecyclerAdapter sessionTypeAdapter = new SelfworkoutSessionTypeRecyclerAdapter(selfWorkoutSessionTypes, currentSession, getContext(), new SelfworkoutSessionTypeRecyclerAdapter.SetOnItemClickListener() {
            @Override
            public void onClickItem(int i) {
                if (currentSession == null){
                    //This is a new session, we have to create it into the database
                    List<SelfWorkoutPlanExercise> exercisesList = dbHelper.getSelfWorkoutExerciseBySessionTypeId(selfWorkoutSessionTypes.get(i).getId());
                    if (exercisesList.size()>0){
                        currentSession = dbHelper.createNewSession(selfworkoutUserId,selfWorkoutSessionTypes.get(i).getId(),new Date().getTime(),1);
                        moveToExercisesList(currentSession.getId());
                    }else{
                        Toast.makeText(getContext(),"No exercises routine available for this session!",Toast.LENGTH_SHORT).show();
                    }

                }else{
                    if (currentSession.getSelfworkoutSessionType().getId().equals(selfWorkoutSessionTypes.get(i).getId())){
                        //Keep enabled to move to the next screen
                        moveToExercisesList(currentSession.getId());
                    }
                }
            }
        });

        sessionTypeRecyclerView.setAdapter(sessionTypeAdapter);

        btnBackToWorkoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataToPass = new Bundle();
                dataToPass.putInt("workoutUserId",selfworkoutUserId);

                SelfworkoutFragment selfworkoutFragment = new SelfworkoutFragment();
                selfworkoutFragment.setArguments(dataToPass);

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.barFrame, selfworkoutFragment);

                fragmentTransaction.commit();
            }
        });

        pbSelfworkoutSessionType.setVisibility(View.GONE);
        llSelfworkoutSessionType.setVisibility(View.VISIBLE);

        // Inflate the layout for this fragment
        return view;
    }

    public void moveToExercisesList(int sessionId){
        List<SelfWorkoutSessionLog> exercisesLog = dbHelper.getSessionLogs(sessionId);

        if (exercisesLog.size() > 0 ){
            //Let's send the data into the next fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putSerializable("exercisesLog",(Serializable) exercisesLog);
            dataToPass.putInt("sessionId",sessionId);

            SelfworkoutSessionExerciseFragment selfworkoutSessionExerciseFragment = new SelfworkoutSessionExerciseFragment();
            selfworkoutSessionExerciseFragment.setArguments(dataToPass);

            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            // Replace the current fragment with the new one
            fragmentTransaction.replace(R.id.barFrame, selfworkoutSessionExerciseFragment);

            // Add the transaction to the back stack
            fragmentTransaction.addToBackStack("self-workout-session-exercises-options");

            // Commit the transaction
            fragmentTransaction.commit();
        }else{
            Toast.makeText(getContext(),"No exercises routine available for this session!",Toast.LENGTH_SHORT).show();
        }


    }

}
