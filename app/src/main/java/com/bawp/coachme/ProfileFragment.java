package com.bawp.coachme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private TextView textViewLastName, textViewfirstName, textViewEmail,  textViewAddress,textViewMobile;
    private ProgressBar progressBar;
    private String firstName="";
    private String lastName="jar";
    private String email="";
    private String address="";
    private String mobile="";
    private ImageView imageView;
    Button signOutBtn;
    Button editProfileBtn;
    FirebaseAuth auth;
    FirebaseUser user;
    String current_User;
    DatabaseReference databaseRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        textViewfirstName =view.findViewById(R.id.textView_show_first_name);
        textViewLastName =view.findViewById(R.id.textView_show_last_name);
        textViewEmail = view.findViewById(R.id.textView_show_email);
        textViewAddress = view.findViewById(R.id.textView_show_address);
        editProfileBtn=view.findViewById(R.id.btnEditProfile);
        signOutBtn=view.findViewById(R.id.btnSignOut);

// Retrieve user data from Firebase Realtime Database and update TextViews
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        current_User = user.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users");


//        progressBar.setVisibility(View.VISIBLE);
            //Extracting data from Database user table and setting
        databaseRef.child(current_User).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
//                                Toast.makeText( context: ReadData.this, text: "Successfully Read", Toast.LENGTH_SHORT).show();
                                DataSnapshot dataSnapshot = task.getResult();
                               firstName = String.valueOf(dataSnapshot.child("firstName").getValue());
                            }else {
//                                Toast.makeText( context: ReadData.this, text: "User Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }}}});


                 textViewfirstName.setText(firstName);

                    textViewEmail.setText(email);
//                  textViewMobile.setText(mobile);
                    textViewAddress.setText(address);
        textViewLastName.setText(lastName);
return view;


    }







}