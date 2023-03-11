package com.bawp.coachme.presentation.trainermap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.utils.DBHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TrainerListFragment extends Fragment {


    // This is for displaying trainers and test logic for recyclerView

    DBHelper dbHelper;  // Call trainers move this to trainersearch
    // Remove up here

     private RecyclerView recyclerView;
     private TrainerListFragment.TrainerViewAdapter trainerListAdapter;
     private static TrainerSearchFragment trainerSearchFragment;

     // List of trainers
    private static List<Trainer> trainerList = new ArrayList<>(); // This is unsorted

    public TrainerListFragment() {
        // Required empty public constructor
    }

    public static TrainerListFragment newInstance(String param1, String param2) {
        TrainerListFragment fragment = new TrainerListFragment();
        Bundle args = new Bundle();
// GET THE TRAINERS FROM THE TRIANER SEARCH
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            trainersMapFiltered = (HashMap<String, Trainer>) getArguments().getSerializable("FILTERED_TRAINERS");
        }
        // Create an instance of your database helper class
        dbHelper = new DBHelper(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.trainer_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.trainer_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        trainerList = dbHelper.getTrainers();
        Log.d("Andrea", "Number of trainers: " + trainerList.size());
        // Here call the adapter
            Log.d("Andrea", "Inside the Trainer List"); // Loading ok
        //set the adapter
        trainerListAdapter = new TrainerListFragment.TrainerViewAdapter(trainerList, trainerSearchFragment, 1 );
        recyclerView.setAdapter(trainerListAdapter);
        return view;
    }

    //Class for the ViewHolder
    private class TrainerViewHolder extends RecyclerView.ViewHolder{

        // Get the things from the layout
       private TextView tvName;
        private TextView tvPrice;
        private TextView tvBio;
        private TextView tvRating;

        private  ImageButton seeMore;
        private  LinearLayout calendarLayout;
        private  ListView listViewHours;
        private  ImageButton closeBtn;

        // constructor OK
        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public TrainerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.trainer_details_fragment, container, false));

            // Find the things from the layout TrainerDetails with logic
            tvName = itemView.findViewById(R.id.tvTrainerName);
            tvPrice = itemView.findViewById(R.id.tvPriceHour);
            tvBio = itemView.findViewById(R.id.tvTrainerBio);
            tvRating  = itemView.findViewById(R.id.tvTrainerRating);

            seeMore = itemView.findViewById(R.id.btnTrainerSeeAppTable);
            calendarLayout = itemView.findViewById(R.id.calLayout); //ok
            listViewHours = itemView.findViewById(R.id.lvTimes);

        }
    }

    public class TrainerViewAdapter extends RecyclerView.Adapter<TrainerListFragment.TrainerViewHolder>{

        //Adapter with new model
        private List<Trainer> unsortedTrainers = new ArrayList<>();
        //Parent fragment
        private TrainerSearchFragment parentFragment;
        private int sortingOpt = 0;
        private DBHelper dbHelper;

        /*
        * Sorting options comes from the filter static parent
        * 1 -> Distance
        * 2 -> Rating
        * 3 -> Price
        * */

        // Constructor OK
        public TrainerViewAdapter(List<Trainer> unsortedTrainers, TrainerSearchFragment parentFragment,  int sortingOpt) {
            this.unsortedTrainers = unsortedTrainers;
            this.sortingOpt = sortingOpt;
            this.parentFragment = parentFragment; //This is calling the parent - container for both map and tlist
            this.dbHelper = new DBHelper(getContext());
        }

        @NonNull
        @Override
        public TrainerListFragment.TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new TrainerListFragment.TrainerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TrainerListFragment.TrainerViewHolder holder, int position) {
           // Fomrater for the price
            Locale locate = new Locale("en", "CA");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locate);
           double rating = unsortedTrainers.get(position).getRating();
            // Set the things in the layout with the holder

            holder.tvName.setText(unsortedTrainers.get(position).getFirstName() + " " + unsortedTrainers.get(position).getLastName()); //Only one for testing
            holder.tvPrice.setText(formatter.format(unsortedTrainers.get(position).getFlatPrice()));
            holder.tvRating.setText(Double.toString(rating));

            //Here goes all the logic
            holder.seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Andrea", "SeMore clicked from the RecyclerAdapter");
                    holder.calendarLayout.setVisibility(View.VISIBLE);
                }
            });

        }

        @Override
        public int getItemCount() {
            return unsortedTrainers.size();
        }
    }
}