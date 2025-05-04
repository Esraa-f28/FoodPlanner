package com.example.foodplanner.model.remote_source;

import com.example.foodplanner.model.remote_source.category.CategoryNetworkCallback;
import com.example.foodplanner.model.remote_source.country.CountryNetworkCallback;
import com.example.foodplanner.model.remote_source.ingredient.IngredientNetworkCallback;
import com.example.foodplanner.model.remote_source.meal.MealNetworkCallback;

public interface IRemoteDataSource {
    void getCategories(CategoryNetworkCallback callback);
    void getMealsByCategory(String categoryName, MealNetworkCallback callback);
    void getMealsByCountry(String countryName, MealNetworkCallback callback);
    void getMealsByIngredient(String ingredientName, MealNetworkCallback callback);
    void getCountries(CountryNetworkCallback callback);
    void getIngredients(IngredientNetworkCallback callback);
    void getRandomMeal(MealNetworkCallback callback);
    void searchMealsByName(String name, MealNetworkCallback callback);
    void searchMealsByFirstLetter(String letter, MealNetworkCallback callback);
    void getMealById(String mealId, MealNetworkCallback callback); // Added
}