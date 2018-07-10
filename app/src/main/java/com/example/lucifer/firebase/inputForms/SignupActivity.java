package com.example.lucifer.firebase.inputForms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lucifer.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout email, password;
    private FirebaseAuth firebaseAuth;
    private MaterialButton signup;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private boolean empty = false;
    ProgressDialog pr;
    String TAG = "firebase_book_log";
    private ArrayList<TextInputLayout> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email = findViewById(R.id.email_layout);
        password = findViewById(R.id.password_layout);
        signup = findViewById(R.id.signup_button);
        pr = new ProgressDialog(this);
        pr.setTitle("Wait");
        pr.setMessage("Signing up");
        pr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pr.setIndeterminate(true);
        pr.setCancelable(false);
        list.add(email);
        list.add(password);
        firebaseAuth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (TextInputLayout textInputLayout : list) {
                    if (textInputLayout.getEditText().getText().toString().trim().equals("")) {
                        textInputLayout.setError("Field cannot be empty");
                        empty = false;
                    }
                }
                if (!empty) {
                    pr.show();
                    add();
                }

            }
        });
    }

    private void add() {
        String memail = email.getEditText().getText().toString().trim();
        String mpass = password.getEditText().getText().toString().trim();
        firebaseAuth.createUserWithEmailAndPassword(memail, mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "succesfull: ");
                    pr.dismiss();
                    user = firebaseAuth.getCurrentUser();
                    showverify();

                } else if (!task.isSuccessful()) {
                    pr.dismiss();
                    new AlertDialog.Builder(SignupActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                            .setTitle("Oops")
                            .setMessage("Something Seems wrong \n" + task.getException())
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
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void showverify() {
        Log.d(TAG, "verify dialog: ");
        new AlertDialog.Builder(SignupActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                .setTitle("User verification")
                .setMessage("Please verify youremail")
                .setCancelable(false)
                .setPositiveButton("Send email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    new AlertDialog.Builder(SignupActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                                            .setTitle("Oops")
                                            .setMessage("Looks like your email doesnt exists")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    startActivity(new Intent(SignupActivity.this, login.class));
                                                }
                                            })
                                            .create()
                                            .show();
                                }
                                Toast.makeText(SignupActivity.this, "Verification mail sent", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, FormUser.class));
                            }
                        });

                    }
                })
                .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        startActivity(new Intent(SignupActivity.this, login.class));
                    }
                })
                .create()
                .show();
    }
}
