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
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.utils.DBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeAppRecyclerFragment extends Fragment {

    private static List<Appointment> appointmentList;
    private HomeAppRecyclerFragment.RecyclerViewAdapter homeListAdapter;
    private RecyclerView recyclerView;

    private static HomeFragment homeFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeListAdapter = new HomeAppRecyclerFragment.RecyclerViewAdapter(appointmentList,homeFragment);
        recyclerView.setAdapter(homeListAdapter);
        return view;
    }

    public static Fragment newInstance(List<Appointment> appointments,  HomeFragment parentFragment) {
        appointmentList = appointments;
        homeFragment = parentFragment;
        return new HomeAppRecyclerFragment();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mTxtViewProductTitle;
        TextView mTxtViewProductDetail;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.home_app_cardview_layout, container, false));
            mCardView = itemView.findViewById(R.id.home_item_card_container);
            mTxtViewProductTitle = itemView.findViewById(R.id.txtViewProductTitle);
            mTxtViewProductDetail = itemView.findViewById(R.id.txtViewProductDetail);
        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<HomeAppRecyclerFragment.RecyclerViewHolder>{

        private List<Appointment> appointmentList;
        private HomeFragment parentFragment;
        private DBHelper dbHelper;

        public RecyclerViewAdapter(List<Appointment> appointmentList, HomeFragment parentFragment) {
            this.appointmentList = appointmentList;
            this.parentFragment = parentFragment;
            this.dbHelper = new DBHelper(getContext());
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new HomeAppRecyclerFragment.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

                //appointments
                String trainerId = appointmentList.get(position).getTrainerId();
                Trainer trainer = dbHelper.getTrainerById(trainerId);

                holder.mTxtViewProductTitle.setText("Training with "+trainer.getFirstName());

                Date bookedDate = new Date(appointmentList.get(position).getBookedDate());
                DateFormat format = new SimpleDateFormat("EEE, dd/MM/yy HH:mm");
                String formattedBookedDate = format.format(bookedDate);
                String serviceType = appointmentList.get(position).getServiceType();

                String description = serviceType + " Session\n"+formattedBookedDate;
                holder.mTxtViewProductDetail.setText(description);



        }

        @Override
        public int getItemCount() {

            return appointmentList.size();

        }
    }

}
