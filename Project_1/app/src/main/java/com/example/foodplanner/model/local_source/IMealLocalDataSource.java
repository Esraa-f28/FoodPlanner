package com.example.foodplanner.model.local_source;

import androidx.lifecycle.LiveData;

import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;

import java.util.List;

public interface IMealLocalDataSource {
    void insertMeal(Meal meal);
    void deleteMeal(Meal meal);

    LiveData<List<Meal>> getAllSavedMeals();

    LiveData<Meal> getMealById(String mealId);

    LiveData<List<ScheduleMeal>> getAllSchedMeals();
    void insertSchedMeal(ScheduleMeal meal);
    void deleteSchedMeal(ScheduleMeal meal);

    LiveData<ScheduleMeal> getSchedMealById(String id);

    LiveData<List<ScheduleMeal>> getMealsForDate(String date);

}
