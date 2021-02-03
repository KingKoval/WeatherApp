package com.example.weather;


import com.example.weather.pojo.current.Example;
import com.example.weather.pojo.forecast.ForecastWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Weather {

    @GET("weather")
    Call<Example> currentWeather(@Query("lat") double latitude,
                                 @Query("lon") double longtitude,
                                 @Query("appid") String api,
                                 @Query("units") String temp,
                                 @Query("lang") String lang);


    @GET("forecast")
    Call<ForecastWeather> forecastWeather(@Query("lat") double latitude,
                                          @Query("lon") double longtitude,
                                          @Query("appid") String api,
                                          @Query("units") String temp,
                                          @Query("lang") String lang);

}
