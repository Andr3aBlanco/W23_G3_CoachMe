package com.bawp.coachme.presentation.userAuthantication;
/**
 * Clsss: EditUserProfileFragment.java
 * class that will gets the data from user account and will let them to modify their personal data
 *
 * @author Jaydip mulani
 * @version 1.0
 */

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bawp.coachme.presentation.user.ProfileFragment;
import com.bawp.coachme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditUserProfileFragment extends Fragment {
    private EditText editTextLastName, editTextfirstName,  editTextAddress,editTextMobile;
    private TextView textviewEmail;
    private ProgressBar progressBar;

    private ImageView imageView;
    Button btnSave;
    Button btnCancle;
    FirebaseAuth auth;
    FirebaseUser user;
    String current_User;
    DatabaseReference databaseRef;

    private String  et_firstName;
    private String   et_lastName ;
    private String   et_email ;
    private String    et_address;
    private String    et_mobile ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


       View view= inflater.inflate(R.layout.fragment_edit_user_profile, container, false);

        Bundle bundle = getArguments();

        et_firstName= bundle.getString("FNAME");
        et_lastName = bundle.getString("LNAME");
        et_email = bundle.getString("EMAIL");
        et_address= bundle.getString("ADDRESS");
        et_mobile = bundle.getString("MOBILE");

        editTextfirstName =view.findViewById(R.id.editText_Edit_first_name);
        editTextLastName =view.findViewById(R.id.editText_Edit_last_name);
        textviewEmail = view.findViewById(R.id.editText_Edit_email);
        editTextAddress = view.findViewById(R.id.editText_Edit_address);
        editTextMobile = view.findViewById(R.id.editText_Edit_phone_number);
        btnSave=view.findViewById(R.id.btnEditProfileSave);
        btnCancle=view.findViewById(R.id.btnEditProfileCancle);

        //setting up edit textboxes
        editTextfirstName.setText(et_firstName);
        editTextLastName .setText(et_lastName);
        textviewEmail .setText(et_email);
        editTextAddress .setText(et_address);
        editTextMobile .setText(et_mobile);


        btnSave.setOnClickListener((View v)->{

            String s_fname,s_lname,s_email,s_mobile,s_address;
            s_fname=editTextfirstName.getText().toString();
            s_lname= editTextLastName.getText().toString();
            s_email=textviewEmail.getText().toString();
            s_address= editTextAddress.getText().toString();
            s_mobile=editTextMobile.getText().toString();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            current_User = user.getUid();

            databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_User);
            databaseRef.child("firstName").setValue(s_fname);
            databaseRef.child("lastName").setValue(s_lname);
            databaseRef.child("email").setValue(s_email);
            databaseRef.child("address").setValue(s_address);
            databaseRef.child("phoneNumber").setValue(s_mobile);
            //return back to profile fragment


            ProfileFragment profileFragment = new ProfileFragment();

            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();

            // Replace the current fragment with the new one
            fragmentTransaction.replace(R.id.barFrame, profileFragment);

            // Add the transaction to the back stack
            fragmentTransaction.addToBackStack(null);

            // Commit the transaction
            fragmentTransaction.commit();
        });
btnCancle.setOnClickListener((View v)->{
    ProfileFragment profileFragment = new ProfileFragment();

    FragmentManager fm = getParentFragmentManager();
    FragmentTransaction fragmentTransaction = fm.beginTransaction();

    // Replace the current fragment with the new one
    fragmentTransaction.replace(R.id.barFrame, profileFragment);

    // Add the transaction to the back stack
    fragmentTransaction.addToBackStack(null);

    // Commit the transaction
    fragmentTransaction.commit();
});
       return view;
    }
}