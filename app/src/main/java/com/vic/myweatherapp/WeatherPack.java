//Victor Ochia. 2020 WeatherSpy


package com.vic.myweatherapp;

import java.util.ArrayList;

//This class holds a forecast and current weather for a city
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
