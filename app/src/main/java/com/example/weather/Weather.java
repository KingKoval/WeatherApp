package com.example.weather;


import com.example.weather.pojo.Root;
import com.example.weather.pojo.current.Example;
import com.example.weather.pojo.forecast.ForecastWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Weather {

    @GET("onecall")
    Call<Root> forecastWeather(@Query("lat") double latitude,
                       @Query("lon") double longtitude,
                       @Query("execlude") String execlude,
                       @Query("appid") String api,
                       @Query("units") String temp,
                       @Query("lang") String lang);

    @GET("weather")
    Call<Example> currentWeather(@Query("lat") double latitude,
                           @Query("lon") double longtitude,
                           @Query("appid") String api,
                           @Query("units") String tempo,
                           @Query("lang") String lang);

}
