package com.example.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    String tag = "0";
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

        final EditText newLocation = findViewById(R.id.AddLocation);


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

    private void locationSetup(String latitude, String longitude, int controlFlow){

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
        APICall(dailyparams, controlFlow);




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

                locationSetup(latitude, longitude, 0);

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

    static public String GetIconUrl(String icon){

        String URL = "https://openweathermap.org/img/wn/" + icon + "@2x.png";

        return URL;

    }



    public void addLocation(View v){

        ImageView add =  findViewById(R.id.addbutton);
        final EditText locationText = findViewById(R.id.AddLocation);


                String Location = locationText.getText().toString();

                RequestParams CityParam = new RequestParams();
                CityParam.put("q", Location);
                CityParam.put("appid", API_KEY);

                AsyncHttpClient myclient = new AsyncHttpClient();

                myclient.get(API_URL, CityParam, new JsonHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {



                     try {
                         String longitude = String.valueOf(response.getJSONObject("coord").getDouble("lon"));
                         String latitude = String.valueOf(response.getJSONObject("coord").getDouble("lat"));

                         Log.d("MyWeatherApp", "longitude new: " + String.valueOf(response.getJSONObject("coord").getDouble("lon")));
                         Log.d("MyWeatherApp", "latitude new: " + String.valueOf(response.getJSONObject("coord").getDouble("lat")));


                         locationSetup(latitude, longitude, 1);

                     }catch(JSONException e){
                         e.printStackTrace();
                     }


                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable e, final JSONObject response) {


                    }


                });



    }

    private void APICall(RequestParams dailyparams, final int controlFlow){

        AsyncHttpClient myclient = new AsyncHttpClient();
        CurrentWeatherDataModel citypack = new CurrentWeatherDataModel();



        myclient.get(API_URL, dailyparams, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response){


                Log.d("MyWeatherApp", response.toString() + "\n***********************************************************\n");

                AsyncHttpClient myclient2 = new AsyncHttpClient();


                myclient2.get(FORECAST_API_URL, forecastparams, new JsonHttpResponseHandler(){

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response2){

                        WeatherPack mypack = new WeatherPack();
                        mypack.setForecast(ForecastDataModel.convertforecastjson(response2));
                        mypack.setWeather(CurrentWeatherDataModel.convertJson(response));
                        Log.d("DyWeatherApp", "HASASDF: " + response.toString());

                        if(controlFlow == 0) {
                            if (mWeatherPacks.isEmpty()) {
                                mWeatherPacks.add(mypack);
                            } else {
                                if (mWeatherPacks.get(0) != null)
                                    mWeatherPacks.set(0, mypack);
                            }
                            SetView();
                        }
                        else{

                            mWeatherPacks.add(mypack);
                            SetView();

                        }

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
        TextView cityNameForecast = findViewById(R.id.CityNameForecast);
        cityNameForecast.setText(mWeatherPacks.get(0).getWeather().getCity());
       city.setText(mWeatherPacks.get(0).getWeather().getCity());

       String iconurl = GetIconUrl(mWeatherPacks.get(0).getWeather().getIcon());

       Log.d("MyWeatherApp", iconurl);

        ImageView Icon = findViewById(R.id.Icon);

        Picasso.get().load(iconurl).resize(100, 100).into(Icon);

        TextView temp = findViewById(R.id.Temperature);
        temp.setText(mWeatherPacks.get(0).getWeather().getTemperature() + "°F");

        TextView description = findViewById(R.id.Description);
        description.setText(mWeatherPacks.get(0).getWeather().getDescription());


        final String[] DaysOfTheWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday"};


        String weekday = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());

        int arrayStart = 0;

        for(int i = 0; i < DaysOfTheWeek.length; ++i){

            if (weekday.equals(DaysOfTheWeek[i])) {
                arrayStart = i;
                break;
            }

        }



        final int[] FragmentIds = {R.id.fragment, R.id.fragment2, R.id.fragment3,
                R.id.fragment4, R.id.fragment5, R.id.fragment6, R.id.fragment7,
                R.id.fragment8};

        for(int i = 0, j = arrayStart; i < 8; ++i){
            FragmentManager fm = getSupportFragmentManager();
            ForecastDayTemplate ForecastTemplate = new ForecastDayTemplate();
            fm.beginTransaction().replace(FragmentIds[i], ForecastTemplate).commit();
            Bundle forecastDayBundle = new Bundle();
            forecastDayBundle.putString("dailyHigh", mWeatherPacks.get(0).getForecast().get(i).getMaximum_temperature());
            forecastDayBundle.putString("dailyLow", mWeatherPacks.get(0).getForecast().get(i).getMinimum_temperature());
            forecastDayBundle.putString("Icon", mWeatherPacks.get(0).getForecast().get(i).getIcon());

            if(i == 0)
                forecastDayBundle.putString("weekday", "Today");
            else
              forecastDayBundle.putString("weekday", DaysOfTheWeek[j]);


            if(j == DaysOfTheWeek.length -1)
                j = 0;
            else
                j+= 1;


            ForecastTemplate.setArguments(forecastDayBundle);
        }

        final int[] DayIds = {R.id.day1, R.id.day2, R.id.day3,
                R.id.day4, R.id.day5, R.id.day6, R.id.day7,
                R.id.day8};
        //LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout forecastLayout = findViewById(R.id.ForecastLayout);

   //     if(forecastLayout.getChildCount() > 0)
          // forecastLayout.removeAllViews();
       // View v;

        Log.d("MyWeatherApp", mWeatherPacks.size() + " this is the size");

        for(int i = 1; i < mWeatherPacks.size() && mWeatherPacks.size() != 1; ++i) {




          //  View v = inflater.inflate(R.layout.addlocation, forecastLayout, false);


            LinearLayout forecastContainer = new LinearLayout(this);
            Resources r = getResources();
            float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, r.getDisplayMetrics());
            //(int) height
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            forecastContainer.setLayoutParams(lp1);
            forecastContainer.setOrientation(LinearLayout.HORIZONTAL);
            forecastContainer.setId(View.generateViewId());
            HorizontalScrollView scroll = new HorizontalScrollView(this);
            scroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            scroll.setScrollbarFadingEnabled(false);

            forecastLayout.addView(scroll);

            scroll.addView(forecastContainer);







            for(int j = 0; j < mWeatherPacks.get(i).Forecast.size(); ++j) {
               /* FragmentManager fm = getSupportFragmentManager();
                ForecastDayTemplate ForecastTemplate = new ForecastDayTemplate();
                Log.d("MyWeatherApp", "we get here");

                getSupportFragmentManager().findFragmentByTag("ggfgfd");

                tag = Integer.toString(Integer.parseInt(tag) + 1);
                fm.beginTransaction().replace(DayIds[j], ForecastTemplate, tag).commit();
                Bundle forecastDayBundle = new Bundle();
                forecastDayBundle.putString("dailyHigh", mWeatherPacks.get(i).getForecast().get(j).getMaximum_temperature());
                forecastDayBundle.putString("dailyLow", mWeatherPacks.get(i).getForecast().get(j).getMinimum_temperature());
                forecastDayBundle.putString("Icon", mWeatherPacks.get(i).getForecast().get(j).getIcon());

                ForecastTemplate.setArguments(forecastDayBundle); */




                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ForecastDayTemplate forecastDayTemplate = new ForecastDayTemplate();
                fragmentTransaction.add(forecastContainer.getId(), forecastDayTemplate);
                fragmentTransaction.commit();
                Bundle forecastDayBundle = new Bundle();
                forecastDayBundle.putString("dailyHigh", mWeatherPacks.get(i).getForecast().get(j).getMaximum_temperature());
                forecastDayBundle.putString("dailyLow", mWeatherPacks.get(i).getForecast().get(j).getMinimum_temperature());
                forecastDayBundle.putString("Icon", mWeatherPacks.get(i).getForecast().get(j).getIcon());
                forecastDayTemplate.setArguments(forecastDayBundle);







            }



          //  forecastLayout.addView(v);
        }



    }


     void locationReceived(){

         final EditText newLocation = findViewById(R.id.AddLocation);
         newLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View v, boolean hasFocus) {
                 if(hasFocus)
                     newLocation.setHint("");
                     else
                         newLocation.setHint("Add Location");
             }
         });






     }

}

