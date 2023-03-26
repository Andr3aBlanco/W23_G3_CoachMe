/**
 * Class: SelfworkoutSessionExRecyclerAdapter.java
 *
 * Recycler Adapter that handles the display of each EXERCISE based on
 * session already selected by the user
 *
 * Each element is going to be represented as a card element, and if the user
 * select one it will go to the next fragment related to the exercise's detail fragment.
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.presentation.selfworkout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutSessionLog;
import com.bawp.coachme.presentation.order.OrderHistoryRecyclerAdapter;
import com.bawp.coachme.utils.DBHelper;

import java.io.Serializable;
import java.util.List;

public class SelfworkoutSessionExRecyclerAdapter extends RecyclerView.Adapter<SelfworkoutSessionExRecyclerAdapter.SelfworkoutSessionExViewHolder> {

    List<SelfWorkoutSessionLog> exercisesLog;
    Context context;
    DBHelper dbHelper;
    SetOnItemClickListener listener;

    public SelfworkoutSessionExRecyclerAdapter(List<SelfWorkoutSessionLog> exercisesLog, Context context, SetOnItemClickListener listener){
        this.context = context;
        this.exercisesLog = exercisesLog;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelfworkoutSessionExViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.selfworkout_ses_exercise_cardview_layout, parent, false);
        SelfworkoutSessionExRecyclerAdapter.SelfworkoutSessionExViewHolder holder = new SelfworkoutSessionExRecyclerAdapter.SelfworkoutSessionExViewHolder(view);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(holder.getAdapterPosition());

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelfworkoutSessionExViewHolder holder, int position) {
        holder.mTxtViewExerciseName.setText(exercisesLog.get(position).getSelfWorkoutExercise().getExerciseName().toUpperCase());
        if(exercisesLog.get(position).getSessionExerciseStatus() == 3){
            holder.mCardView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.disabled_color));
        }
    }

    @Override
    public int getItemCount() {
        return exercisesLog.size();
    }

    public class SelfworkoutSessionExViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mTxtViewExerciseName;

        public SelfworkoutSessionExViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.card_container_sw_exercise);
            mTxtViewExerciseName = itemView.findViewById(R.id.txtViewExerciseName);
        }
    }

    public interface SetOnItemClickListener{
        public void onClickItem(int i);
    }

}
