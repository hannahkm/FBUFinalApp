package com.example.fbufinalapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

//@Parcel(analyze = Itinerary.class)
@ParseClassName("Itinerary")
public class Itinerary extends ParseObject {
    public static String KEY_DESCRIPTION = "description";
    public static String KEY_TITLE = "title";
    public static String KEY_USER = "authors";
    public static String KEY_PLACEID = "placeID";
    public static String KEY_STARTDATE = "startDate";
    public static String KEY_ENDDATE = "endDate";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title){
        put(KEY_TITLE, title);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String descrip){
        put(KEY_DESCRIPTION, descrip);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_USER);
    }

    public void setAuthor(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }

    public String getPlaceID() {
        return getString(KEY_PLACEID);
    }

    public void setPlaceID(String id){
        put(KEY_PLACEID, id);
    }

    public Date getStartDate(){
        return getDate(KEY_STARTDATE);
    }

    public void setStartDate(Date start){
        put(KEY_STARTDATE, start);
    }

    public Date getEndDate(){
        return getDate(KEY_ENDDATE);
    }

    public void setEndDate(Date end){
        put(KEY_ENDDATE, end);
    }

    public String reformatDate(Date date){
        String pattern = "MMMM dd, yyyy";

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }
}
