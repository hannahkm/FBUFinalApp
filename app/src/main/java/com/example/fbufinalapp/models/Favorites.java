package com.example.fbufinalapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Favorites")
public class Favorites extends ParseObject {
    public static String KEY_PLACEID = "placeID";
    public static String KEY_NOTES = "notes";
    public static String KEY_USER = "user";

    public String getPlaceId(){
        return getString(KEY_PLACEID);
    }

    public void setPlaceId(String id){
        put(KEY_PLACEID, id);
    }

    public String getNotes(){
        return getString(KEY_NOTES);
    }

    public void setNotes(String notes){
        put(KEY_NOTES, notes);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }
}
