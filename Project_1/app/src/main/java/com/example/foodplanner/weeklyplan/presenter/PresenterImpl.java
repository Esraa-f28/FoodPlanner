package com.example.foodplanner.weeklyplan.presenter;

import com.example.foodplanner.model.pojo.ScheduleMeal;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.weeklyplan.view.IView;

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
}