package com.example.foodplanner.favproducts.presenter;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.foodplanner.favproducts.view.IFavView;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.repositry.Repository;

import java.util.List;

public class FavPresenter implements IFavPresenter {
    private IFavView view;
    private Repository repository;
    private String userId;

    public FavPresenter(IFavView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public LiveData<List<Meal>> getLocalData() {
        if (userId != null) {
            return repository.getAllSavedMeals(userId);
        }
        return repository.getAllSavedMeals();
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        // Add validation for userId
        if (userId == null) {
            if (view != null) {
                view.showError("User not authenticated");
            }
            return;
        }

        // Ensure the meal has the correct userId before deletion
        if (meal.getUserId() == null || !meal.getUserId().equals(userId)) {
            meal.setUserId(userId); // Set the userId if missing or incorrect
        }

        repository.deleteMeal(meal);
        if (view != null) {
            view.onMealRemoved(meal);
        }
    }

    @Override
    public LiveData<List<Meal>> getFavorites(String userId) {
        this.userId = userId;
        Log.d("FavPresenter", "Fetching favorites for userId: " + userId);
        LiveData<List<Meal>> favorites = repository.getAllSavedMeals(userId);
        favorites.observeForever(meals -> {
            Log.d("FavPresenter", "Retrieved " + (meals != null ? meals.size() : 0) + " favorite meals");
        });
        return favorites;
    }
}