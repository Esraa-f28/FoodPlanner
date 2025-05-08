package com.example.foodplanner.favproducts.presenter;


import androidx.lifecycle.LiveData;

import com.example.foodplanner.model.pojo.Meal;

import java.util.List;

public interface IFavPresenter {
    LiveData<List<Meal>> getLocalData();
    LiveData<List<Meal>> getFavorites(String userId);

    void removeFromFavorites(Meal meal);
}
