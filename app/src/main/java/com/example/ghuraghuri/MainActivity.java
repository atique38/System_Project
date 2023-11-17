package com.example.ghuraghuri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ImageView menu_img;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ProgressBar progressBar;
    SliderView slide;
    RelativeLayout spot,shop,cart,vlog,tourPackage;

    FirebaseAuth auth;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu_img = findViewById(R.id.menu);
        drawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.nav);
        progressBar=findViewById(R.id.pr_main);
        slide=findViewById(R.id.imgSliderHome);
        spot=findViewById(R.id.spot_click);
        shop=findViewById(R.id.shop_click);
        cart=findViewById(R.id.cart_click);
        vlog=findViewById(R.id.vlog_layout);
        tourPackage=findViewById(R.id.tour_pcg);

        auth=FirebaseAuth.getInstance();

        Constant.curr_uid= Objects.requireNonNull(auth.getCurrentUser()).getUid();

        //home activity images
        int [] images={R.drawable.test1,
                R.drawable.test2,
                R.drawable.test3};

        ImageSliderAdapter imageSliderAdapter=new ImageSliderAdapter(images,this);
        slide.setSliderAdapter(imageSliderAdapter);
        slide.setIndicatorAnimation(IndicatorAnimationType.WORM);
        slide.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        slide.startAutoCycle();



        spot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SpotList.class);
                startActivity(intent);
            }
        });
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ProductList.class);
                startActivity(intent);
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Cart.class);
                startActivity(intent);
            }
        });
        vlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,VlogList.class);
                startActivity(intent);
            }
        });
        tourPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, PackageListUser.class);
                startActivity(intent);
            }
        });

        //checkUser();
        menu_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                   /* case R.id.adm:
                        intent=new Intent(MainActivity.this,Admin.class);
                        startActivity(intent);
                        break;*/

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
                    case R.id.vlog:
                        intent=new Intent(MainActivity.this,YourVlog.class);
                        startActivity(intent);
                        break;



                }
                return false;
            }
        });


    }





    /*void checkUser(){
        uid= Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Menu menu=navigationView.getMenu();
        MenuItem menuItem=menu.findItem(R.id.adm);

        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference().child("Admin");
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constant.admin_uid=String.valueOf(snapshot.getValue());

                if(Constant.admin_uid.equals(uid))
                {
                    menuItem.setVisible(true);
                }
                else
                {
                    menuItem.setVisible(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }*/

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}