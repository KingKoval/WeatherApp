package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.POJO.Example;
import com.example.weather.POJO.Main;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;

    private static final int REQUEST_CODE = 1;

    static double latitude;

    private double longtitude;

    private TextView textView_city, textView_temp, textView_weath;
    private SwipeRefreshLayout swipe_refreshWeather;

    private AppCompatButton button_yes_gps, button_no_gps;
    private Dialog dialog_disableGps;

    SharedPreferences settings;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_city = findViewById(R.id.textView_city);
        textView_temp = findViewById(R.id.textView_temp);
        textView_weath = findViewById(R.id.textView_weath);
        swipe_refreshWeather = findViewById(R.id.swipe_refreshWeather);

        settings = this.getSharedPreferences("settings", MODE_PRIVATE);


        if(!checkPermission()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            if(!checkConnected()){
                getLastCurrentWeather();
            } else {
                getLastCurrentWeather();
                getLocation();
            }

        }

        swipe_refreshWeather.setColorSchemeColors(Color.BLUE);
        swipe_refreshWeather.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLocation();

                swipe_refreshWeather.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("city", textView_city.getText().toString());
//        editor.putString("temp", textView_temp.getText().toString());
//        editor.putString("weath", textView_weath.getText().toString());
//        editor.apply();
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

    private void getLastCurrentWeather(){
        textView_city.setText(settings.getString("city", "Not found"));
        textView_temp.setText(settings.getString("temp", "0"));
        textView_weath.setText(settings.getString("weath", "Not found"));
    }

    private void getWeathers(){
        final String API = "0fcdeed0b44f8572a682c8837f51a541";
        final String CELSIUS = "metric";
        final String LANG_RU = "ru";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CurrentWeather currentWeather = retrofit.create(CurrentWeather.class);

        Call<Example> exampleCall = currentWeather.weather(latitude, longtitude, API, CELSIUS, LANG_RU);
        exampleCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                try {
                    textView_city.setText((response.body().getName()));
                    textView_temp.setText(String.valueOf((int)(response.body().getMain().getTemp())));
                    textView_weath.setText(response.body().getWeather().get(0).getDescription().substring(0, 1).toUpperCase()
                        + response.body().getWeather().get(0).getDescription().substring(1));

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("city", textView_city.getText().toString());
                    editor.putString("temp", textView_temp.getText().toString());
                    editor.putString("weath", textView_weath.getText().toString());
                    editor.apply();

                } catch (Exception e){
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

            }
        });


    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        locationListener.onLocationChanged(location);

        if(location != null) {
            getWeathers();
        } else {
            getLastCurrentWeather();
        }
    }



    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if(location != null) {
                latitude = location.getLatitude();
                longtitude = location.getLongitude();

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
            getLastCurrentWeather();

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
                }
            });

            button_no_gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_disableGps.dismiss();
                }
            });

            dialog_disableGps.show();
//            AlertDialog.Builder builder = new Aler
//            tDialog.Builder(MainActivity.this);
//            builder.setIcon(R.drawable.gps_disable);
//            builder.setTitle("GPS disable!");
//            builder.setMessage("Do you want enable GPS?");
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //start settings window when user can enable GPS
//                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                }
//            });
//            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //close AlertDialog
//                    dialog.cancel();
//                }
//            });
//
//            AlertDialog enableGps = builder.create();
//            enableGps.show();
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