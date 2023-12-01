package com.example.ghuraghuri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BlogList extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView nothing;

    BlogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_list);

        recyclerView=findViewById(R.id.blog_recView);
        progressBar=findViewById(R.id.blog_pr);
        nothing=findViewById(R.id.blog_noth);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new BlogAdapter(this);
        progressBar.setVisibility(View.VISIBLE);
        getBlogs();
    }

    void getBlogs(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Blog");
        Constant.blogTitle.clear();
        Constant.blogLocation.clear();
        Constant.blogDate.clear();
        Constant.bloggerName.clear();
        Constant.blogThumb.clear();
        Constant.blogId.clear();
        Constant.blogDetails.clear();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    String uid= Objects.requireNonNull(snapshot1.child("uid").getValue()).toString();

                    if(Constant.curr_uid.equals(uid))
                    {
                        Constant.blogId.add(snapshot1.getKey());
                        Constant.blogTitle.add(Objects.requireNonNull(snapshot1.child("title").getValue()).toString());
                        Constant.blogLocation.add(Objects.requireNonNull(snapshot1.child("location").getValue()).toString());
                        Constant.blogDate.add(Objects.requireNonNull(snapshot1.child("date").getValue()).toString());
                        Constant.bloggerName.add(Objects.requireNonNull(snapshot1.child("blogger").getValue()).toString());
                        Constant.blogThumb.add(Objects.requireNonNull(snapshot1.child("Thumbnail").getValue()).toString());
                        Constant.blogDetails.add(Objects.requireNonNull(snapshot1.child("details").getValue()).toString());
                    }

                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void setAdapter(){
        if(Constant.blogId.isEmpty()){
            nothing.setVisibility(View.VISIBLE);
        }
        else
        {
            nothing.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}