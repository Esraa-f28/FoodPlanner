package com.example.foodplanner.model.repositry;

import androidx.lifecycle.LiveData;

import com.example.foodplanner.model.pojo.CategoryResponse;
import com.example.foodplanner.model.pojo.CountryResponse;
import com.example.foodplanner.model.pojo.IngredientsResponse;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.MealResponse;
import com.example.foodplanner.model.pojo.ScheduleMeal;
import com.example.foodplanner.model.remote_source.category.CategoryNetworkCallback;
import com.example.foodplanner.model.remote_source.country.CountryNetworkCallback;
import com.example.foodplanner.model.remote_source.ingredient.IngredientNetworkCallback;
import com.example.foodplanner.model.remote_source.meal.MealNetworkCallback;

import java.util.List;

public interface Repository {

    // Method to get all categories
    void getCategories(CategoryNetworkCallback callback);

    // Method to get meals by category
    void getMealsByCategory(String categoryName, MealNetworkCallback callback);

    // Method to get meals by country
    void getMealsByCountry(String countryName, MealNetworkCallback callback);

    // Method to get meals by ingredient
    void getMealsByIngredient(String ingredientName, MealNetworkCallback callback);

    // Method to get all countries
    void getCountries(CountryNetworkCallback callback);

    // Method to get all ingredients
    void getIngredients(IngredientNetworkCallback callback);

    // Method to get a random meal
    void getRandomMeal(MealNetworkCallback callback);

    void searchMealsByName(String name, MealNetworkCallback callback);
    void searchMealsByFirstLetter(String letter, MealNetworkCallback callback);

    void getMealById1(String mealId, MealNetworkCallback callback);

    // ==================== Local Database Methods ====================//
    void insertMeal(Meal meal);
    void deleteMeal(Meal meal);
    LiveData<List<Meal>> getAllSavedMeals();
    LiveData<Meal> getMealById(String mealId);

    LiveData<List<ScheduleMeal>> getAllScheduledMeals();
    void insertScheduledMeal(ScheduleMeal meal);
    void deleteScheduledMeal(ScheduleMeal meal);
    LiveData<ScheduleMeal> getScheduledMealById(String id);

    LiveData<List<ScheduleMeal>> getMealsForDate(String date);
}