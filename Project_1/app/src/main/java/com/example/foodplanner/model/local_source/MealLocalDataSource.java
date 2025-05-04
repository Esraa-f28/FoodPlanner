package com.example.foodplanner.model.local_source;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;

import java.util.List;

public class MealLocalDataSource implements IMealLocalDataSource {

    private final MealDAO mealDAO;
    private final SchedMealDAO schedMealDAO;
    private LiveData<List<ScheduleMeal>> scheduledMeals;

    private static MealLocalDataSource instance = null;

    private MealLocalDataSource(Context context) {
        MealDatabase db = MealDatabase.getInstance(context);
        this.mealDAO = db.mealDAO();
        this.schedMealDAO = db.schedMealDAO();
        this.scheduledMeals = schedMealDAO.getScheduledMeals();
    }

    public static MealLocalDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new MealLocalDataSource(context);
        }
        return instance;
    }

    @Override
    public void insertMeal(Meal meal) {
        new Thread(() -> mealDAO.insertMeal(meal)).start();
    }

    @Override
    public void deleteMeal(Meal meal) {
        new Thread(() -> mealDAO.deleteMeal(meal)).start();
    }

    @Override
    public LiveData<List<Meal>> getAllSavedMeals() {
        return mealDAO.getAllMeals();
    }

    @Override
    public LiveData<Meal> getMealById(String mealId) {
        return mealDAO.getMealById(mealId);
    }

    @Override
    public LiveData<List<ScheduleMeal>> getAllSchedMeals() {
        return schedMealDAO.getScheduledMeals();
    }

    @Override
    public void insertSchedMeal(ScheduleMeal meal) {
        new Thread(() -> schedMealDAO.insertScheduledMeal(meal)).start();
    }

    @Override
    public void deleteSchedMeal(ScheduleMeal meal) {
        new Thread(() -> schedMealDAO.deleteScheduledMeal(meal)).start();
    }

    @Override
    public LiveData<ScheduleMeal> getSchedMealById(String id) {
        return schedMealDAO.getScheduledMealById(id);
    }

    @Override
    public LiveData<List<ScheduleMeal>> getMealsForDate(String date) {
        return schedMealDAO.getMealsForDate(date);
    }
}