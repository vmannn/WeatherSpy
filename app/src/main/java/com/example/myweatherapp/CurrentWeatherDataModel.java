package com.example.myweatherapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CurrentWeatherDataModel {

    private String temperature;
    private int condition;
    private String description;
    private String city;
    private String icon;
    private ArrayList<ForecastDataModel> sevendayforecast;

    public static CurrentWeatherDataModel convertJson(JSONObject dailyjson, JSONObject forecastjson) {

        CurrentWeatherDataModel weather = new CurrentWeatherDataModel();
        try {
            weather.city = dailyjson.getString("name");

            weather.condition = dailyjson.getJSONArray("weather")
                    .getJSONObject(0).getInt("id");

            weather.description = dailyjson.getJSONArray("weather").getJSONObject(0).getString("description");
            weather.icon = dailyjson.getJSONArray("weather")
                    .getJSONObject(0).getString("icon");




            double temp = (dailyjson.getJSONObject("main").getDouble("temp"));
            weather.temperature = String.valueOf(Math.rint(temp));

            for(int i = 0; i< 8; ++i) {
            ForecastDataModel insert = new ForecastDataModel();


                temp = forecastjson.getJSONArray("daily").getJSONObject(i).getJSONObject("temp")
                        .getDouble("min");
                insert.setMaximum_temperature(String.valueOf(Math.rint(temp)));


                temp = forecastjson.getJSONArray("daily").getJSONObject(i).getJSONObject("temp")
                        .getDouble("max");
                insert.setMinimum_temperature(String.valueOf(Math.rint(temp)));

                insert.setIcon(forecastjson.getJSONArray("daily").getJSONObject(i).getJSONArray("weather")
                        .getJSONObject(0).getString("icon"));

                weather.sevendayforecast.add(insert);

            }
        } catch (JSONException e) {

            e.printStackTrace();

        }


        return weather;

    }

    public String getCity() {
        return city;
    }

    public String getTemperature() {
        return temperature;
    }


}