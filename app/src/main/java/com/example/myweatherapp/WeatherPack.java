package com.example.myweatherapp;

import java.util.ArrayList;

public class WeatherPack {

    ArrayList<ForecastDataModel> Forecast;

    CurrentWeatherDataModel weather;

    public ArrayList<ForecastDataModel> getForecast() {
        return Forecast;
    }

    public CurrentWeatherDataModel getWeather() {
        return weather;
    }

    public void setForecast(ArrayList<ForecastDataModel> forecast) {
        Forecast = forecast;
    }

    public void setWeather(CurrentWeatherDataModel weather) {
        this.weather = weather;
    }
}
