package com.example.foodplanner.model.remote_source.meal;

import com.example.foodplanner.model.pojo.Meal;

import java.util.List;

public interface MealNetworkCallback {
    void onMealSuccess(List<Meal> meals);
    void onMealFailure(String errorMessage);
}
