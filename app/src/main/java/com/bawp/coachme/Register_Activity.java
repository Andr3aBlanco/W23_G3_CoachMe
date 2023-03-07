package com.bawp.coachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register_Activity extends AppCompatActivity {
    EditText userNameRegtxt;
    EditText passwordRegTxt;
    EditText personName;
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
        personName=findViewById(R.id.txtRegPName);
        progressBar=findViewById(R.id.progressBar);
        btnRegister=findViewById(R.id.btnregisterConf);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email, password,name;
                email = String.valueOf(userNameRegtxt.getText());
                password = String.valueOf(passwordRegTxt.getText());
                name = String.valueOf(personName.getText());
                progressBar.setVisibility(View.GONE);
                if (TextUtils.isEmpty (email)) {

                    Toast.makeText(Register_Activity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty (password)) {
                    Toast.makeText(Register_Activity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty (name)) {
                    Toast.makeText(Register_Activity.this, "Please Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Register_Activity.this, "User created", Toast.LENGTH_SHORT).show();
                                    Intent regIntent=new Intent(Register_Activity.this,Login_in_Activity.class);
                                    startActivity(regIntent);
                                    return;
                                } else {
                                    // If sign in fails, display a message to the user.


                                    Toast.makeText(Register_Activity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });

            }
        });

    }
}