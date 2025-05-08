package com.example.foodplanner.mealdetails.presenter;

import androidx.lifecycle.LifecycleOwner;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;

public interface MealDetailsPresenter {
    void setLifecycleOwner(LifecycleOwner lifecycleOwner);
    void getMealDetails(String mealId);
    void addToFavorites(Meal meal);
    void removeFromFavorites(Meal meal);
    void checkIfFavorite(String mealId);
    void addToSchedule(ScheduleMeal scheduleMeal, Meal meal);
    void removeFromSchedule(String mealId);
    void checkIfScheduled(String mealId);
    void onDestroy();
}