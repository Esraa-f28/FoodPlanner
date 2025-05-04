package com.example.foodplanner.search.view;

import com.example.foodplanner.model.pojo.Category;
import com.example.foodplanner.model.pojo.Country;
import com.example.foodplanner.model.pojo.Ingredients;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.search.presenter.ISearchPresenter;

import java.util.List;

public interface SearchView {
    void showMeals(List<Meal> meals);
    void showFilterOptions(List<?> options, ISearchPresenter.FilterType filterType);
    void filterOptions(String query);
    void hideFilterOptions();
    void showError(String errorMessage);
    void showEmptyState();
}