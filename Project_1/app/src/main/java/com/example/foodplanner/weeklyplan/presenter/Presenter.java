package com.example.foodplanner.weeklyplan.presenter;

import com.example.foodplanner.model.pojo.ScheduleMeal;

public interface Presenter {
    void getMealsForDate(String date);
    void deleteScheduledMeal(ScheduleMeal meal);
    public void getMealsForUserAndDate(String userId, String date) ;

    }