package com.example.lucifer.firebase;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucifer.firebase.Models.UserDetails;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookFull extends AppCompatActivity {
    private AppCompatImageView bookimage;
    private TextView book, user;
    private FirebaseFirestore db;
    private FloatingActionButton call, delete;
    private String number;
    public static final int Request_code = 2;
    String prevclass, curret_book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_full);
        Intent i = getIntent();
        curret_book = i.getStringExtra("bookname");
        prevclass = i.getStringExtra("class");
        db = FirebaseFirestore.getInstance();
        bookimage = findViewById(R.id.image_book);


        if (prevclass.equals("com.example.lucifer.firebase.bookList")) {
            delete = findViewById(R.id.delete);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    deletebook();
                }
            });
        } else {
            call = findViewById(R.id.call);
            call.setVisibility(View.VISIBLE);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callhim();
                }
            });
        }
        Picasso.get().load(i.getStringExtra("image")).fit().placeholder(R.drawable.ic_cloud_download_black_24dp).into(bookimage);
        book = findViewById(R.id.book_detail_full);
        book.setText(" Name: " + i.getStringExtra("bookname") +
                "\n\n Author: " + i.getStringExtra("author") +
                "\n\n Published in: " + i.getStringExtra("published") +
                "\n\n Genere: " + i.getStringExtra("genere") +
                "\n\n Price: " + i.getStringExtra("price"));
        user = findViewById(R.id.user_detail_full);
        db = FirebaseFirestore.getInstance();
        CollectionReference user_reference = db.collection("users");
        Query user_query = user_reference.whereEqualTo("user_id", i.getStringExtra("userid"));
        user_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<UserDetails> users = task.getResult().toObjects(UserDetails.class);
                    UserDetails userdoc = users.get(0);
                    user.setText(" Owner: " + userdoc.getName() +
                            "\n\nLocation: " + userdoc.getLoaction() +
                            "\n\nCollege: " + userdoc.getCollege() +
                            "\n\n");
                    number = userdoc.getNumber();

                }
            }
        });

    }

    public void callhim() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Request_code);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Request_code: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callhim();
                }
            }
        }
    }
//    public void deletebook(){
//        CollectionReference user_reference = db.collection("users");
//        Query user_query = user_reference.whereEqualTo("user_id",FirebaseAuth.getInstance().
//                getCurrentUser().getUid()).whereEqualTo("name",curret_book);
//        user_query.get().
//    }
}
