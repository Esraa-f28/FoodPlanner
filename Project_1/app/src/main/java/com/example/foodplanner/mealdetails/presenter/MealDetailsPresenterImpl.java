package com.example.foodplanner.mealdetails.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.foodplanner.mealdetails.view.MealDetailsView;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.repositry.Repository;

public class MealDetailsPresenterImpl implements MealDetailsPresenter {
    private static final String TAG = "MealDetailsPresenter";
    private final MealDetailsView view;
    private final Repository repository;
    private final Context context;
    private LiveData<Meal> favoriteMealLiveData;
    private LifecycleOwner lifecycleOwner;

    public MealDetailsPresenterImpl(MealDetailsView view, Repository repository, Context context) {
        this.view = view;
        this.repository = repository;
        this.context = context;
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
        Log.d(TAG, "Adding to favorites: " + meal.getStrMeal());
        repository.insertMeal(meal);
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        Log.d(TAG, "Removing from favorites: " + meal.getStrMeal());
        repository.deleteMeal(meal);
    }

    @Override
    public void checkIfFavorite(String mealId) {
        Log.d(TAG, "Checking if favorite: " + mealId);
        if (favoriteMealLiveData != null) {
            favoriteMealLiveData.removeObservers(lifecycleOwner);
        }
        favoriteMealLiveData = repository.getMealById(mealId);
        favoriteMealLiveData.observe(lifecycleOwner, meal -> {
            Log.d(TAG, "Favorite status: " + (meal != null));
            view.setFavorite(meal != null);
        });
    }

    @Override
    public void onDestroy() {
        if (favoriteMealLiveData != null && lifecycleOwner != null) {
            favoriteMealLiveData.removeObservers(lifecycleOwner);
        }
    }
}