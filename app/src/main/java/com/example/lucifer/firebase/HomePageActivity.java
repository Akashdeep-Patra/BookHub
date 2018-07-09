package com.example.lucifer.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lucifer.firebase.Models.BookObject;
import com.example.lucifer.firebase.Models.UserDetails;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {
    MaterialButton logout;
    private ProgressDialog pr;
    private FirebaseAuth firebaseAuth;
    DrawerLayout drawerLayout;
    FloatingActionButton profile, add;
    private AppCompatImageView header;
    FirebaseFirestore db;
    private List<UserDetails> users;
    private List<BookObject> books;
    TextView header1, header2;
    private NavigationView navigationView;
    ProgressBar progressBar;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        books = new ArrayList<>();
        add = findViewById(R.id.add);
        add.setOnClickListener(this);
        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recycle_book);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.drawerlayout_homepage);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        profile = findViewById(R.id.profile);
        showbooks();
        profile.setOnClickListener(this);
        CollectionReference user_reference = db.collection("users");
        Query user_query = user_reference.whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        user_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    users = task.getResult().toObjects(UserDetails.class);
                    UserDetails user = users.get(0);

                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.WHITE)
                            .borderWidthDp(5)
                            .cornerRadiusDp(50)
                            .oval(false)
                            .build();
                    View hv = navigationView.getHeaderView(0);
                    header1 = hv.findViewById(R.id.header1);
                    header2 = hv.findViewById(R.id.header2);
                    header = hv.findViewById(R.id.header_image);
                    header1.setText(user.getName());
                    header2.setText(user.getCollege());
                    Picasso.get().load(user.getImage()).centerCrop().transform(transformation).fit().into(header);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profile) {
            startActivity(new Intent(HomePageActivity.this, ProfileActivity.class));
        } else if (view.getId() == R.id.add) {
            startActivity(new Intent(HomePageActivity.this, bookList.class));
        }
    }

    public void showbooks() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        CollectionReference reference = db.collection("books");
        Query query = reference;
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    books = task.getResult().toObjects(BookObject.class);
                    recyclerviewAdapter adapter = new recyclerviewAdapter(books,HomePageActivity.this);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setAdapter(adapter);

                }
            }
        });

    }
}
