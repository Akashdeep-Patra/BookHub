
package com.example.lucifer.firebase.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lucifer.firebase.BookFull;
import com.example.lucifer.firebase.Models.BookObject;
import com.example.lucifer.firebase.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class recyclerviewAdapter extends RecyclerView.Adapter<recyclerviewAdapter.ViewHolder> {


    List<BookObject> list;
    Context context;

    public recyclerviewAdapter(List<BookObject> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycleriew_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookObject object_model = list.get(position);
        holder.text1.setText("NAME : " + object_model.getName());
        holder.text2.setText("AUTHOR : " + object_model.getAuthor());
        Picasso.get().load(object_model.getImage()).placeholder(R.drawable.ic_cloud_download_black_24dp).resize(400, 300).into(holder.img);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatImageView img;
        TextView text1, text2;

        public ViewHolder(@NonNull View view) {
            super(view);
            img = view.findViewById(R.id.profile_book_image);
            text1 = view.findViewById(R.id.text1);
            text2 = view.findViewById(R.id.text2);
            img.setOnClickListener(this);
            text1.setOnClickListener(this);
            text2.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Context c = view.getContext();
            Intent i = new Intent(context, BookFull.class);
            i.putExtra("bookname", list.get(getAdapterPosition()).getName());
            i.putExtra("author", list.get(getAdapterPosition()).getAuthor());
            i.putExtra("price", list.get(getAdapterPosition()).getPrice());
            i.putExtra("published", list.get(getAdapterPosition()).getPublish_year());
            i.putExtra("image", list.get(getAdapterPosition()).getImage());
            i.putExtra("genere", list.get(getAdapterPosition()).getGenere());
            i.putExtra("userid", list.get(getAdapterPosition()).getUser_id());
            i.putExtra("class", c.getClass().getName());
            c.startActivity(i);
        }
    }
}
