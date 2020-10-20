//Victor Ochia. 2020 WeatherSpy



package com.vic.myweatherapp;
import org.json.JSONException;
import org.json.JSONObject;


//class gets the current weather and description
public class CurrentWeatherDataModel {

    private String temperature;
    private String description;
    private String city;
    private String icon;

     //
    public static CurrentWeatherDataModel convertJson(JSONObject dailyjson) {

        CurrentWeatherDataModel weather = new CurrentWeatherDataModel();
        try {
            weather.city = dailyjson.getString("name");
            weather.description = dailyjson.getJSONArray("weather").getJSONObject(0).getString("description");
            weather.icon = dailyjson.getJSONArray("weather")
                    .getJSONObject(0).getString("icon");




            double temp = (dailyjson.getJSONObject("main").getDouble("temp"));
            weather.temperature = String.valueOf((int) Math.rint(temp));


        } catch (JSONException e) {

            e.printStackTrace();

        }


        return weather;

    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getCity() {
        return city;
    }

    public String getTemperature() {
        return temperature;
    }


}