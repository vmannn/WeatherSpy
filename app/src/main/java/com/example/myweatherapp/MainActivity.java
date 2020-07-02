package com.example.myweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {


    String API_KEY = "4e50edb8686ec742118a9852ad2960dd";
    long MINIMUM_TIME = 6000;
    float MINIMUM_DISTANCE = 1000;
    int REQ_CODE = 123;

    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;

    LocationManager mYLocationManager;
    LocationListener mYLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d("NyWeatherApp", "onResume activated");
        Log.d("MyWeatherApp", "Getting data for location");
        CurrentLocationWeather();

    }


    private void CurrentLocationWeather() {

        mYLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mYLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

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

            Log.d("MyweatherApp", "Permission denied");
        }
    }
}

