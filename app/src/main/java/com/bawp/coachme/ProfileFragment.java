package com.bawp.coachme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.presentation.userAuthantication.EditUserProfileFragment;
import com.bawp.coachme.presentation.userAuthantication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private TextView textViewLastName, textViewfirstName, textViewEmail,  textViewAddress,textViewMobile,textViewfullname;
    private ProgressBar progressBar;

    private ImageView imageView;
    Button signOutBtn;
    Button editProfileBtn;
    FirebaseAuth auth;
    FirebaseUser user;
    String current_User;
    DatabaseReference databaseRef;

    private String  firstName;
    private String   lastName ;
    private String   email ;
    private String    address;
    private String    mobile ;
//    private void replaceFragment(Fragment fragment){
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.barFrame, fragment);
//        fragmentTransaction.commit();
//
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        textViewfirstName =view.findViewById(R.id.textView_show_first_name);
        textViewLastName =view.findViewById(R.id.textView_show_last_name);
        textViewfullname=view.findViewById(R.id.textView_show_name);
        textViewEmail = view.findViewById(R.id.textView_show_email);
        textViewAddress = view.findViewById(R.id.textView_show_address);
        textViewMobile = view.findViewById(R.id.textView_show_phone_number);
        editProfileBtn=view.findViewById(R.id.btnEditProfileSave);
        signOutBtn=view.findViewById(R.id.btnSignOut);

// Retrieve user data from Firebase Realtime Database and update TextViews
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        current_User = user.getUid();

        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_User);



        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    firstName = snapshot.child("firstName").getValue(String.class);
                    lastName = snapshot.child("lastName").getValue(String.class);
                    email = snapshot.child("email").getValue(String.class);
                    address = snapshot.child("address").getValue(String.class);
                    mobile = snapshot.child("phoneNumber").getValue(String.class);
                // Update TextViews with user data
                textViewfirstName.setText(firstName);
                textViewLastName.setText(lastName);
                textViewEmail.setText(email);
                textViewMobile.setText(mobile);
                textViewAddress.setText(address);
                textViewfullname.setText(firstName+" "+lastName);
            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editProfileBtn.setOnClickListener((View v)->{

            Bundle bundle=new Bundle();
            bundle.putString("FNAME",firstName);
            bundle.putString("LNAME",lastName);
            bundle.putString("EMAIL",email);
            bundle.putString("ADDRESS",address);
            bundle.putString("MOBILE",mobile);

            EditUserProfileFragment editUserProfileFragment = new EditUserProfileFragment();
            editUserProfileFragment.setArguments(bundle);

            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            // Replace the current fragment with the new one
            fragmentTransaction.replace(R.id.barFrame, editUserProfileFragment);

            // Add the transaction to the back stack
            fragmentTransaction.addToBackStack(null);

            // Commit the transaction
            fragmentTransaction.commit();


        });


        signOutBtn.setOnClickListener((View v)->{
             FirebaseAuth.getInstance().signOut();
             Intent intent = new Intent(getActivity(), LoginActivity.class);
             startActivity (intent);
            getActivity().finish();


        });

return view;


    }







}