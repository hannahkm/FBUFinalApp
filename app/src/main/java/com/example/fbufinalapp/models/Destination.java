package com.example.fbufinalapp.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ParseObject class to access destination data from Parse backend. Parse models initialized in
 * ParseApplication.java.
 */
@ParseClassName("Destination")
public class Destination extends ParseObject{
    public static String KEY_DESCRIPTION = "notes";
    public static String KEY_NAME = "name";
    public static String KEY_PLACEID = "placeID";
    public static String KEY_TIME = "time";
    public static String KEY_ISDAY = "isDay";
    public static String KEY_DATE = "date";
    public static String KEY_ITINERARY = "itinerary";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name){
        put(KEY_NAME, name);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String notes){
        put(KEY_DESCRIPTION, notes);
    }

    public String getPlaceID() {
        return getString(KEY_PLACEID);
    }

    public void setPlaceID(String id){
        put(KEY_PLACEID, id);
    }

    public Date getTime(){
        return getDate(KEY_TIME);
    }

    public void setTime(Date time){
        put(KEY_TIME, time);
    }

    public boolean getIsDay(){
        return getBoolean(KEY_ISDAY);
    }

    public void setIsDay(Boolean day){
        put(KEY_ISDAY, day);
    }

    public Date getDate() { return getDate(KEY_DATE); }

    public void setDate(Date date) { put(KEY_DATE, date); }

    public ParseObject getItinerary() { return getParseObject(KEY_ITINERARY); }

    public void setItinerary(Itinerary itin) { put(KEY_ITINERARY, itin); }

    public String reformatTime(Date time){
        String pattern = "hh:mm a";
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern);

        return simpleDateFormat.format(time);
    }

}
