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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
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



                Button edit = findViewById(R.id.editButton);

                //if(mWeatherPacks.size() > 1)




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

        return "https://openweathermap.org/img/wn/" + icon + "@2x.png";

    }


    public void Edit(View v){

        if(mWeatherPacks.size() < 2){



           // Toast.makeText(MainActivity.this, "Add a forecast from a city to edit", Toast.LENGTH_LONG).show();

        }


    }



    public void addLocation(View v){

        Button edit = findViewById(R.id.editButton);
        Button done = findViewById(R.id.doneButton);


        if(edit.getVisibility() == View.INVISIBLE) {

            edit.setVisibility(View.VISIBLE);
            done.setVisibility(View.GONE);



        }


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
                            SetForecastView();

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


    public void doneWithDelete(View v){

        doneDeleting();

    }



    public void doneDeleting(){


        EditText addLocation = findViewById(R.id.AddLocation);
        addLocation.setEnabled(true);
        ImageView addButton = findViewById(R.id.addbutton);
        addButton.setClickable(true);


        final LinearLayout forecastLayout = findViewById(R.id.ForecastLayout);


        int count = forecastLayout.getChildCount();

        for(int i = 2; i < count; ++i) {
            View current = forecastLayout.getChildAt(i);
            current.setId(View.generateViewId());
            LinearLayout cityWrap = findViewById(current.getId());
            LinearLayout cityAndDelete = (LinearLayout) cityWrap.getChildAt(0);
            View deletion = cityAndDelete.getChildAt(0);
            deletion.setVisibility(View.GONE);

        }

        Button editButton = findViewById(R.id.editButton);
        editButton.setVisibility(View.VISIBLE);

        Button doneButton = findViewById(R.id.doneButton);
        doneButton.setVisibility(View.GONE);



    }


    public void deleteForecast(View v){


        EditText addLocation = findViewById(R.id.AddLocation);
        addLocation.setEnabled(false);
        ImageView addButton = findViewById(R.id.addbutton);
        addButton.setClickable(false);

       Button editButton = findViewById(R.id.editButton);

        editButton.setVisibility(View.GONE);

        Button doneButton = findViewById(R.id.doneButton);

        doneButton.setVisibility(View.VISIBLE);



        final LinearLayout forecastLayout = findViewById(R.id.ForecastLayout);






        int count = forecastLayout.getChildCount();
        View current = null;


        for(int i = 2; i < count; ++i) {

            current = forecastLayout.getChildAt(i);
            current.setId(View.generateViewId());
            LinearLayout cityWrap = findViewById(current.getId());
            LinearLayout hg = (LinearLayout) cityWrap.getChildAt(0);

            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            final ImageButton deleteButton = new ImageButton(this);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            deleteButton.setImageResource(R.drawable.deletebutton);
            deleteButton.setBackgroundColor(0x3C56FA);
            hg.addView(deleteButton, 0);



            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    View current = (View) deleteButton.getParent();
                    LinearLayout removal = (LinearLayout) current.getParent();
                    ((ViewGroup)removal.getParent()).removeView(removal);

                    doneDeleting(); //remove the delete buttons


                    Button editButton = findViewById(R.id.editButton);
                    editButton.setVisibility(View.VISIBLE);

                    Button doneButton = findViewById(R.id.doneButton);
                    doneButton.setVisibility(View.GONE);


                }
            });














        }



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






    }


    void SetForecastView(){


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

        LinearLayout forecastLayout = findViewById(R.id.ForecastLayout);

        for(int i = mWeatherPacks.size() -1; i < mWeatherPacks.size() && mWeatherPacks.size() != 1; ++i) {

            LinearLayout forecastContainer = new LinearLayout(this);
            Resources r = getResources();
            float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 140, r.getDisplayMetrics());
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            forecastContainer.setLayoutParams(lp1);
            forecastContainer.setOrientation(LinearLayout.HORIZONTAL);
            forecastContainer.setId(View.generateViewId());
            LinearLayout cityWrap = new LinearLayout(this);
            cityWrap.setLayoutParams(lp1);
            cityWrap.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout deleteWrap = new LinearLayout(this);
            deleteWrap.setOrientation(LinearLayout.HORIZONTAL);
            deleteWrap.setLayoutParams(lp3);
            TextView currentCity = new TextView(this);
            currentCity.setLayoutParams(lp3);
            currentCity.setText(mWeatherPacks.get(i).getWeather().getCity());
            currentCity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            cityWrap.addView(deleteWrap);
            deleteWrap.addView(currentCity);
            GridLayout gridLayout = new GridLayout(this);
            gridLayout.setColumnCount(8);
            gridLayout.setRowCount(1);
            gridLayout.setLayoutParams(lp1);
            gridLayout.setId(View.generateViewId());
            HorizontalScrollView scroll = new HorizontalScrollView(this);
            scroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            scroll.setScrollbarFadingEnabled(false);

            forecastLayout.addView(cityWrap);
            cityWrap.addView(scroll);


            scroll.addView(forecastContainer);

            forecastContainer.addView(gridLayout);
            final ScrollView Scrollme = findViewById(R.id.parentScroll);


            for(int j = 0, l = arrayStart; j < mWeatherPacks.get(i).Forecast.size(); ++j) {


                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ForecastDayTemplate forecastDayTemplate = new ForecastDayTemplate();
                fragmentTransaction.add(gridLayout.getId(), forecastDayTemplate);
                fragmentTransaction.commit();
                Bundle forecastDayBundle = new Bundle();
                forecastDayBundle.putString("dailyHigh", mWeatherPacks.get(i).getForecast().get(j).getMaximum_temperature());
                forecastDayBundle.putString("dailyLow", mWeatherPacks.get(i).getForecast().get(j).getMinimum_temperature());
                forecastDayBundle.putString("Icon", mWeatherPacks.get(i).getForecast().get(j).getIcon());

                if(j == 0)
                    forecastDayBundle.putString("weekday", "Today");
                else
                    forecastDayBundle.putString("weekday", DaysOfTheWeek[l]);


                if(l == DaysOfTheWeek.length -1)
                    l = 0;
                else
                    l+= 1;

                forecastDayTemplate.setArguments(forecastDayBundle);


                Scrollme.fullScroll(ScrollView.FOCUS_DOWN);

            }



        }




    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mYLocationManager != null){
            mYLocationManager.removeUpdates(mYLocationListener);

        }



    }
}

