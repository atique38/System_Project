package com.example.ghuraghuri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class SpotList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecViewAdapter adapter;
    TextView nothing;
    ProgressBar progressBar;
    EditText search;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_list);

        nothing=findViewById(R.id.noth);
        progressBar=findViewById(R.id.pr_main);
        search =findViewById(R.id.search_edt);

        auth=FirebaseAuth.getInstance();

        recyclerView=findViewById(R.id.recView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new RecViewAdapter(SpotList.this);

        progressBar.setVisibility(View.VISIBLE);

        readData(new callBack() {
            @Override
            public void onCallback(ArrayList<String> plc_name, ArrayList<String> rating, ArrayList<String> tag) {

                Constant.tmp_name.clear();
                Constant.tmp_rating.clear();
                Constant.tmp_tag.clear();

                Constant.plc_name.clear();
                Constant.rating.clear();
                Constant.tag.clear();


                Constant.tmp_name.addAll(plc_name);
                Constant.tmp_rating.addAll(rating);
                Constant.tmp_tag.addAll(tag);

                Constant.plc_name.addAll(plc_name);
                Constant.rating.addAll(rating);
                Constant.tag.addAll(tag);

                if (Constant.tmp_name.isEmpty())
                {
                    nothing.setVisibility(View.VISIBLE);
                }
                else
                {
                    nothing.setVisibility(View.GONE);

                    recyclerView.setAdapter(adapter);
                }
                progressBar.setVisibility(View.GONE);


            }
        });

        readfav();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text=editable.toString().toLowerCase();
                filter(text);

            }
        });
    }

    interface callBack
    {

        void onCallback(ArrayList<String> plc_name,
                        ArrayList<String > rating,
                        ArrayList<String> tag
        );
    }

    void readData(final callBack cb)
    {
        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference().child("Admin");
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constant.admin_uid=String.valueOf(snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Places");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> plc_name=new ArrayList<>();
                ArrayList<String > rating=new ArrayList<>();
                ArrayList<String> tag=new ArrayList<>();

                Constant.plc_id.clear();
                Constant.tmp_plc_id.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.exists())
                    {
                        plc_name.add(String.valueOf(dataSnapshot.child("Name").getValue()));
                        //rating.add(String.valueOf(dataSnapshot.child("Ratings").getValue()));
                        float rt=Float.parseFloat(String.valueOf(dataSnapshot.child("Ratings").getValue()));
                        String ans = new DecimalFormat("##.#").format(rt);
                        rating.add(ans);
                        tag.add(String.valueOf(dataSnapshot.child("Tag").getValue()));
                        Constant.plc_id.add(dataSnapshot.getKey());
                        Constant.tmp_plc_id.add(dataSnapshot.getKey());
                    }
                }

                cb.onCallback(plc_name,rating,tag);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    void filter(String s)
    {
        ArrayList<String> temp = new ArrayList<>(Constant.plc_name);

        Constant.tmp_name.clear();
        Constant.tmp_rating.clear();
        Constant.tmp_plc_id.clear();
        Constant.tmp_tag.clear();

        int i;
        for(i=0;i<temp.size();i++)
        {
            if(temp.get(i).toLowerCase().contains(s))
            {
                Constant.tmp_plc_id.add(Constant.plc_id.get(i));
                Constant.tmp_name.add(Constant.plc_name.get(i));
                Constant.tmp_rating.add(Constant.rating.get(i));
                Constant.tmp_tag.add(Constant.tag.get(i));
            }

        }
        adapter.notifyDataSetChanged();

        if (Constant.tmp_name.isEmpty())
        {
            nothing.setVisibility(View.VISIBLE);
        }
        else
        {
            nothing.setVisibility(View.GONE);
        }
    }

    void readfav()
    {
        Constant.key.clear();
        FirebaseAuth auth=FirebaseAuth.getInstance();
        String uid= Objects.requireNonNull(auth.getCurrentUser()).getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().
                child("User").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Favourite List").exists())
                {
                    for (DataSnapshot snapshot1:snapshot.child("Favourite List").getChildren())
                    {
                        Constant.key.add(snapshot1.getKey());
                        //System.out.println(snapshot1.getKey());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}