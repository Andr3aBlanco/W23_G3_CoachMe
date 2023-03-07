package com.bawp.coachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_in_Activity extends AppCompatActivity {

    Button logInBtn;
    Button jayButton;
    FirebaseAuth mAuth;
    ImageView googleLoginBtn;
//importing data
    EditText usernametxt;
    EditText passwordtxt;
    TextView goToRegister;
    ProgressBar porgBar;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent (getApplicationContext(), Main_page_Activity.class);
            startActivity (intent);
            finish();
        }}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
        mAuth=FirebaseAuth.getInstance();
        googleLoginBtn=findViewById(R.id.imageGoogleAuth);
        usernametxt=findViewById(R.id.txtusername);
        passwordtxt=findViewById(R.id.txtpassword);
        goToRegister=findViewById(R.id.txtGoToReg);
        porgBar=findViewById(R.id.pBar);
        logInBtn=findViewById(R.id.btnregisterConf);
//        //creating login button on click listner
//

        goToRegister.setOnClickListener((View view)->{
            Intent regIntent=new Intent(Login_in_Activity.this,Register_Activity.class);
            startActivity(regIntent);
        });
        //hello word
        logInBtn.setOnClickListener((View View)-> {
            porgBar.setVisibility(View.GONE);
            String email, password,name;
            email = String.valueOf(usernametxt.getText());
            password = String.valueOf(passwordtxt.getText());


            if (TextUtils.isEmpty (email)) {

                Toast.makeText(Login_in_Activity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();


                return;
            }if (TextUtils.isEmpty (password)) {
                Toast.makeText(Login_in_Activity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

//sending request to the firebase to check the  existing user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(Login_in_Activity.this, "Loged in",
                                        Toast.LENGTH_SHORT).show();
                                 //starting new Activity when user id password is correct
                                Intent intent = new Intent (getApplicationContext(), Main_page_Activity.class);
                                startActivity (intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(Login_in_Activity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        });
    }
}