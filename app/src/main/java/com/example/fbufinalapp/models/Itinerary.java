package com.example.fbufinalapp.models;

import com.example.fbufinalapp.CommonValues;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ParseObject class to access itinerary data from Parse backend. Parse models initialized in
 * ParseApplication.java.
 */
@ParseClassName("Itinerary")
public class Itinerary extends ParseObject {
    public String getTitle() {
        return getString(CommonValues.KEY_TITLE);
    }

    public void setTitle(String title){
        put(CommonValues.KEY_TITLE, title);
    }

    public String getDescription() {
        return getString(CommonValues.KEY_DESCRIPTION_ITINERARY);
    }

    public void setDescription(String descrip){
        put(CommonValues.KEY_DESCRIPTION_ITINERARY, descrip);
    }

    public List<String> getAuthor() {
        return getList(CommonValues.KEY_USER);
    }

    public void setAuthor(List<String> authors){
        put(CommonValues.KEY_USER, authors);
    }

    public String getPlaceID() {
        return getString(CommonValues.KEY_PLACEID);
    }

    public void setPlaceID(String id){
        put(CommonValues.KEY_PLACEID, id);
    }

    public Date getStartDate(){
        return getDate(CommonValues.KEY_STARTDATE);
    }

    public void setStartDate(Date start){
        put(CommonValues.KEY_STARTDATE, start);
    }

    public Date getEndDate(){
        return getDate(CommonValues.KEY_ENDDATE);
    }

    public void setEndDate(Date end){
        put(CommonValues.KEY_ENDDATE, end);
    }

    public List<String> getDestinations() {
        return getList(CommonValues.KEY_DESTINATIONS);
    }

    public void setDestinations(List<String> destinations) { put(CommonValues.KEY_DESTINATIONS, destinations);}

    public String reformatDate(Date date){
        String pattern = "MMMM dd, yyyy";

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }
}
