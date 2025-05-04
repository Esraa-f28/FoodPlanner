package com.example.foodplanner.model.pojo;

import com.google.gson.annotations.SerializedName;

public class Country {
    @SerializedName("strArea")
    private String strArea;

    private String strAreaThumb;

    public String getStrArea() {
        return strArea;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }

    public String getStrAreaThumb() {
        return strAreaThumb != null ? strAreaThumb : "https://via.placeholder.com/48";
    }

    public void setStrAreaThumb(String strAreaThumb) {
        this.strAreaThumb = strAreaThumb;
    }
}