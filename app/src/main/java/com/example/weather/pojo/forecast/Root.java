package com.example.weather.pojo.forecast;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Root {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lon")
    @Expose
    private double lon;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("timezone_offset")
    @Expose
    private int timezoneOffset;
    @SerializedName("current")
    @Expose
    private Current current;
//    @SerializedName("minutely")
//    @Expose
//    private List<Minutely> minutely = null;
//    @SerializedName("hourly")
//    @Expose
//    private List<Hourly> hourly = null;
    @SerializedName("daily")
    @Expose
    private ArrayList<Dayli> daily = null;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

//    public List<Minutely> getMinutely() {
//        return minutely;
//    }
//
//    public void setMinutely(List<Minutely> minutely) {
//        this.minutely = minutely;
//    }
//
//    public List<Hourly> getHourly() {
//        return hourly;
//    }
//
//    public void setHourly(List<Hourly> hourly) {
//        this.hourly = hourly;
//    }

    public ArrayList<Dayli> getDaily() {
        return daily;
    }

    public void setDaily(ArrayList<Dayli> daily) {
        this.daily = daily;
    }

}
