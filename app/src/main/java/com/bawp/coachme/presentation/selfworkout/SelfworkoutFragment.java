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

                SelfWorkoutSession session = dbHelper.getActiveSelfWorkoutSession(selfworkoutUserId);
                List<SelfWorkoutSessionType> sessionTypes = dbHelper.getSessionTypesByPlanId(selfworkoutPlanId);

                if(session != null){
                    //Let's check if the session is in the same day
                    if(session.getSessionDate() >= startTime & session.getSessionDate() <= endTime){

                        //We are resuming
                        isNewSession = false;
                        currentSession = session;
                        Log.d("SESSION","RESUMING");

                    }else{

                        //The user starts a session days before and he/she forgot to finish it
                        //We have to restart it
                        isNewSession = true;
                        dbHelper.updateSelfWorkoutSessionStatus(session.getId(),2);
                        Log.d("SESSION","RESTART");
                    }

                }else{
                    //This is a new session
                    isNewSession = true;
                }

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
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();


            }
        });

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
}