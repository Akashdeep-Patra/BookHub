package com.example.lucifer.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.Toast;

import com.example.lucifer.firebase.Models.BookObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class bookForm extends AppCompatActivity {
    TextInputLayout bookname, author, publish_year, price, genere;
    AppCompatImageButton book_image;
    MaterialButton upload;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    private int select_code = 2;
    private Uri image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_form);
        bookname = findViewById(R.id.bookname);
        author = findViewById(R.id.author_name);
        publish_year = findViewById(R.id.pubish_year);
        price = findViewById(R.id.price);
        genere = findViewById(R.id.genere);
        book_image = findViewById(R.id.book_image);
        upload = findViewById(R.id.upload);
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("please wait");
        progressDialog.setCancelable(false);
        book_image.setOnClickListener(new View.OnClickListener() {
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
                uploadall();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == select_code && resultCode == RESULT_OK) {
            image_path = data.getData();
            Picasso.get().load(image_path).centerInside().fit().into(book_image);
        }
    }


    public void uploadall() {
        progressDialog.show();
        StorageReference rootstorage = FirebaseStorage.getInstance().getReference();
        final StorageReference childref = rootstorage.child("book_images").child(image_path.getLastPathSegment());
        childref.putFile(image_path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                childref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final BookObject book = new BookObject();
                        book.setImage(uri.toString());
                        book.setName(bookname.getEditText().getText().toString());
                        book.setAuthor(author.getEditText().getText().toString());
                        book.setGenere(genere.getEditText().getText().toString());
                        book.setPrice(price.getEditText().getText().toString());
                        book.setPublish_year(publish_year.getEditText().getText().toString());
                        book.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        db.collection("books").document().set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(bookForm.this, "uploaded" + book.getImage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(bookForm.this, bookList.class));
                                    finish();

                                } else
                                    Toast.makeText(bookForm.this, "Document not uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}
