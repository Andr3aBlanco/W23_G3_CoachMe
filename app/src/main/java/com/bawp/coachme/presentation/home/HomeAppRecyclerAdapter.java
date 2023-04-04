package com.bawp.coachme.presentation.home;

/**
 * Class: HomeAppRecyclerAdapter.java
 *
 * Recycler View Adapter that will handle all the generation of each current
 * appointment a user has purchased and didn't occur yet.
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.utils.DBHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeAppRecyclerAdapter extends RecyclerView.Adapter<HomeAppRecyclerAdapter.HomeAppViewHolder> {

    List<Appointment> appointmentList;
    Context context;
    SetOnItemClickListener listener;
    DBHelper dbHelper;

    public HomeAppRecyclerAdapter(List<Appointment> appointmentList, Context context, SetOnItemClickListener listener){
        this.appointmentList = appointmentList;
        this.context = context;
        this.listener = listener;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public HomeAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_app_cardview_layout, parent, false);
        HomeAppRecyclerAdapter.HomeAppViewHolder holder = new HomeAppRecyclerAdapter.HomeAppViewHolder(view);

        //Click listener for see appointment details button
        holder.btnAppDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.rlMapApppDetails.getVisibility() == View.GONE){
                    holder.rlMapApppDetails.setVisibility(View.VISIBLE);
                }else{
                    holder.rlMapApppDetails.setVisibility(View.GONE);
                }



                // for the map
                holder.mvLocation.onCreate(null);
               holder.mvLocation.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


                        // current position
                        int currentposition = holder.getBindingAdapterPosition();
                        System.out.println("INSIDE THE HOME ADAPTER position " + currentposition);
                        Trainer trainer = dbHelper.getTrainerById(appointmentList.get(currentposition).getTrainerId());
                        LatLng trainerLocation = new LatLng(trainer.getLatitudeCoord(), trainer.getLongitudeCoord());
                        googleMap.addMarker(new MarkerOptions().position(trainerLocation).title(trainer.getFirstName()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(trainerLocation));
                    }
                });
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAppViewHolder holder, int position) {
        //appointments
        String trainerId = appointmentList.get(position).getTrainerId();
        Trainer trainer = dbHelper.getTrainerById(trainerId);

        //Downloading the logo from firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String imageURL = trainer.getTrainerProfileImage();
        StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
        Glide.with(context)
                .load(imageRef)
                .into(holder.mImgViewTrainerPhoto);

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

    public class HomeAppViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mTxtViewProductTitle;
        TextView mTxtViewProductDetail;
        ImageView mImgViewTrainerPhoto;

        ImageButton btnAppDetails;

        // Additional details
        MapView mvLocation;
        RelativeLayout rlMapApppDetails;


        public HomeAppViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.home_item_card_container);
            mTxtViewProductTitle = itemView.findViewById(R.id.txtViewProductTitle);
            mTxtViewProductDetail = itemView.findViewById(R.id.txtViewProductDetail);
            mImgViewTrainerPhoto = itemView.findViewById(R.id.imgViewTrainerPhoto);
            btnAppDetails = itemView.findViewById(R.id.btnCheckAppointmentHome);
            mvLocation = itemView.findViewById(R.id.mapViewAppLocation);
            rlMapApppDetails = itemView.findViewById(R.id.rlAappAddDetails);
        }
    }

    public interface SetOnItemClickListener{
        public void setOnItemClick(int i);
    }




}
