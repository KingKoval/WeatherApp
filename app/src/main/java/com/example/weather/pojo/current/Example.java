package com.example.weather.pojo.current;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Example {

    @SerializedName("weather")
    private List<Weather> weather = null;
    @SerializedName("main")
    private Main main;
    @SerializedName("name")
    public String name;
    @SerializedName("dt")
    private int dt;
    @SerializedName("wind")
    private Wind wind;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDt() { return dt; }

    public void setDt(int dt) { this.dt = dt; }
}
