package com.bawp.coachme.presentation.home;

/**
 * Class: HomeAppRecyclerAdapter.java
 *
 * Recycler View Adapter that will handle all the generation of each current
 * appointment a user has purchased and didn't occur yet.
 *
 * @author Luis Miguel Miranda / Andrea Blanco
 * @version 1.0
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.utils.DBHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    int currentCard = -1;

     MapView mapView;


    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
        notifyDataSetChanged();
    }

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


        holder.btnAppDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Click listener for see appointment details button
                if(currentCard != holder.getBindingAdapterPosition()){

                    currentCard = holder.getBindingAdapterPosition();
                    notifyDataSetChanged();

                }else{
                    currentCard = -1;
                    notifyDataSetChanged();

                }

//                Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
//                Debug.getMemoryInfo(memoryInfo);
//                Log.d("RAM", "Total memory used: " + memoryInfo.getTotalPss() + " KB");

            }
        });

        holder.cancelApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Click listener for see appointment details button
                int position = holder.getBindingAdapterPosition();

                dbHelper.updateAppointmentStatus(appointmentList.get(position).getId(),2);
                appointmentList.remove(position);
                System.out.println("APPOINTMENT CANCELLED. ");
                setAppointmentList(appointmentList);

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

        String appointmentTitle = "Training with "+trainer.getFirstName();

        if (appointmentList.get(position).getStatus() == 4){
            appointmentTitle += "\nIn Progress!";
        }

        holder.mTxtViewProductTitle.setText(appointmentTitle);

        Date bookedDate = new Date(appointmentList.get(position).getBookedDate());
        DateFormat format = new SimpleDateFormat("EEE, dd/MM/yy HH:mm");
        String formattedBookedDate = format.format(bookedDate);
        String serviceType = appointmentList.get(position).getServiceType();

        String description = serviceType + " Session\n"+formattedBookedDate;
        holder.mTxtViewProductDetail.setText(description);




        if(position == currentCard){

            holder.rlMapApppDetails.setVisibility(View.VISIBLE);

            double latitude = trainer.getLatitudeCoord();
            double longitude = trainer.getLongitudeCoord();



            holder.mapView.onCreate(null);
            holder.mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {

                    LatLng center = new LatLng(latitude, longitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(center, 10f);
                    googleMap.moveCamera(cameraUpdate);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(trainer.getFirstName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

                    googleMap.addMarker(markerOptions);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);

                    if(holder.mapView != null ){

                        holder.mapView.onLowMemory();
                        holder.mapView.onResume();
//                        holder.mapView.onDestroy();


                    }
                }
            });


        }else{

            holder.rlMapApppDetails.setVisibility(View.GONE);

        }



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

        RelativeLayout rlMapApppDetails;
        Button cancelApp;
        FrameLayout mapFrame;

        MapView mapView;

        public HomeAppViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.home_item_card_container);
            mTxtViewProductTitle = itemView.findViewById(R.id.txtViewProductTitle);
            mTxtViewProductDetail = itemView.findViewById(R.id.txtViewProductDetail);
            mImgViewTrainerPhoto = itemView.findViewById(R.id.imgViewTrainerPhoto);
            btnAppDetails = itemView.findViewById(R.id.btnCheckAppointmentHome);
            rlMapApppDetails = itemView.findViewById(R.id.rlAappAddDetails);
            cancelApp = itemView.findViewById(R.id.btnCancelApp);
//            mapFrame = itemView.findViewById(R.id.mapViewAppLocation);

            mapView = itemView.findViewById(R.id.mapViewAppLocation);

        }



    }

    public interface SetOnItemClickListener{
        public void setOnItemClick(int i);
    }





}
