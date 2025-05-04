package com.example.foodplanner.model.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryResponse {
    @SerializedName("meals")
private List<Country> countries;
public List<Country> getCountries() {
    return countries;
}
}
