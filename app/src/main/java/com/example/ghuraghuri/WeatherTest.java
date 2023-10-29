package com.example.ghuraghuri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherTest extends AppCompatActivity {
    TextView temperature;
    Button button;

    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_test);

        temperature=findViewById(R.id.temp_txt);
        button=findViewById(R.id.temp_btn);

        url="https://api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid=9dd5ca196deda19d4cbf43066a21c4f2";
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue= Volley.newRequestQueue(WeatherTest.this);

                JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //JSONObject object=response.getJSONObject("list");
                            JSONArray array=response.getJSONArray("list");
                            for(int i=0;i<array.length();i++){
                                JSONObject object=array.getJSONObject(i);
                                JSONObject object2=object.getJSONObject("main");
                                String temp=object2.getString("temp");
                                temperature.setText(temp);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(WeatherTest.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WeatherTest.this, error.toString(), Toast.LENGTH_SHORT).show();
                        //System.out.println(error.toString());
                    }
                });

                queue.add(request);
            }
        });
    }
}