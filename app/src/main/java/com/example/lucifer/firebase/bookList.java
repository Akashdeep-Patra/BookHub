package com.example.lucifer.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;

import com.example.lucifer.firebase.Models.BookObject;
import com.example.lucifer.firebase.adapters.recyclerviewAdapter;
import com.example.lucifer.firebase.inputForms.bookForm;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class bookList extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton addbooks;
    List<BookObject> books;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        addbooks = findViewById(R.id.add_books);
        addbooks.setOnClickListener(this);
        books = new ArrayList<>();
        recyclerView = findViewById(R.id.book_aded);
        swipeRefreshLayout = findViewById(R.id.swipe);
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressbar);
        showbooks();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showbooks();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_books:
                startActivity(new Intent(bookList.this, bookForm.class));
        }
    }

    public void showbooks() {
        books.clear();
        recyclerView.setAdapter(null);
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
                    recyclerviewAdapter adapter = new recyclerviewAdapter(books, bookList.this);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);


                }
            }
        });

    }
}
