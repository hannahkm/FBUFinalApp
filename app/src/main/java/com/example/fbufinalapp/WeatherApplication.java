package com.example.fbufinalapp;

import android.app.Application;

import com.example.fbufinalapp.models.AccuweatherForecast;
import com.example.fbufinalapp.models.AccuweatherLocation;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class WeatherApplication extends Application {
    private static Retrofit retrofit;
    public static String BASE_URL_ACCUWEATHER = "https://dataservice.accuweather.com/";

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
        @GET("locations/v1/cities/geoposition/search")
        Call<AccuweatherLocation> getLocation(@Query("apikey") String apikey, @Query("q") String latlng);

        @GET("forecasts/v1/daily/1day/{locationKey}")
        Call<AccuweatherForecast> getForecast(@Path("locationKey") String locationKey, @Query("apikey") String apiKey);

    }


}
