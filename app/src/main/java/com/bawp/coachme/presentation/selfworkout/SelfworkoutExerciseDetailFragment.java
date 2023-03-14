package com.bawp.coachme.presentation.selfworkout;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.bawp.coachme.model.SelfWorkoutPlanExercise;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.utils.DBHelper;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SelfworkoutExerciseDetailFragment extends Fragment {

    private SelfWorkoutSessionLog exerciseDetail;
    private int sessionId;
    TextView txtViewExerciseNameDetail;
    ImageView imgViewExercise;
    TextView txtViewExerciseSets;
    TextView txtViewExerciseRepetitions;
    TextView txtViewRestTime;
    ProgressBar pbExerciseDetail;
    LinearLayout llExerciseDetailLayout;
    Button btnMarkCompleted;
    DBHelper dbHelper;

    public SelfworkoutExerciseDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseDetail = (SelfWorkoutSessionLog) getArguments().getSerializable("exerciseDetail");
            sessionId = getArguments().getInt("sessionId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selfworkout_exercise_detail, container, false);

        dbHelper = new DBHelper(getContext());

        SelfWorkoutPlanExercise exercise = exerciseDetail.getSelfWorkoutExercise();

        //update exercise activity if the current status is "pending" (status 1)
        if (exerciseDetail.getSessionExerciseStatus() == 1){
            //changing to status "in progress" (status 2)
            dbHelper.updateSelfWorkoutSessionLogByStatus(exerciseDetail.getId(), 2);
        }

        txtViewExerciseNameDetail = view.findViewById(R.id.txtViewExerciseNameDetail);
        txtViewExerciseSets = view.findViewById(R.id.txtViewExerciseSets);
        txtViewExerciseRepetitions = view.findViewById(R.id.txtViewExerciseRepetitions);
        txtViewRestTime = view.findViewById(R.id.txtViewRestTime);
        imgViewExercise = view.findViewById(R.id.imgViewExercise);
        btnMarkCompleted = view.findViewById(R.id.btnMarkCompleted);

        pbExerciseDetail = view.findViewById(R.id.pbExerciseDetail);
        llExerciseDetailLayout = view.findViewById(R.id.llExerciseDetailLayout);

        pbExerciseDetail.setVisibility(View.VISIBLE);
        llExerciseDetailLayout.setVisibility(View.GONE);

        //Filling the data
        txtViewExerciseNameDetail.setText(exercise.getExerciseName());
        txtViewExerciseSets.setText(Integer.toString(exercise.getNumSets()));
        txtViewExerciseRepetitions.setText(exercise.getRepetitions());
        txtViewRestTime.setText(exercise.getRestTime());

        //Downloading image from Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Setting the image of the workout plan
        StorageReference imageRef = storage.getReferenceFromUrl(exercise.getExerciseImageURLFirestore());
        Glide.with(getContext())
                .load(imageRef)
                .into(imgViewExercise);

        btnMarkCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mark the exercise activity (log) into status completed (status 3)
                int numRowsUpdated = dbHelper.updateSelfWorkoutSessionLogByStatus(exerciseDetail.getId(),3);
                if (numRowsUpdated > 0){
                    //We can proceed
                    Intent moveToCompletedActivity = new Intent(getActivity(),SelfworkoutExerciseCompletedActivity.class);
                    moveToCompletedActivity.putExtra("sessionId",sessionId);
                    startActivity(moveToCompletedActivity);
                }else{
                    Toast.makeText(getContext(),"There is an error while updating the database",Toast.LENGTH_SHORT).show();
                }
            }
        });

        pbExerciseDetail.setVisibility(View.GONE);
        llExerciseDetailLayout.setVisibility(View.VISIBLE);

        return view;
    }
}