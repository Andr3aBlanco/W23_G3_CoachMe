package com.bawp.coachme.presentation.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText emailEditText;
    Button resetPasswordBtn;
    ProgressBar progressBar;
    TextView backToLogIn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        emailEditText = findViewById(R.id.txtresetuserEmail);
        resetPasswordBtn = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.pBar);
        backToLogIn = findViewById(R.id.txtBackToLogin);

        mAuth = FirebaseAuth.getInstance();

        // creating on click listener for backToLogIn
        backToLogIn.setOnClickListener((View view) -> {
            Intent regIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(regIntent);
        });

        resetPasswordBtn.setOnClickListener((View view) -> {
            String email, password, name;
            email = String.valueOf(emailEditText.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ResetPasswordActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ResetPasswordActivity.this, "Please provide a valid Email", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener((@NonNull Task<Void> task) -> {

                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ResetPasswordActivity.this, "Please Check your Email", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ResetPasswordActivity.this, "Try again!!", Toast.LENGTH_SHORT).show();
                }

            });
        });
    }
}