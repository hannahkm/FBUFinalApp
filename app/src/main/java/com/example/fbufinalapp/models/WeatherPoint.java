package com.example.fbufinalapp.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class WeatherPoint {

    @SerializedName("@id")
    @Expose
    private Object id;
    @SerializedName("@type")
    @Expose
    private Object type;
    @SerializedName("cwa")
    @Expose
    private Object cwa;
    @SerializedName("forecastOffice")
    @Expose
    private Object forecastOffice;
    @SerializedName("gridId")
    @Expose
    private Object gridId;
    @SerializedName("gridX")
    @Expose
    private int gridX;
    @SerializedName("gridY")
    @Expose
    private int gridY;
    @SerializedName("forecast")
    @Expose
    private Object forecast;
    @SerializedName("forecastHourly")
    @Expose
    private Object forecastHourly;
    @SerializedName("forecastGridData")
    @Expose
    private Object forecastGridData;
    @SerializedName("observationStations")
    @Expose
    private Object observationStations;
    @SerializedName("forecastZone")
    @Expose
    private Object forecastZone;
    @SerializedName("county")
    @Expose
    private Object county;
    @SerializedName("fireWeatherZone")
    @Expose
    private Object fireWeatherZone;
    @SerializedName("timeZone")
    @Expose
    private Object timeZone;
    @SerializedName("radarStation")
    @Expose
    private Object radarStation;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Object getCwa() {
        return cwa;
    }

    public void setCwa(Object cwa) {
        this.cwa = cwa;
    }

    public Object getForecastOffice() {
        return forecastOffice;
    }

    public void setForecastOffice(Object forecastOffice) {
        this.forecastOffice = forecastOffice;
    }

    public Object getGridId() {
        return gridId;
    }

    public void setGridId(Object gridId) {
        this.gridId = gridId;
    }

    public int getGridX() {
        return gridX;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public Object getForecast() {
        return forecast;
    }

    public void setForecast(Object forecast) {
        this.forecast = forecast;
    }

    public Object getForecastHourly() {
        return forecastHourly;
    }

    public void setForecastHourly(Object forecastHourly) {
        this.forecastHourly = forecastHourly;
    }

    public Object getForecastGridData() {
        return forecastGridData;
    }

    public void setForecastGridData(Object forecastGridData) {
        this.forecastGridData = forecastGridData;
    }

    public Object getObservationStations() {
        return observationStations;
    }

    public void setObservationStations(Object observationStations) {
        this.observationStations = observationStations;
    }

    public Object getForecastZone() {
        return forecastZone;
    }

    public void setForecastZone(Object forecastZone) {
        this.forecastZone = forecastZone;
    }

    public Object getCounty() {
        return county;
    }

    public void setCounty(Object county) {
        this.county = county;
    }

    public Object getFireWeatherZone() {
        return fireWeatherZone;
    }

    public void setFireWeatherZone(Object fireWeatherZone) {
        this.fireWeatherZone = fireWeatherZone;
    }

    public Object getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Object timeZone) {
        this.timeZone = timeZone;
    }

    public Object getRadarStation() {
        return radarStation;
    }

    public void setRadarStation(Object radarStation) {
        this.radarStation = radarStation;
    }

}