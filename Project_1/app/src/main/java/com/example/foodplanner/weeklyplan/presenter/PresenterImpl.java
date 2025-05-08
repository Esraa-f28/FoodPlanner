package com.example.foodplanner.weeklyplan.presenter;

import com.example.foodplanner.model.pojo.ScheduleMeal;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.weeklyplan.view.IView;

import java.util.List;
import java.util.stream.Collectors;

public class PresenterImpl implements Presenter {
    private IView view;
    private Repository repository;

    public PresenterImpl(IView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getMealsForDate(String date) {
        repository.getMealsForDate(date).observe(view.getLifecycleOwner(), meals -> {
            view.showMeals(meals);
        });
    }

    @Override
    public void deleteScheduledMeal(ScheduleMeal meal) {
        repository.deleteScheduledMeal(meal);
    }
    @Override
    public void getMealsForUserAndDate(String userId, String date) {
        repository.getScheduledMealsByUserId(userId).observe(view.getLifecycleOwner(), meals -> {
            List<ScheduleMeal> filteredMeals = meals != null ?
                    meals.stream()
                            .filter(meal -> meal.getScheduledDate() != null && meal.getScheduledDate().equals(date))
                            .collect(Collectors.toList()) :
                    new java.util.ArrayList<>();
            view.showMeals(filteredMeals);
        });
    }
}