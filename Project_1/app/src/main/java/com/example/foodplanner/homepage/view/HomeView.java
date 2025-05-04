package com.example.foodplanner.homepage.view;

import com.example.foodplanner.model.pojo.Category;
import com.example.foodplanner.model.pojo.Country;
import com.example.foodplanner.model.pojo.Ingredients;
import com.example.foodplanner.model.pojo.Meal;
import java.util.List;

public interface HomeView {
    void showRandomMeal(Meal meal);
    void showCategories(List<Category> categories);
    void showCountries(List<Country> countries);
    void showIngredients(List<Ingredients> ingredients);
    void showMealsByFilter(List<Meal> meals);
    void showError(String message);
}