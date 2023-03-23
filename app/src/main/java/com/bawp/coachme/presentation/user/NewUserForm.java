package com.bawp.coachme.presentation.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bawp.coachme.LoadingDBSplashActivity;
import com.bawp.coachme.MainActivity;
import com.bawp.coachme.R;
import com.bawp.coachme.model.Role;
import com.bawp.coachme.model.User;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import adapter.PlaceAutoSuggestionAdapter;

public class NewUserForm extends AppCompatActivity {
    EditText firstNameTxt;
    EditText lastNameTxt;
    AutoCompleteTextView addressTxt;
    EditText phoneNumberTxt;

    String emailTxt;

    Button confirmDataBtn;
    FirebaseAuth auth;

    FirebaseUser user;
    String current_User;
    String fname;
    String lname;
    String phonenum;

    Button btn;
    DatabaseReference databaseRef;

    public void onStart() {
        super.onStart();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_User);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // //user exists
                    //
                    // //here i have to change to main activity when i create one
                    // Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    // startActivity(intent);
                    // Toast.makeText(NewUserForm.this, "Welcome Back ", Toast.LENGTH_SHORT).show();
                    // finish();
                }
            };

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuserform);
        // getting current user from firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        // saving current user ID
        current_User = user.getUid();

        // creating database reference
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_User);
        confirmDataBtn = findViewById(R.id.btnConfirm);
        firstNameTxt = findViewById(R.id.txtFirstName);
        lastNameTxt = findViewById(R.id.txtLastName);
        addressTxt = findViewById(R.id.txtAddress);
        phoneNumberTxt = findViewById(R.id.txtPhoneNumber);

        // btn=findViewById(R.id.btngotosn);

        addressTxt.setAdapter(new PlaceAutoSuggestionAdapter(NewUserForm.this, android.R.layout.simple_list_item_1));
        // btn.setOnClickListener((View view)->{
        //
        //// Intent intent = new Intent (NewUserForm.this, LoginActivity.class);
        //// startActivity (intent);
        //// finish();
        // //SignOut from FireBase **this function i have to add where we select logout
        // account
        // FirebaseAuth.getInstance().signOut();
        // Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        // startActivity (intent);
        // finish();
        // });
        // if user is not logged in we will open login activity
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();

        } else {
            emailTxt = user.getEmail();
            fname = user.getDisplayName();
            phonenum = user.getPhoneNumber();
            firstNameTxt.setText(fname);
            phoneNumberTxt.setText(phonenum);

        }
        // whem exsiting user gets log in it will redirect them to main activity

        confirmDataBtn.setOnClickListener((View v) -> {
            String Fname, Lname, address, phoneNumber;

            // creating role as a customer ID=1 and role customer
            Role newRole = new Role(1, "Customer");
            Fname = String.valueOf(firstNameTxt.getText());
            Lname = String.valueOf(lastNameTxt.getText());
            phoneNumber = String.valueOf(phoneNumberTxt.getText());
            address = String.valueOf(addressTxt.getText());
            User newUser = new User(Fname, Lname, emailTxt, address, phoneNumber, newRole);

            //Because the user is a new user, let's add it into firebase
            databaseRef.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserSingleton.getInstance().setUserId(current_User);
                        Toast.makeText(NewUserForm.this, "Welcome to CoachMe ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoadingDBSplashActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(NewUserForm.this, "unable to add user details ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        // SignOut from FireBase **this function i have to add where we select logout
        // account
        // FirebaseAuth.getInstance().signOut();
        // Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        // startActivity (intent);
        // finish();

    }
}