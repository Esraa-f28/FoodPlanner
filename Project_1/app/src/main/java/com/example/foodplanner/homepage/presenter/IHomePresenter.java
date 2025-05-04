package com.example.foodplanner.homepage.presenter;

public interface IHomePresenter {
    void getRandomMeal();
    void getCategories();
    void getCountries();
    void getIngredients();
    void getMealsByCategory(String category);
    void getMealsByCountry(String country);
    void getMealsByIngredient(String ingredient);
}