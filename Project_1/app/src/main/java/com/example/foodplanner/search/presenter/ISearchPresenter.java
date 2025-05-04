package com.example.foodplanner.search.presenter;

import com.example.foodplanner.model.pojo.Meal;

import java.util.List;

public interface ISearchPresenter {
    void searchMealsByName(String query);
    void searchMealsByFirstLetter(String letter);
    void getCategories();
    void getCountries();
    void getIngredients();
    void searchMealsByCategory(String category);
    void searchMealsByCountry(String country);
    void searchMealsByIngredient(String ingredient);
    void setCurrentFilter(FilterType filterType);
    FilterType getCurrentFilter();
    void resetSearch();
    enum FilterType {
        CATEGORY, COUNTRY, INGREDIENT, NONE
    }
}