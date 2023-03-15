package com.bawp.coachme.presentation.selfworkout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.presentation.order.OrderListRecyclerFragment;
import com.bawp.coachme.presentation.order.OrderPaymentOptionsFragment;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SelfworkoutFragment extends Fragment {

    TextView txtViewSelfworkoutName;
    TextView txtViewSelfworkoutMG;
    TextView txtViewSelfworkoutDifficulty;
    TextView txtViewSelfworkoutDpW;
    TextView txtViewSelfworkoutTotalWeeks;
    ImageView imgViewSelfworkout;
    ProgressBar pbSelfworkoutMain;
    LinearLayout llSelfworkoutMainLayout;
    Button btnStartResumeWorkout;
    Button btnRestartWorkout;
    int selfworkoutUserId;
    String selfworkoutPlanId;
    SelfWorkoutSession currentSession;
    Boolean isNewSession;
    DBHelper dbHelper;

    FragmentManager fm;
    Fragment fragment;

    public SelfworkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selfworkoutUserId = getArguments().getInt("workoutUserId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_selfworkout, container, false);

        //Saving the current fragment object
        SelfworkoutFragment currentFragment = this;

        dbHelper = new DBHelper(getContext());

        //Get the components from the Fragment
        txtViewSelfworkoutName = view.findViewById(R.id.txtViewSelfworkoutName);
        txtViewSelfworkoutMG = view.findViewById(R.id.txtViewSelfworkoutMG);
        txtViewSelfworkoutDifficulty = view.findViewById(R.id.txtViewSelfworkoutDifficulty);
        txtViewSelfworkoutDpW = view.findViewById(R.id.txtViewSelfworkoutDpW);
        txtViewSelfworkoutTotalWeeks = view.findViewById(R.id.txtViewSelfworkoutTotalWeeks);
        btnStartResumeWorkout = view.findViewById(R.id.btnStartResumeWorkout);
        btnRestartWorkout = view.findViewById(R.id.btnRestartWorkout);

        imgViewSelfworkout = view.findViewById(R.id.imgViewSelfworkout);
        pbSelfworkoutMain = view.findViewById(R.id.pbSelfworkoutMain);
        llSelfworkoutMainLayout = view.findViewById(R.id.llSelfworkoutMainLayout);

        pbSelfworkoutMain.setVisibility(View.VISIBLE);
        llSelfworkoutMainLayout.setVisibility(View.GONE);

        //Get the information from the user
        getWorkoutPlan();

        //Get a session if exists
        SelfWorkoutSession session = dbHelper.getTodaySelfWorkoutSession(selfworkoutUserId);

        //Get all the possible session types
        List<SelfWorkoutSessionType> sessionTypes = dbHelper.getSessionTypesByPlanId(selfworkoutPlanId);

        btnStartResumeWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(session != null){
                    //Let's check if the session is in the same day
                    if(session.getSessionStatus() == 1){

                        //We are resuming
                        isNewSession = false;
                        currentSession = session;
                        moveToSelfWorkoutSessions(sessionTypes,session);

                    }else{

                        //The user wants to restart the session
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Attention!");
                        builder.setMessage("You have finished your workout plan for today. Do you want to restart the session?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isNewSession = true;
                                dbHelper.updateSelfWorkoutSessionStatus(session.getId(),1);
                                dbHelper.deleteSelfWorkoutSessionLogsBySessionId(session.getId());

                                SelfWorkoutSession updatedSession = dbHelper.getSessionById(session.getId());

                                dialog.dismiss();
                                moveToSelfWorkoutSessions(sessionTypes, updatedSession);
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }

                }else{
                    //This is a new session
                    isNewSession = true;
                    moveToSelfWorkoutSessions(sessionTypes,null);
                }

            }
        });

        if (session!= null){

            btnRestartWorkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //The user wants to restart the session
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Attention!");
                    builder.setMessage("Are you sure you want to restart your session?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper.deleteSelfWorkoutSessionLogsBySessionId(session.getId());
                            dbHelper.deleteSelfWorkoutSessionBySessionId(session.getId());
                            dialog.dismiss();
                            isNewSession = true;
                            moveToSelfWorkoutSessions(sessionTypes, null);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }else{
            Toast.makeText(getContext(),"No session for today created!",Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    public void getWorkoutPlan(){

        SelfWorkoutPlanByUser swpUser = dbHelper.getSelfWorkoutPlanByUserById(selfworkoutUserId);

        //filling data
        txtViewSelfworkoutName.setText(swpUser.getSelfworkoutplan().getTitle());
        txtViewSelfworkoutMG.setText(swpUser.getSelfworkoutplan().getMainGoals());
        txtViewSelfworkoutDifficulty.setText(swpUser.getSelfworkoutplan().getLevel());
        txtViewSelfworkoutDpW.setText(Integer.toString(swpUser.getSelfworkoutplan().getDaysPerWeek()));
        txtViewSelfworkoutTotalWeeks.setText(swpUser.getSelfworkoutplan().getDuration());

        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Setting the image of the workout plan
        StorageReference imageRef = storage.getReferenceFromUrl(swpUser.getSelfworkoutplan().getPosterUrlFirestore());
        Glide.with(getContext())
                .load(imageRef)
                .into(imgViewSelfworkout);

        selfworkoutPlanId = swpUser.getSelfworkoutplan().getId();

        pbSelfworkoutMain.setVisibility(View.GONE);
        llSelfworkoutMainLayout.setVisibility(View.VISIBLE);

    }

    public void moveToSelfWorkoutSessions(List<SelfWorkoutSessionType> sessionTypes, SelfWorkoutSession session){


        Bundle passDataToFragment = new Bundle();
        passDataToFragment.putSerializable("sessionTypesList",(Serializable) sessionTypes);
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
}

