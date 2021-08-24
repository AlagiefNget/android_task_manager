package com.example.taskmanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private TextView signUpTxt;
    private EditText loginEmail;
    private EditText loginPswd;
    private Button loginBtn;

    private FirebaseAuth myAuth;
    private ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpTxt = findViewById(R.id.signup_txt);
        loginEmail = findViewById(R.id.loginEmail);
        loginPswd = findViewById(R.id.loginPsswd);
        loginBtn = findViewById(R.id.loginBtn);

        myAuth = FirebaseAuth.getInstance();
        myDialog = new ProgressDialog(this);

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String pswd = loginPswd.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    loginEmail.setError("Please specify your email");
                    return;
                }
                if(TextUtils.isEmpty(pswd)){
                    loginPswd.setError("Please specify your password");
                    return;
                }

                myDialog.setMessage("Processing....");
                myDialog.show();

                myAuth.signInWithEmailAndPassword(email, pswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }else{
                            Toast.makeText(getApplicationContext(), "Log in failed", Toast.LENGTH_SHORT).show();
                        }
                        myDialog.dismiss();
                    }
                });

            }
        });

    }
}