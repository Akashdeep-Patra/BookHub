package com.example.lucifer.firebase.inputForms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.Toast;

import com.example.lucifer.firebase.HomePageActivity;
import com.example.lucifer.firebase.Models.UserDetails;
import com.example.lucifer.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FormUser extends Activity {
    private TextInputLayout name, city, college, number;
    private MaterialButton upload;
    private AppCompatImageButton profile_image;
    private ArrayList<TextInputLayout> list = new ArrayList<>();
    private boolean empty = false;
    private int select_code = 2;
    private Uri image_path;
    private StorageReference rootstorage;
    Uri output;
    private FirebaseFirestore db;
    private UserDetails user = new UserDetails();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_user);
        name = findViewById(R.id.name_edit_form);
        number = findViewById(R.id.number_edit_form);
        city = findViewById(R.id.city_edit_form);
        college = findViewById(R.id.college_edit_form);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("\n please wait for a sec");
        progressDialog.setCancelable(false);

        db = FirebaseFirestore.getInstance();

        list.add(name);
        list.add(city);
        list.add(college);
        profile_image = findViewById(R.id.profileimage_form);
        upload = findViewById(R.id.upload_form);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, select_code);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (TextInputLayout textInputLayout : list) {
                    if (textInputLayout.getEditText().getText().toString().trim().equals("")) {
                        textInputLayout.setError("Field cannot be empty");
                        empty = true;
                    }
                }
                if (image_path == null) {
                    Toast.makeText(FormUser.this, "have to select a profile pictire", Toast.LENGTH_LONG).show();
                    empty = true;
                }
                if (!empty) {
                    progressDialog.show();

                    uploadImage("users", image_path);


                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == select_code && resultCode == RESULT_OK) {
            image_path = data.getData();
            Picasso.get().load(image_path).centerInside().fit().into(profile_image);
            empty = false;
        }
    }

    public void uploadImage(String folder_name, Uri imageuri) {

        StorageReference rootstorage = FirebaseStorage.getInstance().getReference();
        final StorageReference childref = rootstorage.child(folder_name).child(imageuri.getLastPathSegment());
        childref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                childref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        user.setImage(uri.toString());
                        user.setName(name.getEditText().getText().toString().trim());
                        user.setLoaction(city.getEditText().getText().toString().trim());
                        user.setCollege(college.getEditText().getText().toString().trim());
                        user.setNumber(number.getEditText().getText().toString().trim());
                        user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        db.collection("users").document().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(FormUser.this, "uploaded" + user.getImage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(FormUser.this, HomePageActivity.class));

                                } else
                                    Toast.makeText(FormUser.this, "Document not uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

}
