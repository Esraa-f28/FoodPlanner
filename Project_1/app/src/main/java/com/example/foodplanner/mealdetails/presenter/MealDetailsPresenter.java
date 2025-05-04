package com.example.foodplanner.mealdetails.presenter;

import androidx.lifecycle.LifecycleOwner;

import com.example.foodplanner.model.pojo.Meal;

public interface MealDetailsPresenter {
    void getMealDetails(String mealId);
    void addToFavorites(Meal meal);
    void removeFromFavorites(Meal meal);
    void checkIfFavorite(String mealId);
    void onDestroy();
    void setLifecycleOwner(LifecycleOwner lifecycleOwner);
}