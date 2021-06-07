package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.pojo.forecast.Dayli;
import com.example.weather.pojo.forecast.Root;
import com.example.weather.pojo.current.Example;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;

    private static final int REQUEST_CODE = 1;

    private double latitude, longtitude;
    private String city;

    private TextView textView_city,
                    textView_temp,
                    textView_weath,
                    textView_day,
                    textView_wind,
                    textView_temps,
                    textView_humidity;

    private SwipeRefreshLayout swipe_refreshWeather;
    private RelativeLayout bottomSheets;
    private ShapeDrawable background_forecastWeather;

    private AppCompatButton button_yes_gps, button_no_gps;

    private Button button_forecastWeather;

    private RelativeLayout background;
    
    private Dialog dialog_disableGps;

    private ImageButton button_myLocation;

    private ImageView imageView_sun;

    SharedPreferences settings;

    private RecyclerView recView_forecastWeather;
    private ForecastWeatherAdapter adapter;
    private ArrayList<Dayli> list_forecastWeather;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        textView_city = findViewById(R.id.textView_city);
        textView_temp = findViewById(R.id.textView_temp);
        textView_weath = findViewById(R.id.textView_weath);
        textView_day = findViewById(R.id.textView_day);
        textView_wind = findViewById(R.id.textView_wind);
        textView_temps = findViewById(R.id.textView_temps);
        textView_humidity = findViewById(R.id.textView_humidity);

        button_forecastWeather = findViewById(R.id.button_forecastWeather);
        button_myLocation = findViewById(R.id.button_myLocation);

        swipe_refreshWeather = findViewById(R.id.swipe_refreshWeather);

        background = findViewById(R.id.background);

        imageView_sun = findViewById(R.id.imageView_sun);

        settings = this.getSharedPreferences("settings", MODE_PRIVATE);


        if(!checkPermission()){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            locationListener.onProviderDisabled(LocationManager.NETWORK_PROVIDER);

        } else {

            if(!checkConnected()){
                getLastCurrentWeather();
                Toast.makeText(this, "No internet conection", Toast.LENGTH_LONG).show();
            } else {
                getLastCurrentWeather();
                getWeather();

                button_myLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getLocation();
                    }
                });
                //getForecastWeather();
                button_forecastWeather.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_in_bottom,
                                        R.anim.slide_in_top,
                                        R.anim.slide_out_bottom,
                                        R.anim.slide_out_top)
                                .replace(R.id.container, new ForecastWeatherFragment())
                                .commit();

                        getWeather();
                    }
                });

            }
        }

        swipe_refreshWeather.setColorSchemeColors(Color.BLUE);
        swipe_refreshWeather.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeather();
                Log.i("GPS!!!", String.valueOf(latitude) + "; " + String.valueOf(longtitude));

                swipe_refreshWeather.setRefreshing(false);
            }
        });
    }


    private boolean checkPermission(){
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(fineLocation != PackageManager.PERMISSION_GRANTED && coarseLocation != PackageManager.PERMISSION_GRANTED){
                return false;
            } else{
                return true;
            }
        } else{
            return true;
        }

    }

    private boolean checkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wifi == null && !wifi.isConnected()) && (mobile == null && !mobile.isConnected())){
            return false;
        } else {
            return true;
        }
    }

    private void getLastCurrentWeather() {
        textView_city.setText(settings.getString("city", "Not found"));
        textView_temp.setText(settings.getString("temp", "0"));
        textView_weath.setText(settings.getString("weath", "Not found"));
        textView_day.setText(settings.getString("day", "Today"));
        textView_temps.setText(settings.getString("temps", "- / -"));
        textView_humidity.setText(settings.getString("humidity", "- %"));
        textView_wind.setText(settings.getString("wind", "- m/s"));

        String bg = settings.getString("weath", "Not found");

        String overcastBg = "Overcast clouds";
        String ScatteredBg = "Scattered clouds";

        try {
            setBackground(bg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getWeather(){
        //https://api.openweathermap.org/data/2.5/weather?q=krasyliv&appid=0fcdeed0b44f8572a682c8837f51a541&units=metric&lang=en
        final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
        final String API = "0fcdeed0b44f8572a682c8837f51a541";
        final String CELSIUS = "metric";
        final String LANG_RU = "ru";
        final String LANG_PL = "pl";
        final String LANG_EN = "en";
        final String DAYLI = "dayli";

        SharedPreferences.Editor editor = settings.edit();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Weather weather = retrofit.create(Weather.class);

        Call<Example> exampleCall = weather.currentWeather((double) settings.getFloat("lat", 0),
                                                            (double) settings.getFloat("lon", 0),
                                                                  API, CELSIUS, LANG_EN);
        exampleCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                try {
                    Log.i("GPS", String.valueOf(settings.getFloat("lat", 0)) + " / " + String.valueOf(settings.getFloat("lon", 0)));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

                    String overcastBg = "Overcast clouds";
                    String ScatteredBg = "Scattered clouds";


                    String day = dateFormat.format(new Date((long)response.body().getDt() * 1000));
                    String dayFormated = day.substring(0, 1).toUpperCase() + day.substring(1);
                    String city = response.body().getName();
                    String temp = String.valueOf((int)(response.body().getMain().getTemp()));
                    String weath = response.body().getWeather().get(0).getDescription().substring(0, 1).toUpperCase()
                            + response.body().getWeather().get(0).getDescription().substring(1);
                    String wind = String.valueOf(response.body().getWind().getSpeed()) + " m/s";
                    String humidity = String.valueOf(response.body().getMain().getHumidity()) + " %";

                    textView_city.setText(city);
                    textView_temp.setText(temp);
                    textView_weath.setText(weath);
                    textView_day.setText(dayFormated);
                    textView_wind.setText(wind);
                    textView_humidity.setText(humidity);

                    setBackground(weath);

                    editor.putString("city", city);
                    editor.putString("temp", temp);
                    editor.putString("weath", weath);
                    editor.putString("day", dayFormated);
                    editor.putString("humidity", humidity);
                    editor.putString("wind", wind);

                } catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

            }
        });

        //Weather forecastWeather = retrofit.create(Weather.class);

        Call<Root> weatherCall = weather.forecastWeather((double) settings.getFloat("lat", 0),
                                                            (double) settings.getFloat("lon", 0),
                                                            DAYLI, API, CELSIUS, LANG_EN);

        weatherCall.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {

                try {
                    String temps = String.valueOf((int) response.body().getDaily().get(0).getTemp().getMin()) + "° / "
                            + String.valueOf((int) response.body().getDaily().get(0).getTemp().getMax()) + "°";

                    textView_temps.setText(temps);

                    editor.putString("temps", temps);

                    list_forecastWeather = new ArrayList<>();
                    for(int i = 1; i < 8; i++){
                        list_forecastWeather.add(response.body().getDaily().get(i));
                    }
                    recView_forecastWeather = findViewById(R.id.recView_forecastWeather);
                    recView_forecastWeather.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    adapter = new ForecastWeatherAdapter(MainActivity.this, list_forecastWeather);
                    recView_forecastWeather.setAdapter(adapter);

                } catch (Exception e){
                    Log.i("ERROR!!!", e.toString());
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Log.i("ERROR!!!", t.toString());
            }
        });

        editor.apply();
    }

    private void setBackground(String weath) throws IOException {
        InputStream inputStream;
        Drawable drawable;
        switch (weath) {
            case "Overcast clouds":
                inputStream = background.getContext().getAssets().open("weather_backgrounds/overcast_clouds.png");
                drawable = Drawable.createFromStream(inputStream, null);
                background.setBackground(drawable);
                imageView_sun.setAlpha(0.0f);
                break;

            case "Broken clouds":
                inputStream = background.getContext().getAssets().open("weather_backgrounds/broken_clouds.png");
                drawable = Drawable.createFromStream(inputStream, null);
                background.setBackground(drawable);
                imageView_sun.setAlpha(0.0f);
                break;

            case "Scattered clouds":
                inputStream = background.getContext().getAssets().open("weather_backgrounds/scattered_clouds.png");
                drawable = Drawable.createFromStream(inputStream, null);
                background.setBackground(drawable);
                break;

            case "Few clouds":
                inputStream = background.getContext().getAssets().open("weather_backgrounds/few_clouds.png");
                drawable = Drawable.createFromStream(inputStream, null);
                background.setBackground(drawable);
                imageView_sun.setAlpha(1f);
                break;

        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        locationListener.onLocationChanged(location);

        if(location != null) {
            //getLastCurrentWeather();
            getWeather();
            Toast.makeText(MainActivity.this, "Your location finded", Toast.LENGTH_SHORT).show();
        } else {
            //getLastCurrentWeather();
        }
    }



    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if(location != null) {
                latitude = location.getLatitude();
                longtitude = location.getLongitude();
                Log.i("GPS!!!", String.valueOf(latitude) + "; " + String.valueOf(longtitude));

                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("lat", (float) latitude);
                editor.putFloat("lon", (float) longtitude);
                editor.apply();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            //getLastCurrentWeather();

            dialog_disableGps = new Dialog(MainActivity.this);
            dialog_disableGps.setContentView(R.layout.customize_dialog_gps);
            dialog_disableGps.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Window window = dialog_disableGps.getWindow();
            window.setGravity(Gravity.CENTER);
            window.getAttributes().windowAnimations = R.style.AnimSlipeUp;

            button_yes_gps = dialog_disableGps.findViewById(R.id.button_yes_gps);
            button_no_gps = dialog_disableGps.findViewById(R.id.button_no_gps);

            button_yes_gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    getLocation();
                    dialog_disableGps.dismiss();
                }
            });

            button_no_gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_disableGps.dismiss();
                }
            });

            dialog_disableGps.show();
        }

    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
            finish();
        } else {

        }
    }

}