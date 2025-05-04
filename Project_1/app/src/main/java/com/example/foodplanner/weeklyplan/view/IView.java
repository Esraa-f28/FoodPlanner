package com.example.foodplanner.weeklyplan.view;

import androidx.lifecycle.LifecycleOwner;

import com.example.foodplanner.model.pojo.ScheduleMeal;

import java.util.List;

public interface IView {
    void showMeals(List<ScheduleMeal> meals);
    LifecycleOwner getLifecycleOwner();
}