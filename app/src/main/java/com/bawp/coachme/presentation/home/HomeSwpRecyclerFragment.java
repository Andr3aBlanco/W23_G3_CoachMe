package com.bawp.coachme.presentation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.utils.DBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeSwpRecyclerFragment extends Fragment {

    private static List<SelfWorkoutPlanByUser> selfWorkoutPlanByUsers;
    private HomeSwpRecyclerFragment.RecyclerViewAdapter homeListAdapter;
    private RecyclerView recyclerView;
    private static int listType;

    private static HomeFragment homeFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeListAdapter = new HomeSwpRecyclerFragment.RecyclerViewAdapter(selfWorkoutPlanByUsers,homeFragment);
        recyclerView.setAdapter(homeListAdapter);
        return view;
    }

    public static Fragment newInstance( List<SelfWorkoutPlanByUser>swpList, HomeFragment parentFragment) {
        selfWorkoutPlanByUsers = swpList;
        homeFragment = parentFragment;
        return new HomeSwpRecyclerFragment();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mTxtViewProductTitle;
        TextView mTxtViewProductDetail;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.home_swp_cardview_layout, container, false));
            mCardView = itemView.findViewById(R.id.home_swp_card_container);
            mTxtViewProductTitle = itemView.findViewById(R.id.txtViewProductTitle);
            mTxtViewProductDetail = itemView.findViewById(R.id.txtViewProductDetail);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<HomeSwpRecyclerFragment.RecyclerViewHolder>{

        private List<SelfWorkoutPlanByUser> selfWorkoutPlanByUsersList;
        private HomeFragment parentFragment;
        private DBHelper dbHelper;

        public RecyclerViewAdapter(List<SelfWorkoutPlanByUser> selfWorkoutPlanByUsersList, HomeFragment parentFragment) {
            this.selfWorkoutPlanByUsersList = selfWorkoutPlanByUsersList;
            this.parentFragment = parentFragment;
            this.dbHelper = new DBHelper(getContext());
        }

        @NonNull
        @Override
        public HomeSwpRecyclerFragment.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new HomeSwpRecyclerFragment.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeSwpRecyclerFragment.RecyclerViewHolder holder, int position) {


                String title = selfWorkoutPlanByUsersList.get(position).getSelfworkoutplan().getTitle();
                String mainGoal = selfWorkoutPlanByUsersList.get(position).getSelfworkoutplan().getMainGoals();
                holder.mTxtViewProductTitle.setText(title);
                holder.mTxtViewProductDetail.setText(mainGoal);


        }

        @Override
        public int getItemCount() {

                return selfWorkoutPlanByUsersList.size();

        }
    }

}
