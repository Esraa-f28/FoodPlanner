package com.example.foodplanner.mealdetails.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.foodplanner.mealdetails.view.MealDetailsView;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.utils.UserSessionManager;

public class MealDetailsPresenterImpl implements MealDetailsPresenter {
    private static final String TAG = "MealDetailsPresenter";
    private final MealDetailsView view;
    private final Repository repository;
    private final Context context;
    private LiveData<Meal> favoriteMealLiveData;
    private LiveData<ScheduleMeal> scheduledMealLiveData;
    private LifecycleOwner lifecycleOwner;
    private final UserSessionManager sessionManager;

    public MealDetailsPresenterImpl(MealDetailsView view, Repository repository, Context context) {
        this.view = view;
        this.repository = repository;
        this.context = context;
        this.sessionManager = new UserSessionManager(context);
    }

    @Override
    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void getMealDetails(String mealId) {
        Log.d(TAG, "Fetching meal details for id: " + mealId);
        view.showLoading();
        LiveData<Meal> mealLiveData = repository.getMealById(mealId);
        mealLiveData.observe(lifecycleOwner, meal -> {
            view.hideLoading();
            if (meal != null) {
                Log.d(TAG, "Meal fetch success: " + meal.getStrMeal());
                view.showMealDetails(meal);
                view.showIngredientsList();
                checkIfFavorite(meal.getIdMeal());
                checkIfScheduled(meal.getIdMeal());
            } else {
                Log.e(TAG, "Meal fetch failed: Meal is null");
                if (!isOnline()) {
                    view.showError("You're offline. Please view this meal online first to cache its details.");
                } else {
                    view.showError("Failed to load meal details");
                }
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void addToFavorites(Meal meal) {
        Log.d(TAG, "Original meal userId: " + meal.getUserId());
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Cannot add to favorites: userId is null or empty");
            view.showError("Please log in to add to favorites");
            return;
        }
        meal.setUserId(userId);
        Log.d(TAG, "Adding to favorites: " + meal.getStrMeal() + " with userId: " + meal.getUserId());
        repository.insertMeal(meal);
        Log.d(TAG, "Inserted meal with userId: " + userId);
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        Log.d(TAG, "Removing from favorites: " + meal.getStrMeal());
        String userId = sessionManager.getUserId();
        meal.setUserId(userId);
        repository.deleteMeal(meal);
    }

    @Override
    public void checkIfFavorite(String mealId) {
        Log.d(TAG, "Checking if favorite: " + mealId);
        if (favoriteMealLiveData != null) {
            favoriteMealLiveData.removeObservers(lifecycleOwner);
        }
        String userId = sessionManager.getUserId();
        favoriteMealLiveData = repository.getFavoriteByIdAndUserId(mealId, userId);
        favoriteMealLiveData.observe(lifecycleOwner, meal -> {
            Log.d(TAG, "Favorite status: " + (meal != null));
            view.setFavorite(meal != null);
        });
    }

    @Override
    public void addToSchedule(ScheduleMeal scheduleMeal, Meal meal) {
        Log.d(TAG, "Adding to schedule: " + scheduleMeal.getStrMeal() + " with userId: " + scheduleMeal.getUserId());
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Cannot insert meal: userId is null or empty");
            view.showError("Please log in to schedule a meal");
            return;
        }
        if (meal == null) {
            Log.e(TAG, "Cannot insert meal: meal is null");
            view.showError("Cannot schedule meal");
            return;
        }
        if (scheduleMeal.getUserId() == null) {
            scheduleMeal.setUserId(userId);
            Log.d(TAG, "Set userId on ScheduleMeal: " + userId);
        }
        meal.setUserId(userId);
        repository.insertScheduledMeal(scheduleMeal);
//        repository.insertMeal(meal);
        Log.d(TAG, "Inserted scheduled meal and meal with userId: " + userId);
    }

    @Override
    public void removeFromSchedule(String mealId) {
        Log.d(TAG, "Removing from schedule: " + mealId);
        String userId = sessionManager.getUserId();
        repository.getScheduledMealById(mealId).observe(lifecycleOwner, scheduleMeal -> {
            if (scheduleMeal != null && userId != null && userId.equals(scheduleMeal.getUserId())) {
                repository.deleteScheduledMeal(scheduleMeal);
                Log.d(TAG, "Scheduled meal removed: " + scheduleMeal.getStrMeal());
            }
        });
    }

    @Override
    public void checkIfScheduled(String mealId) {
        Log.d(TAG, "Checking if scheduled: " + mealId);
        if (scheduledMealLiveData != null) {
            scheduledMealLiveData.removeObservers(lifecycleOwner);
        }
        String userId = sessionManager.getUserId();
        scheduledMealLiveData = repository.getScheduleMealByIdAndUserId(mealId,userId);
        scheduledMealLiveData.observe(lifecycleOwner, scheduleMeal -> {
            boolean isScheduled = scheduleMeal != null && scheduleMeal.getUserId() != null && scheduleMeal.getUserId().equals(userId);
            Log.d(TAG, "Scheduled status: " + isScheduled + ", scheduleMeal: " + (scheduleMeal != null ? scheduleMeal.getStrMeal() : "null") + ", scheduleMeal.userId: " + (scheduleMeal != null ? scheduleMeal.getUserId() : "null"));
            view.setScheduled(isScheduled);
        });
    }

    @Override
    public void onDestroy() {
        if (favoriteMealLiveData != null && lifecycleOwner != null) {
            favoriteMealLiveData.removeObservers(lifecycleOwner);
        }
        if (scheduledMealLiveData != null && lifecycleOwner != null) {
            scheduledMealLiveData.removeObservers(lifecycleOwner);
        }
    }
}