package com.example.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.pojo.forecast.Dayli;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ForecastWeatherAdapter extends RecyclerView.Adapter<ForecastWeatherAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Dayli> days;
    Context context;
    private AssetManager as;

    private SharedPreferences sp_forecastWeather;

    public ForecastWeatherAdapter(Context context, ArrayList<Dayli> days){
        this.days = days;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ForecastWeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item_forecast_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastWeatherAdapter.ViewHolder holder, int position) {
        Dayli dayli = days.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        String day = dateFormat.format(new Date((long)dayli.getDt() * 1000)).substring(0, 1).toUpperCase()
                + dateFormat.format(new Date((long)dayli.getDt() * 1000)).substring(1);
        String min_temp = String.valueOf((int)dayli.getTemp().getMax()) + "°";
        String max_temp = String.valueOf((int)dayli.getTemp().getMin()) + "°";
        String statWeath = dayli.getWeather().get(0).getDescription().substring(0, 1).toUpperCase()
                + dayli.getWeather().get(0).getDescription().substring(1);

        holder.textView_nextDay.setText(day);
        holder.textView_max.setText(min_temp);
        holder.textView_min.setText(max_temp);
        holder.textView_statWeath.setText(statWeath);


        try{
            InputStream inputStream = holder.imageView_weatherIcon.getContext().getAssets().open("weather_icons/" + dayli.getWeather().get(0).getIcon() + ".png");
            Drawable d = Drawable.createFromStream(inputStream, null);
            holder.imageView_weatherIcon.setImageDrawable(d);
        } catch (IOException ex) {

        }


    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView_nextDay, textView_min, textView_max, textView_statWeath;
        ImageView imageView_weatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_nextDay = itemView.findViewById(R.id.textView_nextDay);
            textView_min = itemView.findViewById(R.id.textView_min);
            textView_max = itemView.findViewById(R.id.textView_max);
            textView_statWeath = itemView.findViewById(R.id.textView_statWeath);
            imageView_weatherIcon = itemView.findViewById(R.id.imageView_weatherIcon);

        }
    }

}
