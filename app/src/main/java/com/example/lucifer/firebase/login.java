package com.example.lucifer.firebase;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class login extends AppCompatActivity {
    private TextInputLayout email, password;
    private MaterialButton login, signup;
    private FirebaseAuth mauth;
    private ArrayList<TextInputLayout> list = new ArrayList<>();
    private boolean empty = false;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    public  String TAG="Firebase_app_project";
    ProgressDialog pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email_layout);
        password = findViewById(R.id.password_layout);
        login = findViewById(R.id.login_button);
        signup=findViewById(R.id.signup_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.getContext(),SignupActivity.class));
            }
        });
        list.add(email);
        list.add(password);
        pr=  new ProgressDialog(login.getContext());
        pr.setMessage("Siging in");
        pr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pr.setIndeterminate(true);
        pr.setCancelable(false);
        mauth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null&&firebaseAuth.getCurrentUser().isEmailVerified()) {
                    startActivity(new Intent(login.getContext(), HomePageActivity.class));
                }
            }
        };
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (TextInputLayout textInputLayout : list) {
                    if (textInputLayout.getEditText().getText().toString().equals("")) {
                        textInputLayout.setError("Field cannot be empty");
                        empty = true;
                    }
                }
                if (!empty) {
                    pr.show();
                    loggin();
                }
            }
        });

    }

    private void loggin() {
        String memail = email.getEditText().getText().toString().trim();
        String mpassword = password.getEditText().getText().toString().trim();
        mauth.signInWithEmailAndPassword(memail, mpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    pr.dismiss();
                    Log.d(TAG, "onComplete: ");
                   showDialog("Oops","\n Looks like your'e not signed up yet \n Or you have entered wrong credentials ");
                } else if (task.isSuccessful()){
                    pr.dismiss();
                    user=mauth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        Toast.makeText(getApplicationContext(), "LogedIn", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.getContext(), HomePageActivity.class));
                    }
                    else
                        showDialog("Oops","Looks like your email is not verified");
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(authStateListener);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public  void showDialog(String title, String message)
    {
        new AlertDialog.Builder(login.getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

}
