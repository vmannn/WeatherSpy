package com.example.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import android.os.Handler;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    String API_KEY = "4e50edb8686ec742118a9852ad2960dd";
    long MINIMUM_TIME = 5000;
    float MINIMUM_DISTANCE = 1000;
    int REQ_CODE = 123;
    String API_URL = "http://api.openweathermap.org/data/2.5/weather";
    String FORECAST_API_URL = "http://api.openweathermap.org/data/2.5/onecall?";

    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    LocationManager mYLocationManager;
    LocationListener mYLocationListener;
    boolean IsDaily;
    boolean IsForecast;
    RequestParams forecastparams = new RequestParams();

    JSONObject daily;
    JSONObject forecast;
    ArrayList <WeatherPack> mWeatherPacks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d("MyWeatherApp", "onResume activated");
        Log.d("MyWeatherApp", "Getting data for location");

        final Handler handler = new Handler();
        final int delay = 900000; //15 minutes

        handler.postDelayed(new Runnable(){
            public void run(){
                CurrentLocationWeather();
                handler.postDelayed(this, delay);
            }
        }, delay);

        CurrentLocationWeather();
    }


    private void CurrentLocationWeather() {

        mYLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mYLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                Log.d("MyWeatherApp", "longitude: " + longitude);
                Log.d("MyWeatherApp", "latitude: " + latitude);

                String units = "imperial";
                String exclude = "current,minutely,hourly";

                RequestParams dailyparams = new RequestParams();

                dailyparams.put("lon", longitude);
                dailyparams.put("lat", latitude);
                dailyparams.put("units", units);
                dailyparams.put("appid", API_KEY);
                forecastparams.put("lon", longitude);
                forecastparams.put("lat", latitude);
                forecastparams.put("units", units);
                forecastparams.put("appid", API_KEY);
                forecastparams.put("exclude", exclude);
                APICall(dailyparams);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
            return;
        }
        mYLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MINIMUM_TIME, MINIMUM_DISTANCE, mYLocationListener);


        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_CODE){

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                CurrentLocationWeather();
            }




        }

        else{

            Log.d("MyWeatherApp", "Permission denied");
        }
    }

    public String GetIconUrl(String icon){

        String URL = "https://openweathermap.org/img/wn/" + icon + "@2x.png";

        return URL;

    }
    private void APICall(RequestParams dailyparams){

        AsyncHttpClient myclient = new AsyncHttpClient();
        CurrentWeatherDataModel citypack = new CurrentWeatherDataModel();



        myclient.get(API_URL, dailyparams, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response){

            //Log.d("MyWeatherApp", response.toString() + "\n***********************************************************\n");

                Log.d("MyWeatherApp", response.toString() + "\n***********************************************************\n");

                AsyncHttpClient myclient2 = new AsyncHttpClient();


                myclient2.get(FORECAST_API_URL, forecastparams, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response2){

                        WeatherPack mypack = new WeatherPack();
                        mypack.setForecast(ForecastDataModel.convertforecastjson(response2));
                        mypack.setWeather(CurrentWeatherDataModel.convertJson(response));
                        Log.d("DyWeatherApp", "HASASDF: " + response.toString());


                        if(mWeatherPacks.isEmpty()) {
                            mWeatherPacks.add(mypack);
                        }

                        else{
                            if(mWeatherPacks.get(0) != null)
                            mWeatherPacks.set(0, mypack);
                        }
                        SetView();


                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response2){
                        Log.e("MyWeatherApp", "faiure" + e.toString() + " " + statusCode);
                        Log.d("MyWeatherApp", "faiure" + e.toString() + " " + statusCode);
                        Toast.makeText(MainActivity.this, "Network error please try again forecast", Toast.LENGTH_SHORT).show();
                        IsForecast = false;


                    }


                });








            }


            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.e("MyWeatherApp", "faiure " + e.toString() + " " + statusCode);
                Log.d("MyWeatherApp", "faiure " + e.toString() + " " + statusCode);
                Toast.makeText(MainActivity.this, "Network error please try again daily", Toast.LENGTH_SHORT).show();

                IsDaily = false;

            }


        });

       /* AsyncHttpClient myclient2 = new AsyncHttpClient();


        myclient2.get(FORECAST_API_URL, forecastparams, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response2){

             //   Log.d("MyWeatherApp", response2.toString());
                IsForecast = true;
                forecast = response2;

            }

            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response2){
                Log.e("MyWeatherApp", "faiure" + e.toString() + " " + statusCode);
                Log.d("MyWeatherApp", "faiure" + e.toString() + " " + statusCode);
                Toast.makeText(MainActivity.this, "Network error please try again forecast", Toast.LENGTH_SHORT).show();
                IsForecast = false;


            }


        }); */


        //if(IsDaily && IsForecast){

    }





    void SetView(){

        if(mWeatherPacks.isEmpty()){
            return;
        }

        TextView city = findViewById(R.id.City);
       city.setText(mWeatherPacks.get(0).getWeather().getCity());

       String iconurl = GetIconUrl(mWeatherPacks.get(0).getWeather().getIcon());

       Log.d("MyWeatherApp", iconurl);

        ImageView Icon = findViewById(R.id.Icon);

        Picasso.get().load(iconurl).resize(100, 100).into(Icon);

        TextView temp = findViewById(R.id.Temperature);
        temp.setText(mWeatherPacks.get(0).getWeather().getTemperature() + "°F");

        TextView description = findViewById(R.id.Description);
        description.setText(mWeatherPacks.get(0).getWeather().getDescription());



    }

}

