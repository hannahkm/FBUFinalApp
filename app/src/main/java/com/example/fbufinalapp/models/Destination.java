package com.example.fbufinalapp.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.fbufinalapp.CommonValues;
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
    public String getName() {
        return getString(CommonValues.KEY_NAME);
    }

    public void setName(String name){
        put(CommonValues.KEY_NAME, name);
    }

    public String getDescription() {
        return getString(CommonValues.KEY_DESCRIPTION_DESTINATION);
    }

    public void setDescription(String notes){
        put(CommonValues.KEY_DESCRIPTION_DESTINATION, notes);
    }

    public String getPlaceID() {
        return getString(CommonValues.KEY_PLACEID);
    }

    public void setPlaceID(String id){
        put(CommonValues.KEY_PLACEID, id);
    }

    public Date getTime(){
        return getDate(CommonValues.KEY_TIME);
    }

    public void setTime(Date time){
        put(CommonValues.KEY_TIME, time);
    }

    public boolean getIsDay(){
        return getBoolean(CommonValues.KEY_ISDAY);
    }

    public void setIsDay(Boolean day){
        put(CommonValues.KEY_ISDAY, day);
    }

    public Date getDate() { return getDate(CommonValues.KEY_DATE); }

    public void setDate(Date date) { put(CommonValues.KEY_DATE, date); }

    public ParseObject getItinerary() { return getParseObject(CommonValues.KEY_ITINERARY_DESTINATION); }

    public void setItinerary(Itinerary itin) { put(CommonValues.KEY_ITINERARY_DESTINATION, itin); }

    public String reformatTime(){
        Date time = getDate();
        String pattern = "hh:mm a";
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat(pattern);

        return simpleDateFormat.format(time);
    }

}
