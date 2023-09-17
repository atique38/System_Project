package com.example.ghuraghuri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView menu_img;
    RecyclerView recyclerView;
    RecViewAdapter adapter;
    TextView nothing;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ProgressBar progressBar;
    EditText search;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu_img = findViewById(R.id.menu);
        nothing=findViewById(R.id.noth);
        drawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.nav);
        progressBar=findViewById(R.id.pr_main);
        search =findViewById(R.id.search_edt);

        auth=FirebaseAuth.getInstance();

        recyclerView=findViewById(R.id.recView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new RecViewAdapter(MainActivity.this);

       // recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

        progressBar.setVisibility(View.VISIBLE);

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

        readfav();

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
                progressBar.setVisibility(View.INVISIBLE);


            }
        });





        menu_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menu menu=navigationView.getMenu();
                MenuItem menuItem=menu.findItem(R.id.adm);
                if(Constant.admin_uid.equals(Objects.requireNonNull(auth.getCurrentUser()).getUid()))
                {
                    menuItem.setVisible(true);
                }
                else
                {
                    menuItem.setVisible(false);
                }

                if(!drawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                    navigationView.bringToFront();
                }
                else
                {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId())
                {

                    case R.id.acc:
                        intent=new Intent(MainActivity.this,Account.class);
                        startActivity(intent);
                        //Toast.makeText(MainActivity.this, "Account", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.adm:
                        intent=new Intent(MainActivity.this,Admin.class);
                        startActivity(intent);
                        break;

                    case R.id.fv_list:
                        intent=new Intent(MainActivity.this,FavouriteList.class);
                        startActivity(intent);
                        break;

                    case R.id.log:
                        SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                        SharedPreferences.Editor edit=preferences.edit();
                        edit.putBoolean("rem",false);
                        edit.apply();

                        intent=new Intent(MainActivity.this,SignIn.class);
                        intent.putExtra("from_main",true);
                        startActivity(intent);
                        break;
                    case R.id.hist:
                        intent=new Intent(MainActivity.this,History.class);
                        startActivity(intent);
                        break;
                    case R.id.rev:
                        intent =new Intent(MainActivity.this,YourReview.class);
                        startActivity(intent);
                        break;

                    case R.id.sugg:
                        intent=new Intent(MainActivity.this,Suggestion.class);
                        startActivity(intent);
                        break;



                }
                return false;
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
        DatabaseReference ref2=FirebaseDatabase.getInstance().getReference().child("Admin");
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}