//Victor Ochia. 2020 WeatherSpy


package com.example.myweatherapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//class gets the forecast for the day of the week when we feed it a json.
public class ForecastDataModel {

     private String minimum_temperature;
     private String maximum_temperature;
     private String icon;


     public String getMinimum_temperature() {
         return minimum_temperature;
     }

     public String getMaximum_temperature() {
         return maximum_temperature;
     }

     public void setMinimum_temperature(String minimum_temperature) {
         this.minimum_temperature = minimum_temperature;
     }

     public void setMaximum_temperature(String maximum_temperature) {
         this.maximum_temperature = maximum_temperature;
     }

     public void setIcon(String icon) {
         this.icon = icon;
     }


     public String getIcon() {
         return icon;
     }


     static public ArrayList<ForecastDataModel> convertforecastjson(JSONObject forecastjson) {

         ArrayList<ForecastDataModel> sevendayforecast = new ArrayList<>();
         try {
             for (int i = 0; i < 8; ++i) {
                 ForecastDataModel insert = new ForecastDataModel();


                 double temp = forecastjson.getJSONArray("daily").getJSONObject(i).getJSONObject("temp")
                         .getDouble("max");
                 insert.setMaximum_temperature(String.valueOf( (int) Math.rint(temp)));


                 temp = forecastjson.getJSONArray("daily").getJSONObject(i).getJSONObject("temp")
                         .getDouble("min");
                 insert.setMinimum_temperature(String.valueOf( (int) Math.rint(temp)));

                 insert.setIcon(forecastjson.getJSONArray("daily").getJSONObject(i).getJSONArray("weather")
                         .getJSONObject(0).getString("icon"));

                 sevendayforecast.add(insert);

             }

         } catch (JSONException e) {

             e.printStackTrace();

         }
                 return sevendayforecast;
     }



}


