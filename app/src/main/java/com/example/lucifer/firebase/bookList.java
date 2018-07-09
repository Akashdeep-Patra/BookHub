package com.example.lucifer.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class bookList extends AppCompatActivity  implements View.OnClickListener {
    FloatingActionButton addbooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        addbooks=findViewById(R.id.add_books);
        addbooks.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.add_books:
                    startActivity(new Intent(bookList.this,bookForm.class));
            }
    }
}
