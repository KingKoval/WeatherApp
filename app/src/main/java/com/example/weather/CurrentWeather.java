package com.example.weather;


import com.example.weather.POJO.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrentWeather {

    @GET("weather")
    Call<Example> weather(@Query("lat") double latitude,
                          @Query("lon") double longtitude,
                          @Query("appid") String api,
                          @Query("units") String temp,
                          @Query("lang") String lang);
}
