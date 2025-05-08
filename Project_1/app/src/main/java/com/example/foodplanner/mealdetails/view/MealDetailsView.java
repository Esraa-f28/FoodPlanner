package com.example.foodplanner.mealdetails.view;

import com.example.foodplanner.model.pojo.Meal;

public interface MealDetailsView {
    void showMealDetails(Meal meal);
    void setFavorite(boolean favoriteStatus);
    void showError(String errorMessage);
    void showLoading();
    void hideLoading();
    void showIngredientsList();

    void setScheduled(boolean ScheduledStatus);

}