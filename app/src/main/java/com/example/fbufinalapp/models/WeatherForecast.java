package com.example.fbufinalapp.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class WeatherForecast {

    @SerializedName("updated")
    @Expose
    private Object updated;
    @SerializedName("units")
    @Expose
    private Object units;
    @SerializedName("forecastGenerator")
    @Expose
    private Object forecastGenerator;
    @SerializedName("generatedAt")
    @Expose
    private Object generatedAt;
    @SerializedName("updateTime")
    @Expose
    private Object updateTime;
    @SerializedName("validTimes")
    @Expose
    private Object validTimes;
    @SerializedName("elevation")
    @Expose
    private Object elevation;
    @SerializedName("periods")
    @Expose
    private Object periods;

    public Object getUpdated() {
        return updated;
    }

    public void setUpdated(Object updated) {
        this.updated = updated;
    }

    public Object getUnits() {
        return units;
    }

    public void setUnits(Object units) {
        this.units = units;
    }

    public Object getForecastGenerator() {
        return forecastGenerator;
    }

    public void setForecastGenerator(Object forecastGenerator) {
        this.forecastGenerator = forecastGenerator;
    }

    public Object getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Object generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Object getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Object updateTime) {
        this.updateTime = updateTime;
    }

    public Object getValidTimes() {
        return validTimes;
    }

    public void setValidTimes(Object validTimes) {
        this.validTimes = validTimes;
    }

    public Object getElevation() {
        return elevation;
    }

    public void setElevation(Object elevation) {
        this.elevation = elevation;
    }

    public Object getPeriods() {
        return periods;
    }

    public void setPeriods(Object periods) {
        this.periods = periods;
    }

}