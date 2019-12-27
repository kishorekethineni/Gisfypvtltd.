package com.example.gisyproject.LoginFB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gisyproject.MainActivity;
import com.example.gisyproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email,password;
    Button loginBtn;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=(EditText)findViewById(R.id.Regemail);
        password=(EditText)findViewById(R.id.Regpassword);
        loginBtn=(Button) findViewById(R.id.Loginbtn);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(LoginActivity.this);
        TextView registerBTN = (TextView) findViewById(R.id.registerBtn);

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this, Register.class);
                startActivity(i);
                finish();
            }
        });
        //maine yahan pr login button pr click listener lagaya hai  ,yad rkhna be (to my self :) )
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {




                String uemail=email.getText().toString();
                String upassword=password.getText().toString();

                if(TextUtils.isEmpty(uemail)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(upassword)){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setTitle("Logging in");
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(uemail, upassword)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(LoginActivity.this, "Aman bhiaya is lazy", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(LoginActivity.this, "Login Successfull!!", Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(LoginActivity.this,MainActivity.class);
                                    progressDialog.dismiss();
                                    startActivity(i);
                                    finish();


                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }
        });

        TextView ForgotPassword=(TextView)findViewById(R.id.forgotpassword);
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog(LoginActivity.this);
            }
        });
    }
    public void showAddItemDialog(Context c) {
        progressDialog = new ProgressDialog(LoginActivity.this);
        final EditText taskEditText = new EditText(c);

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Reset Email")
                .setMessage("Enter you email ")
                .setView(taskEditText)
                .setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setTitle("Reset Password");
                        progressDialog.show();
                        String email = String.valueOf(taskEditText.getText());
                        mAuth.sendPasswordResetEmail(email)

                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

}
