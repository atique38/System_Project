package com.example.ghuraghuri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.Objects;

public class ProductInfo extends AppCompatActivity {
    SliderView slide;
    TextView name,price,rating,features,details;

    String key;
    ProductImageAdapter adapter;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        key=getIntent().getStringExtra("product_id");
        index= getIntent().getIntExtra("product_index",0);

        slide=findViewById(R.id.imgSlider_pdct_details);
        name=findViewById(R.id.pdct_name_info);
        price=findViewById(R.id.pdct_price_info);
        rating=findViewById(R.id.pdct_rate_info);
        features=findViewById(R.id.feature_detail);
        details=findViewById(R.id.desc_detail);

        adapter=new ProductImageAdapter(this);
        slide.setIndicatorAnimation(IndicatorAnimationType.WORM);
        slide.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        slide.startAutoCycle();

        getImage();
    }

    void getImage()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Product").child(key).child("Photos");
        Constant.productImg.clear();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot img:snapshot.getChildren()){
                    Constant.productImg.add(Objects.requireNonNull(img.getValue()).toString());
                }
                setInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void setInfo(){
        slide.setSliderAdapter(adapter);

        String rate=Constant.productRating.get(index)+"/5";
        String cost=Constant.productPrice.get(index)+"tk";
        name.setText(Constant.productName.get(index));
        rating.setText(rate);
        price.setText(cost);
        features.setText(Constant.productFeatures.get(index));
        details.setText(Constant.productDescription.get(index));
    }
}