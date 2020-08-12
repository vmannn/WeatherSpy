package com.example.myweatherapp;

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


}

