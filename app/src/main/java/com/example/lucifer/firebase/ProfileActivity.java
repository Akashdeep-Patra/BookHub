package com.example.lucifer.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.lucifer.firebase.Models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends AppCompatActivity  {
   private  TextView name,city,college;
   private AppCompatImageView profile_image;
   FirebaseFirestore db;
   private List<UserDetails> users;
   private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name=findViewById(R.id.name);
        city=findViewById(R.id.city);
        college=findViewById(R.id.college);
        profile_image=findViewById(R.id.profileimage);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        db=FirebaseFirestore.getInstance();
        CollectionReference user_reference=db.collection("users");
        Query  user_query=user_reference.whereEqualTo("user_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        user_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
             if (task.isSuccessful())
             {
                  users=task.getResult().toObjects(UserDetails.class);
                  UserDetails user= users.get(0);
                  name.setText(user.getName());
                  city.setText(user.getLoaction());
                  college.setText(user.getCollege());
                    Transformation transformation= new RoundedTransformationBuilder()
                            .borderColor(Color.WHITE)
                            .borderWidthDp(5)
                            .cornerRadiusDp(50)
                            .oval(false)
                            .build();
                 Picasso.get().load(user.getImage()).centerCrop().transform(transformation).fit().into(profile_image);
                 progressDialog.dismiss();
             }
            }
        });
    }




}
