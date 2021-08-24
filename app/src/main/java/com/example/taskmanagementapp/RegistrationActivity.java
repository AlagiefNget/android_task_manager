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

public class RegistrationActivity extends AppCompatActivity {
    private EditText registerEmail;
    private EditText registerPsswd;
    private Button registerBtn;
    private TextView loginTxt;

    private FirebaseAuth myAuth;
    private ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registerEmail = findViewById(R.id.registerEmail);
        registerPsswd = findViewById(R.id.registerPsswd);
        registerBtn = findViewById(R.id.registerBtn);
        loginTxt = findViewById(R.id.signin_txt);

        myAuth = FirebaseAuth.getInstance();
        myDialog = new ProgressDialog(this);

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString().trim();
                String pswd = registerPsswd.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    registerEmail.setError("Required field...");
                    return;
                }
                if(TextUtils.isEmpty(pswd)){
                    registerPsswd.setError("Required field...");
                    return;
                }

                myDialog.setMessage("Processing....");
                myDialog.show();
                myAuth.createUserWithEmailAndPassword(email, pswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(getApplicationContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                        myDialog.dismiss();
                    }
                });
            }
        });
    }
}