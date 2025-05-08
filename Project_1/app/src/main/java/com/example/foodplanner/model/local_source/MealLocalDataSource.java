package com.example.foodplanner.model.local_source;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;

import java.util.List;

public class MealLocalDataSource implements IMealLocalDataSource {
    private static final String TAG = "MealLocalDataSource";
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
        if (meal == null || meal.getUserId() == null) {
            Log.e(TAG, "Cannot insert meal: meal or userId is null");
            return;
        }
        new Thread(() -> {
            Log.d(TAG, "Inserting meal: " + meal.getStrMeal() + " with userId: " + meal.getUserId() + ", idMeal: " + meal.getIdMeal());
            mealDAO.insertMeal(meal);
            Log.d(TAG, "Insertion completed for meal: " + meal.getStrMeal());
            // Synchronous check
            Meal insertedMeal = mealDAO.getMealByIdSync(meal.getIdMeal());
            if (insertedMeal != null) {
                Log.d(TAG, "Post-insertion check: meal " + insertedMeal.getStrMeal() + " has userId: " + insertedMeal.getUserId());
            } else {
                Log.w(TAG, "Post-insertion check: meal not found with idMeal: " + meal.getIdMeal());
            }
        }).start();
    }

    @Override
    public void deleteMeal(Meal meal) {
        new Thread(() -> {
            Log.d(TAG, "Deleting meal: " + meal.getStrMeal() + " with userId: " + meal.getUserId());
            mealDAO.deleteMeal(meal);
        }).start();
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
        new Thread(() -> {
            Log.d(TAG, "Inserting scheduled meal: " + meal.getStrMeal() + " with userId: " + meal.getUserId());
            schedMealDAO.insertScheduledMeal(meal);
        }).start();
    }

    @Override
    public void deleteSchedMeal(ScheduleMeal meal) {
        new Thread(() -> {
            Log.d(TAG, "Deleting scheduled meal: " + meal.getStrMeal() + " with userId: " + meal.getUserId());
            schedMealDAO.deleteScheduledMeal(meal);
        }).start();
    }

    @Override
    public LiveData<ScheduleMeal> getSchedMealById(String id) {
        return schedMealDAO.getScheduledMealById(id);
    }

    @Override
    public LiveData<Meal> getMealByIdAndUserId(String mealId, String userId) {
        Log.d(TAG, "Fetching meal with mealId: " + mealId + " and userId: " + userId);
        return mealDAO.getMealByIdAndUserId(mealId, userId);
    }

    @Override
    public LiveData<List<ScheduleMeal>> getMealsForDate(String date) {
        return schedMealDAO.getMealsForDate(date);
    }

    @Override
    public LiveData<List<Meal>> getMealsByUserId(String userId) {
        Log.d(TAG, "Fetching meals for userId: " + userId);
        LiveData<List<Meal>> meals = mealDAO.getMealsByUserId(userId);
        meals.observeForever(mealsList -> Log.d(TAG, "Retrieved meals count: " + (mealsList != null ? mealsList.size() : "null")));
        return meals;
    }

    @Override
    public LiveData<List<ScheduleMeal>> getScheduledMealsByUserId(String userId) {
        Log.d(TAG, "Fetching scheduled meals for userId: " + userId);
        return schedMealDAO.getScheduledMealsByUserId(userId);
    }

    @Override
    public LiveData<ScheduleMeal> getScheduleMealByIdAndUserId(String mealId, String userId) {
        Log.d(TAG, "Fetching meal with mealId: " + mealId + " and userId: " + userId);
        return schedMealDAO.getScheduleMealByIdAndUserId(mealId, userId);
    }
}