/**
 * Class: SelfworkoutFragment.java
 *
 * Fragment that will display one SelfWorkout Plan that is active for the user
 * (because he/she paid for it). It contains the basic info about the plan
 * including the previous SESSIONS he/she did and it can restart a current session
 * o start a new one.
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.presentation.selfworkout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.presentation.home.HomeFragment;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    LinearLayout llTableSessionsDetail;

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

        //Check if the navbar has been hidden
        BottomAppBar btnNavigationAppBar =  getActivity().findViewById(R.id.bottomNavBarWrapper);
        if (btnNavigationAppBar.getVisibility() == View.GONE){
            btnNavigationAppBar.setVisibility(View.VISIBLE);
        }

        FloatingActionButton btnActionButton = getActivity().findViewById(R.id.floatingAdd);
        if (btnActionButton.getVisibility() == View.GONE){
            btnActionButton.setVisibility(View.VISIBLE);
        }

        dbHelper = new DBHelper(getContext());

        //Get the components from the Fragment
        txtViewSelfworkoutName = view.findViewById(R.id.txtViewSelfworkoutName);
        txtViewSelfworkoutMG = view.findViewById(R.id.txtViewSelfworkoutMG);
        txtViewSelfworkoutDifficulty = view.findViewById(R.id.txtViewSelfworkoutDifficulty);
        txtViewSelfworkoutDpW = view.findViewById(R.id.txtViewSelfworkoutDpW);
        txtViewSelfworkoutTotalWeeks = view.findViewById(R.id.txtViewSelfworkoutTotalWeeks);
        btnStartResumeWorkout = view.findViewById(R.id.btnStartResumeWorkout);
        btnRestartWorkout = view.findViewById(R.id.btnRestartWorkout);
        llTableSessionsDetail = view.findViewById(R.id.llTableSessionsDetail);

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

                        Toast.makeText(getContext(),"You have finished your workout for today. If you want restart your session select 'Restart Workout'",Toast.LENGTH_LONG).show();

                    }

                }else{
                    //This is a new session
                    isNewSession = true;
                    moveToSelfWorkoutSessions(sessionTypes,null);
                }

            }
        });

        //if there is an active session, we can allow the user to restart it
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

        //Get the entire list of previous sessions to display in the history table
        populateSessionRecords();

    }

    public void moveToSelfWorkoutSessions(List<SelfWorkoutSessionType> sessionTypes, SelfWorkoutSession session){


        Bundle passDataToFragment = new Bundle();
        passDataToFragment.putSerializable("sessionTypesList",(Serializable) sessionTypes);
        passDataToFragment.putSerializable("sessionObj",(Serializable) session ) ;
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

    public void populateSessionRecords(){
        List<SelfWorkoutSession> sessions = dbHelper.getListSessionsByUser(selfworkoutUserId);

        for (SelfWorkoutSession session : sessions){
            LinearLayout linearLayout = new LinearLayout(getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params.setMargins(0,4,0,0);

            linearLayout.setLayoutParams(params);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView txtViewType = new TextView(getContext());
            txtViewType.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0.5f));
            txtViewType.setText(session.getSelfworkoutSessionType().getSessionType());
            txtViewType.setTextSize(13);
            txtViewType.setTextColor(Color.parseColor("#200E32"));
            txtViewType.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_regular));
            txtViewType.setGravity(Gravity.CENTER);
            txtViewType.setPadding(10, 0, 0, 0);
            linearLayout.addView(txtViewType);

            TextView txtViewDate = new TextView(getContext());
            txtViewDate.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0.5f));
            txtViewDate.setTextSize(13);
            txtViewDate.setTextColor(Color.parseColor("#200E32"));
            txtViewDate.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_regular));
            txtViewDate.setGravity(Gravity.CENTER);
            txtViewDate.setPadding(10, 0, 0, 0);

            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

            if (session.getSessionEndDate() == 0){
                txtViewDate.setText("In Progress");
                linearLayout.addView(txtViewDate);
            }else{
                txtViewDate.setText(format.format(session.getSessionDate()));
                linearLayout.addView(txtViewDate);

                TextView txtViewDuration = new TextView(getContext());
                txtViewDuration.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0.5f));
                txtViewDuration.setTextSize(13);
                txtViewDuration.setTextColor(Color.parseColor("#200E32"));
                txtViewDuration.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_regular));
                txtViewDuration.setGravity(Gravity.CENTER);
                txtViewDuration.setPadding(10, 0, 0, 0);

                Long endDate = session.getSessionEndDate();
                Long startDate = session.getSessionDate();

                long diff = Math.abs(startDate - endDate); // Difference in milliseconds
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);

                txtViewDuration.setText(Long.toString(minutes) + " min");

                linearLayout.addView(txtViewDuration);

            }

            llTableSessionsDetail.addView(linearLayout);

        }

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
                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.barFrame, new HomeFragment());
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });
    }
}

