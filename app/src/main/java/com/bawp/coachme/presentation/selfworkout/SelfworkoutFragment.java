package com.bawp.coachme.presentation.selfworkout;

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

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.presentation.order.OrderListRecyclerFragment;
import com.bawp.coachme.presentation.order.OrderPaymentOptionsFragment;
import com.bawp.coachme.presentation.order.OrdersFragment;
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

    String selfworkoutUserId;
    String selfworkoutPlanId;
    String swpSessionRefKey;
    SelfWorkoutSession currentSession;
    Boolean isNewSession;
    List<SelfWorkoutSessionType> selfworkoutSessionTypes;
    ArrayList<String> selfworkoutSessionTypeIds;

    FragmentManager fm;
    Fragment fragment;

    DatabaseReference CoachMeDatabaseRef;
    FirebaseDatabase CoachMeDatabaseInstance;

    public SelfworkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selfworkoutUserId = getArguments().getString("workoutUserId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_selfworkout, container, false);

        //Saving the current fragment object
        SelfworkoutFragment currentFragment = this;

        //Preparing the database reference to firebase
        CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();

        //Get the components from the Fragment
        txtViewSelfworkoutName = view.findViewById(R.id.txtViewSelfworkoutName);
        txtViewSelfworkoutMG = view.findViewById(R.id.txtViewSelfworkoutMG);
        txtViewSelfworkoutDifficulty = view.findViewById(R.id.txtViewSelfworkoutDifficulty);
        txtViewSelfworkoutDpW = view.findViewById(R.id.txtViewSelfworkoutDpW);
        txtViewSelfworkoutTotalWeeks = view.findViewById(R.id.txtViewSelfworkoutTotalWeeks);
        btnStartResumeWorkout = view.findViewById(R.id.btnStartResumeWorkout);

        imgViewSelfworkout = view.findViewById(R.id.imgViewSelfworkout);
        pbSelfworkoutMain = view.findViewById(R.id.pbSelfworkoutMain);
        llSelfworkoutMainLayout = view.findViewById(R.id.llSelfworkoutMainLayout);

        pbSelfworkoutMain.setVisibility(View.VISIBLE);
        llSelfworkoutMainLayout.setVisibility(View.GONE);

        //Get the information from the user
        getWorkoutPlan();

        btnStartResumeWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validate if it is a resume or a start
                Date currentDate = new Date();
                // Set the start time to the beginning of the current day
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                //set initial dates
                calendar.set(year, month, dayOfMonth, 0, 0, 0);
                Long startTime = calendar.getTime().getTime();
                calendar.set(year, month, dayOfMonth, 23, 59, 59);
                Long endTime = calendar.getTime().getTime();

                selfworkoutSessionTypes = new ArrayList<>();
                selfworkoutSessionTypeIds = new ArrayList<>();

                //For now, let's move to the next fragment with the list of session Types.
                DatabaseReference swpSessionTypesRef = CoachMeDatabaseRef.child("selfWorkoutSessionTypes");
                DatabaseReference swpSessionRef = CoachMeDatabaseRef.child("selfWorkoutSessions");

                List<Task<DataSnapshot>> tasks = new ArrayList<>();

                Query swpSessionTypeQuery = swpSessionTypesRef.orderByChild("selfworkoutPlanId").equalTo(selfworkoutPlanId);
                Query swpSessionQuery = swpSessionRef.orderByChild("sessionDate").startAt(startTime).endAt(endTime);

                tasks.add(swpSessionTypeQuery.get());
                tasks.add(swpSessionQuery.get());

                // Create a new task that completes when all tasks in the list complete successfully
                Task<List<DataSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

                allTasks.addOnCompleteListener(new OnCompleteListener<List<DataSnapshot>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<DataSnapshot>> task) {

                        DataSnapshot dsSessionType = task.getResult().get(0);
                        DataSnapshot dsSessions = task.getResult().get(1);

                        for(DataSnapshot ds : dsSessionType.getChildren()){
                            SelfWorkoutSessionType swST = ds.getValue(SelfWorkoutSessionType.class);
                            selfworkoutSessionTypeIds.add(ds.getKey());
                            selfworkoutSessionTypes.add(swST);
                        }

                        if (dsSessions.getValue() == null){
                            //that means that there is not sessions available for today
                            Date newSessionDate = new Date();
                            SelfWorkoutSession currentSession = new SelfWorkoutSession(selfworkoutUserId,null,newSessionDate.getTime(),1);
                            swpSessionRefKey = swpSessionRef.push().getKey();
                            swpSessionRef.child(swpSessionRefKey).setValue(currentSession);
                            isNewSession = true;
                        }else{
                            //if not, let see if the user has a session today
                            SelfWorkoutSession swSession = null;

                            for(DataSnapshot ds: dsSessions.getChildren()){
                                SelfWorkoutSession swSessionTemp = ds.getValue(SelfWorkoutSession.class);
                                if(swSessionTemp.getSwpUserId().equals(selfworkoutUserId)){
                                    swSession = swSessionTemp;
                                    swpSessionRefKey = ds.getKey();
                                }
                            }

                            if(swSession == null){
                                //finally we conclude that this is a new session
                                //create a new Session
                                Date newSessionDate = new Date();
                                SelfWorkoutSession currentSession = new SelfWorkoutSession(selfworkoutUserId,null,newSessionDate.getTime(),1);
                                swpSessionRefKey = swpSessionRef.push().getKey();
                                swpSessionRef.child(swpSessionRefKey).setValue(currentSession);
                                isNewSession = true;
                            }else{
                                //it's not a new one, that means that user is resuming
                                isNewSession = false;
                                currentSession = swSession;
                            }
                        }

                        Bundle passDataToFragment = new Bundle();
                        passDataToFragment.putSerializable("swST",(Serializable) selfworkoutSessionTypes);
                        passDataToFragment.putString("sessionId",swpSessionRefKey);
                        passDataToFragment.putSerializable("sessionObj",(Serializable) currentSession ) ;
                        passDataToFragment.putBoolean("isNewSession",isNewSession);
                        passDataToFragment.putStringArrayList("swSTId",selfworkoutSessionTypeIds);

                        SelfworkoutSessionTypeFragment selfworkoutSessionTypeFragment = new SelfworkoutSessionTypeFragment();
                        selfworkoutSessionTypeFragment.setArguments(passDataToFragment);

                        FragmentManager fm = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();

                        // Replace the current fragment with the new one
                        fragmentTransaction.replace(R.id.barFrame, selfworkoutSessionTypeFragment);

                        // Add the transaction to the back stack
                        fragmentTransaction.addToBackStack(null);

                        // Commit the transaction
                        fragmentTransaction.commit();
                    }
                });

            }
        });

        return view;
    }

    public void getWorkoutPlan(){

        DatabaseReference swpRef = CoachMeDatabaseRef.child("selfWorkoutPlans");
        DatabaseReference swpByUsersRef = CoachMeDatabaseRef.child("selfWorkoutPlansByUser");

        List<Task<DataSnapshot>> tasks = new ArrayList<>();

        tasks.add(swpRef.get());
        tasks.add(swpByUsersRef.get());

        // Create a new task that completes when all tasks in the list complete successfully
        Task<List<DataSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

        allTasks.addOnCompleteListener(new OnCompleteListener<List<DataSnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<DataSnapshot>> task) {
                // All tasks completed successfully
                DataSnapshot snapSwpByRef = task.getResult().get(0);
                DataSnapshot snapSwpUserByRef = task.getResult().get(1);

                SelfWorkoutPlanByUser swpByUser = snapSwpUserByRef.child(selfworkoutUserId).getValue(SelfWorkoutPlanByUser.class);
                SelfWorkoutPlan swp = snapSwpByRef.child(swpByUser.getSelfworkoutplanId()).getValue(SelfWorkoutPlan.class);

                //filling data
                txtViewSelfworkoutName.setText(swp.getTitle());
                txtViewSelfworkoutMG.setText(swp.getMainGoals());
                txtViewSelfworkoutDifficulty.setText(swp.getLevel());
                txtViewSelfworkoutDpW.setText(Integer.toString(swp.getDaysPerWeek()));
                txtViewSelfworkoutTotalWeeks.setText(swp.getDuration());

                FirebaseStorage storage = FirebaseStorage.getInstance();

                //Setting the image of the workout plan
                StorageReference imageRef = storage.getReferenceFromUrl(swp.getPosterUrlFirestore());
                Glide.with(getContext())
                        .load(imageRef)
                        .into(imgViewSelfworkout);

                selfworkoutPlanId = swpByUser.getSelfworkoutplanId();

                pbSelfworkoutMain.setVisibility(View.GONE);
                llSelfworkoutMainLayout.setVisibility(View.VISIBLE);

            }
        });

    }
}