package com.example.foodplanner.model.remote_source.country;

import com.example.foodplanner.model.pojo.Country;

import java.util.List;

public interface CountryNetworkCallback {
    void onCountrySuccess(List<Country> countries);
    void onCountryFailure(String errorMessage);
}
