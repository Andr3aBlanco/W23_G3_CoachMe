package com.bawp.coachme.presentation.trainermap;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;

public class TrainerViewHolder extends RecyclerView.ViewHolder {

    //Get the things from the layout
    //Things from the layout
    TextView tvName;
    TextView tvPrice;
    TextView tvBio;
    TextView tvRating;

    ImageButton seeMore;
    LinearLayout calendarLayout;
    ListView listViewHours;
    ImageButton closeBtn;


    public TrainerViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvTrainerNameR);
         tvPrice = itemView.findViewById(R.id.tvPriceHourR);
         tvBio = itemView.findViewById(R.id.tvTrainerBioR);
         tvRating  = itemView.findViewById(R.id.tvTrainerRatingR);

         seeMore = itemView.findViewById(R.id.btnTrainerSeeAppTableR);
         calendarLayout = itemView.findViewById(R.id.calLayoutR); //ok
         listViewHours = itemView.findViewById(R.id.lvTimesR);


    }

    public void bind(Trainer trainer){

        tvName.setText(trainer.getFirstName() + " " + trainer.getLastName());

        // ClickListener to launch the TrainerDetailsFragment from here
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fm = Fra; ///cjeck this again
//                FragmentTransaction ft = fm.beginTransaction();
//                ft.replace(R.id.rvTrainerList, TrainerDetailsFragment.newInstance(trainer));
//                ft.addToBackStack(null);
//                ft.commit();
            }
        });
    }
}
