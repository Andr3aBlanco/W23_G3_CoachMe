package com.bawp.coachme.presentation.selfworkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.List;

public class SelfworkoutSessionExRecyclerFragment extends Fragment {

    private SelfworkoutSessionExRecyclerFragment.RecyclerViewAdapter swpSessionExercisesAdapter;
    private RecyclerView recyclerView;

    private static List<SelfWorkoutSessionLog> exercisesLog;
    private static int sessionId;
    private static SelfworkoutSessionExerciseFragment selfworkoutSessionExerciseFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swpSessionExercisesAdapter = new SelfworkoutSessionExRecyclerFragment.RecyclerViewAdapter(exercisesLog,sessionId, selfworkoutSessionExerciseFragment);
        recyclerView.setAdapter(swpSessionExercisesAdapter);
        return view;
    }

    public static Fragment newInstance(List<SelfWorkoutSessionLog> list, int id, SelfworkoutSessionExerciseFragment parentFragment) {
        exercisesLog = list;
        sessionId = id;
        selfworkoutSessionExerciseFragment = parentFragment;
        return new SelfworkoutSessionExRecyclerFragment();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView mTxtViewExerciseName;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.selfworkout_ses_exercise_cardview_layout, container, false));
            mCardView = itemView.findViewById(R.id.card_container_sw_exercise);
            mTxtViewExerciseName = itemView.findViewById(R.id.txtViewExerciseName);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<SelfworkoutSessionExRecyclerFragment.RecyclerViewHolder> {

        private List<SelfWorkoutSessionLog> exercisesLog;
        private int sessionId;
        private SelfworkoutSessionExerciseFragment parentFragment;

        public RecyclerViewAdapter(List<SelfWorkoutSessionLog> exercisesLog,int sessionId, SelfworkoutSessionExerciseFragment parentFragment) {
            this.exercisesLog = exercisesLog;
            this.sessionId = sessionId;
            this.parentFragment = parentFragment;
        }

        @NonNull
        @Override
        public SelfworkoutSessionExRecyclerFragment.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SelfworkoutSessionExRecyclerFragment.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SelfworkoutSessionExRecyclerFragment.RecyclerViewHolder holder, int position) {
            int selectedPosition = position;
            holder.mTxtViewExerciseName.setText(exercisesLog.get(position).getSelfWorkoutExercise().getExerciseName().toUpperCase());

            //Set the event when the user click on one card
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelfWorkoutSessionLog exerciseDetail = exercisesLog.get(selectedPosition);
                    Bundle dataToPass = new Bundle();
                    dataToPass.putSerializable("exerciseDetail",(Serializable) exerciseDetail);

                    SelfworkoutExerciseDetailFragment selfworkoutExerciseDetailFragment = new SelfworkoutExerciseDetailFragment();
                    selfworkoutExerciseDetailFragment.setArguments(dataToPass);

                    FragmentManager fm = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();

                    // Replace the current fragment with the new one
                    fragmentTransaction.replace(R.id.barFrame, selfworkoutExerciseDetailFragment);

                    // Add the transaction to the back stack
                    fragmentTransaction.addToBackStack(null);

                    // Commit the transaction
                    fragmentTransaction.commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return exercisesLog.size();
        }
    }

}
