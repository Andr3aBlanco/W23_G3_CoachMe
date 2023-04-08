package com.bawp.coachme.presentation.userAuthantication;
/**
 * Clsss: RegisterActivity.java
 * class which which let user to get register their account and save the data to the firebase
 *
 * @author Jaydip mulani
 * @version 1.0
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText userNameRegtxt;
    EditText passwordRegTxt;
TextView alreadyHaveAccount;
    Button btnRegister;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth=FirebaseAuth.getInstance();
        userNameRegtxt=findViewById(R.id.txtRegUsername);
        passwordRegTxt=findViewById(R.id.txtRegPassword);
        alreadyHaveAccount=findViewById(R.id.txtGoToLogin);
        progressBar=findViewById(R.id.progressBar);
        btnRegister=findViewById(R.id.btnregisterConf);
        String DeviceId= Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        alreadyHaveAccount.setOnClickListener((View view)->{

            Intent regIntent=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(regIntent);
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email, password;
                email = String.valueOf(userNameRegtxt.getText());
                password = String.valueOf(passwordRegTxt.getText());

                progressBar.setVisibility(View.GONE);
                if (TextUtils.isEmpty (email)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }if(!Patterns.EMAIL_ADDRESS.matcher (email).matches()) {

                    Toast.makeText(RegisterActivity.this, "Please provide a valid Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty (password)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

//creting user in user table of firebase and saving data to the single ton
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent regIntent=new Intent(getApplicationContext(), NewUserForm.class);
                                    startActivity(regIntent);
                                    return;
                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(RegisterActivity.this, "User Already Exist",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });

            }
        });

    }
}