package com.bawp.coachme.presentation.selfworkout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlanExercise;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.utils.DBHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SelfworkoutSessionTypeRecyclerFragment extends Fragment {

    private SelfworkoutSessionTypeRecyclerFragment.RecyclerViewAdapter swpSessionTypeAdapter;
    private RecyclerView recyclerView;
    private static List<SelfWorkoutSessionType> swpSTList;
    private static SelfWorkoutSession session;
    private static int selfworkoutUserId;
    private static SelfworkoutSessionTypeFragment selfworkoutSessionTypeFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swpSessionTypeAdapter = new SelfworkoutSessionTypeRecyclerFragment.RecyclerViewAdapter(swpSTList, session,selfworkoutUserId,selfworkoutSessionTypeFragment);
        recyclerView.setAdapter(swpSessionTypeAdapter);
        return view;
    }

    public static Fragment newInstance(List<SelfWorkoutSessionType> list, SelfWorkoutSession ses,int workoutId, SelfworkoutSessionTypeFragment parentFragment) {
        swpSTList = list;
        selfworkoutSessionTypeFragment = parentFragment;
        session = ses;
        selfworkoutUserId = workoutId;
        return new SelfworkoutSessionTypeRecyclerFragment();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        TextView mTxtViewSessionType;
        ImageView mImgViewSessionType;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.selfworkout_session_cardview_layout, container, false));
            mCardView = itemView.findViewById(R.id.card_container_sw_st);
            mTxtViewSessionType = itemView.findViewById(R.id.txtViewSessionType);
            mImgViewSessionType = itemView.findViewById(R.id.imgViewSessionType);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<SelfworkoutSessionTypeRecyclerFragment.RecyclerViewHolder> {

        private List<SelfWorkoutSessionType> swpSTList;
        private SelfWorkoutSession session;
        private int selfworkoutUserId;
        private SelfworkoutSessionTypeFragment parentFragment;
        private DBHelper dbHelper;

        public RecyclerViewAdapter(List<SelfWorkoutSessionType> swpSTList, SelfWorkoutSession session,int selfworkoutUserId, SelfworkoutSessionTypeFragment parentFragment) {
            this.swpSTList = swpSTList;
            this.session = session;
            this.parentFragment = parentFragment;
            this.dbHelper = new DBHelper(getContext());
            this.selfworkoutUserId = selfworkoutUserId;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SelfworkoutSessionTypeRecyclerFragment.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
            int selectedPosition = position;

            holder.mTxtViewSessionType.setText(swpSTList.get(position).getSessionType().toUpperCase());

            //getting image from session type
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Setting the image of the workout plan
            StorageReference imageRef = storage.getReferenceFromUrl(swpSTList.get(position).getSessionTypeIconURLFirestore());

            //Download image
            Glide.with(getContext())
                    .load(imageRef)
                    .into(holder.mImgViewSessionType);

            //Set an event when the CardView has been clicked
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //check if we don't have any log for this session
                    if (session == null){
                        //This is a new session, we have to create it into the database
                        session = dbHelper.createNewSession(selfworkoutUserId,swpSTList.get(holder.getAdapterPosition()).getId(),new Date().getTime(),1);
                    }

                    List<SelfWorkoutSessionLog> exercisesLog = dbHelper.getSessionLogs(session.getId());

                    //Let's send the data into the next fragment
                    Bundle dataToPass = new Bundle();
                    dataToPass.putSerializable("exercisesLog",(Serializable) exercisesLog);
                    dataToPass.putInt("sessionId",session.getId());

                    SelfworkoutSessionExerciseFragment selfworkoutSessionExerciseFragment = new SelfworkoutSessionExerciseFragment();
                    selfworkoutSessionExerciseFragment.setArguments(dataToPass);

                    FragmentManager fm = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();

                    // Replace the current fragment with the new one
                    fragmentTransaction.replace(R.id.barFrame, selfworkoutSessionExerciseFragment);

                    // Add the transaction to the back stack
                    fragmentTransaction.addToBackStack(null);

                    // Commit the transaction
                    fragmentTransaction.commit();

                }
            });

        }

        @Override
        public int getItemCount() {
            return swpSTList.size();
        }
    }
}