package com.example.weather.pojo.current;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    @Expose
    private double speed;


    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
