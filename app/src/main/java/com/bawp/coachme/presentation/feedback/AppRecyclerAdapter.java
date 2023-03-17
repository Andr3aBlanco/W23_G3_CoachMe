package com.bawp.coachme.presentation.feedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.AppointmentHolder> {

    List<Appointment> pastAppointments = new ArrayList<>();
    int clickedIndex = -1;


    // Aux variables to get from the other tables
    String TrainerFullName = "";
    DBHelper dbHelper;
    Trainer currentTrainer;
    OnItemClickListener onItemClickListener;


    public AppRecyclerAdapter(List<Appointment> pastAppointments, OnItemClickListener onItemClickListener) {
        this.pastAppointments = pastAppointments;
        this.onItemClickListener = onItemClickListener;
    }

    public AppRecyclerAdapter(List<Appointment> pastAppointments) {
        this.pastAppointments = pastAppointments;
    }

    @NonNull
    @Override
    public AppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_history_item, parent, false);

        AppointmentHolder holder =new AppointmentHolder(itemView);

        //
        dbHelper = new DBHelper(parent.getContext()); // Check this and change if necessary


        // click listener to update the selected index
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(holder.getAdapterPosition());
                clickedIndex = holder.getAdapterPosition();

                // get here the trainer by id
              currentTrainer = dbHelper.getTrainerById(pastAppointments.get(clickedIndex).getTrainerId());
                notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentHolder holder, int position) {

        holder.trainerName.setText(currentTrainer.getFirstName() + " " + currentTrainer.getLastName());

    }

    @Override
    public int getItemCount() {
        return pastAppointments.size();
    }

    public class AppointmentHolder extends RecyclerView.ViewHolder{

    // From the layout
    TextView trainerName;
    TextView appDate;
    RatingBar starRating;

    public AppointmentHolder(@NonNull View itemView) {
        super(itemView);

        trainerName = itemView.findViewById(R.id.txtTrainerNameAppRV);
        appDate = itemView.findViewById(R.id.txtAppDateRV);
        starRating = itemView.findViewById(R.id.appRatingstar);

    }
}

    public interface OnItemClickListener {
        public void onItemClick(int i);

    }
}
