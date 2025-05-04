package com.example.foodplanner.homepage.presenter;

import android.util.Log;

import com.example.foodplanner.homepage.view.HomeView;
import com.example.foodplanner.model.pojo.Category;
import com.example.foodplanner.model.pojo.Country;
import com.example.foodplanner.model.pojo.Ingredients;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.remote_source.category.CategoryNetworkCallback;
import com.example.foodplanner.model.remote_source.country.CountryNetworkCallback;
import com.example.foodplanner.model.remote_source.ingredient.IngredientNetworkCallback;
import com.example.foodplanner.model.remote_source.meal.MealNetworkCallback;
import com.example.foodplanner.model.repositry.Repository;

import java.util.List;

public class HomePresenterImpl implements IHomePresenter, MealNetworkCallback,
        CategoryNetworkCallback, CountryNetworkCallback, IngredientNetworkCallback {

    private final HomeView view;
    private final Repository repository;
    private boolean isRandomMealRequest; // Tracks if the current request is for a random meal
    private static final String TAG = "HomePresenterImpl";

    public HomePresenterImpl(HomeView view, Repository repository) {
        this.view = view;
        this.repository = repository;
        this.isRandomMealRequest = false;
    }

    @Override
    public void getRandomMeal() {
        isRandomMealRequest = true;
        repository.getRandomMeal(this);
    }

    @Override
    public void getCategories() {
        repository.getCategories(this);
    }

    @Override
    public void getCountries() {
        repository.getCountries(this);
    }

    @Override
    public void getIngredients() {
        repository.getIngredients(this);
    }

    @Override
    public void getMealsByCategory(String category) {
        isRandomMealRequest = false;
        repository.getMealsByCategory(category, this);
    }

    @Override
    public void getMealsByCountry(String country) {
        isRandomMealRequest = false;
        repository.getMealsByCountry(country, this);
    }

    @Override
    public void getMealsByIngredient(String ingredient) {
        isRandomMealRequest = false;
        repository.getMealsByIngredient(ingredient, this);
    }

    @Override
    public void onMealSuccess(List<Meal> meals) {
        Log.d(TAG, "Meal fetch success: count=" + (meals != null ? meals.size() : "null") + ", isRandom=" + isRandomMealRequest);
        if (meals != null && !meals.isEmpty()) {
            if (isRandomMealRequest && meals.size() == 1) {
                view.showRandomMeal(meals.get(0));
            } else {
                view.showMealsByFilter(meals);
            }
        } else {
            view.showError("No meals received");
        }
        isRandomMealRequest = false; // Reset after handling
    }

    @Override
    public void onCategorySuccess(List<Category> categories) {
        Log.d(TAG, "Category fetch success: count=" + (categories != null ? categories.size() : "null"));
        view.showCategories(categories);
    }

    @Override
    public void onCountrySuccess(List<Country> countries) {
        Log.d(TAG, "Country fetch success: count=" + (countries != null ? countries.size() : "null"));
        view.showCountries(countries);
    }

    @Override
    public void onIngredientSuccess(List<Ingredients> ingredients) {
        Log.d(TAG, "Ingredient fetch success: count=" + (ingredients != null ? ingredients.size() : "null"));
        view.showIngredients(ingredients);
    }

    @Override
    public void onMealFailure(String errorMessage) {
        Log.e(TAG, "Meal fetch failed: " + errorMessage);
        view.showError(errorMessage);
        isRandomMealRequest = false; // Reset on failure
    }

    @Override
    public void onCategoryFailure(String errorMessage) {
        Log.e(TAG, "Category fetch failed: " + errorMessage);
        view.showError(errorMessage);
    }

    @Override
    public void onCountryFailure(String errorMessage) {
        Log.e(TAG, "Country fetch failed: " + errorMessage);
        view.showError(errorMessage);
    }

    @Override
    public void onIngredientFailure(String errorMessage) {
        Log.e(TAG, "Ingredient fetch failed: " + errorMessage);
        view.showError(errorMessage);
    }
}