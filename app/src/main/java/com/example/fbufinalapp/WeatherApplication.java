package com.example.fbufinalapp;

import android.app.Application;
import android.util.Log;

import com.example.fbufinalapp.models.AccuweatherForecast;
import com.example.fbufinalapp.models.AccuweatherLocation;
import com.example.fbufinalapp.models.WeatherForecast;
import com.example.fbufinalapp.models.WeatherForecastWrapper;
import com.example.fbufinalapp.models.WeatherPoint;
import com.example.fbufinalapp.models.WeatherPointWrapper;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class WeatherApplication extends Application {
    private static Retrofit retrofit;
    public static String BASE_URL = "https://api.weather.gov/";
    public static String BASE_URL_ACCUWEATHER = "https://dataservice.accuweather.com/";
    public static LinkedTreeMap<String, Object> todayForecast;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL_ACCUWEATHER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface getWeatherData{
        @GET("points/{latitude},{longitude}")
        Call<WeatherPointWrapper> getWeather(@Path("latitude") double lat, @Path("longitude") double lon);

        @GET("gridpoints/{office}/{gridX},{gridY}/forecast")
        Call<WeatherForecastWrapper> getForecast(@Path("office") String gridId, @Path("gridX") int gridX, @Path("gridY") int gridY);

        @GET("locations/v1/cities/geoposition/search")
        Call<AccuweatherLocation> getLocation(@Query("apikey") String apikey, @Query("q") String latlng);

        @GET("forecasts/v1/daily/1day/{locationKey}")
        Call<AccuweatherForecast> getForecast(@Path("locationKey") String locationKey, @Query("apikey") String apiKey);

    }


}
