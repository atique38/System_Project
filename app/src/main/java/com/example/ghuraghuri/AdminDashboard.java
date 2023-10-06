package com.example.ghuraghuri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboard extends AppCompatActivity {
    TextView userCount,agencyCount,spotCount;
    CardView addSpot,addProduct,order;

    long current;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        userCount=findViewById(R.id.user_count);
        agencyCount=findViewById(R.id.agency_count);
        spotCount=findViewById(R.id.spot_count);
        addSpot=findViewById(R.id.add_place);
        addProduct=findViewById(R.id.add_product);
        order=findViewById(R.id.orders);

        countUser();
        countAgency();
        countSpot();

        addSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(AdminDashboard.this,Admin.class);
                startActivity(intent);
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AdminDashboard.this,AddProduct.class);
                startActivity(intent);
            }
        });

    }

    private void startCounter(long end, TextView txt) {
        current = 0;
         // Set your desired end value
        long increment = 1; // Set your desired increment
        int duration = 1000;
        final Handler handler = new Handler(Looper.getMainLooper());
        final long step = Math.abs(duration / (end - current));

        Runnable runnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (current > end) {
                    txt.setText(Long.toString(end));
                } else {
                    txt.setText(Long.toString(current));
                }

                if (current == end) {
                    return;
                }

                current += increment;

                handler.postDelayed(this, step);
            }
        };

        handler.postDelayed(runnable, step);
    }

    void countUser()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("User");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total=snapshot.getChildrenCount();

                startCounter(total,userCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void countAgency()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Agency");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total=snapshot.getChildrenCount();

                startCounter(total,agencyCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void countSpot()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Places");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total=snapshot.getChildrenCount();

                startCounter(total,spotCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}