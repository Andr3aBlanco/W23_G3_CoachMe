package com.bawp.coachme.presentation.selfworkout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlanExercise;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SelfworkoutExerciseDetailFragment extends Fragment {

    private SelfWorkoutSessionLog exerciseDetail;
    TextView txtViewExerciseNameDetail;
    ImageView imgViewExercise;
    TextView txtViewExerciseSets;
    TextView txtViewExerciseRepetitions;
    TextView txtViewRestTime;
    ProgressBar pbExerciseDetail;
    LinearLayout llExerciseDetailLayout;

    public SelfworkoutExerciseDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseDetail = (SelfWorkoutSessionLog) getArguments().getSerializable("exerciseDetail");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selfworkout_exercise_detail, container, false);

        SelfWorkoutPlanExercise exercise = exerciseDetail.getSelfWorkoutExercise();

        txtViewExerciseNameDetail = view.findViewById(R.id.txtViewExerciseNameDetail);
        txtViewExerciseSets = view.findViewById(R.id.txtViewExerciseSets);
        txtViewExerciseRepetitions = view.findViewById(R.id.txtViewExerciseRepetitions);
        txtViewRestTime = view.findViewById(R.id.txtViewRestTime);
        imgViewExercise = view.findViewById(R.id.imgViewExercise);

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

        pbExerciseDetail.setVisibility(View.GONE);
        llExerciseDetailLayout.setVisibility(View.VISIBLE);

        return view;
    }
}