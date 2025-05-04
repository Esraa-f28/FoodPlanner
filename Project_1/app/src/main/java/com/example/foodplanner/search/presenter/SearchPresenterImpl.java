package com.example.foodplanner.search.presenter;

import android.util.Log;

import com.example.foodplanner.model.pojo.Category;
import com.example.foodplanner.model.pojo.Country;
import com.example.foodplanner.model.pojo.Ingredients;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.remote_source.category.CategoryNetworkCallback;
import com.example.foodplanner.model.remote_source.country.CountryNetworkCallback;
import com.example.foodplanner.model.remote_source.ingredient.IngredientNetworkCallback;
import com.example.foodplanner.model.remote_source.meal.MealNetworkCallback;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.search.view.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenterImpl implements ISearchPresenter, MealNetworkCallback,
        CategoryNetworkCallback, CountryNetworkCallback, IngredientNetworkCallback {

    private final SearchView view;
    private final Repository repository;
    private FilterType currentFilter = FilterType.NONE;
    private String lastQuery = "";

    public SearchPresenterImpl(SearchView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void searchMealsByName(String query) {
        lastQuery = query;
        if (currentFilter == FilterType.NONE) {
            if (query.isEmpty()) {
                view.showMeals(new ArrayList<>());
            } else {
                repository.searchMealsByName(query, this);
            }
        } else {
            view.filterOptions(query);
        }
    }

    @Override
    public void searchMealsByFirstLetter(String letter) {
        if (letter != null && !letter.isEmpty()) {
            repository.searchMealsByFirstLetter(letter, this);
        }
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
    public void searchMealsByCategory(String category) {
        repository.getMealsByCategory(category, this);
    }

    @Override
    public void searchMealsByCountry(String country) {
        repository.getMealsByCountry(country, this);
    }

    @Override
    public void searchMealsByIngredient(String ingredient) {
        repository.getMealsByIngredient(ingredient, this);
    }

    @Override
    public void setCurrentFilter(FilterType filterType) {
        this.currentFilter = filterType;
        switch (filterType) {
            case CATEGORY:
                getCategories();
                break;
            case COUNTRY:
                getCountries();
                break;
            case INGREDIENT:
                getIngredients();
                break;
            case NONE:
                view.hideFilterOptions();
                if (!lastQuery.isEmpty()) {
                    searchMealsByName(lastQuery);
                }
                break;
        }
    }

    @Override
    public FilterType getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public void resetSearch() {
        currentFilter = FilterType.NONE;
        lastQuery = "";
        view.hideFilterOptions();
    }

    @Override
    public void onMealSuccess(List<Meal> meals) {
        if (view != null) {
            view.showMeals(meals != null ? meals : new ArrayList<>());
        }
    }

    @Override
    public void onMealFailure(String errorMessage) {
        if (view != null) {
            view.showError(errorMessage);
        }
    }

    @Override
    public void onCategorySuccess(List<Category> categories) {
        if (view != null) {
            view.showFilterOptions(categories != null ? categories : new ArrayList<>(), FilterType.CATEGORY);
        }
    }

    @Override
    public void onCategoryFailure(String errorMessage) {
        if (view != null) {
            view.showError(errorMessage);
        }
    }

    @Override
    public void onCountrySuccess(List<Country> countries) {
        if (countries != null) {
            Log.d("SearchPresenter", "Countries received: " + countries.size());
            for (Country country : countries) {
                Log.d("SearchPresenter", "Country: " + country.getStrArea() + ", Thumb: " + country.getStrAreaThumb());
            }
        } else {
            Log.d("SearchPresenter", "Countries list is null");
        }
        if (view != null) {
            view.showFilterOptions(countries != null ? countries : new ArrayList<>(), FilterType.COUNTRY);
        }
    }

    @Override
    public void onIngredientSuccess(List<Ingredients> ingredients) {
        if (ingredients != null) {
            Log.d("SearchPresenter", "Ingredients received: " + ingredients.size());
            for (Ingredients ingredient : ingredients) {
                Log.d("SearchPresenter", "Ingredient: " + ingredient.getStrIngredient() + ", Thumb: " + ingredient.getStrIngredientThumb());
            }
        } else {
            Log.d("SearchPresenter", "Ingredients list is null");
        }
        if (view != null) {
            view.showFilterOptions(ingredients != null ? ingredients : new ArrayList<>(), FilterType.INGREDIENT);
        }
    }
    @Override
    public void onCountryFailure(String errorMessage) {
        if (view != null) {
            view.showError(errorMessage);
        }
    }

//    @Override
//    public void onIngredientSuccess(List<Ingredients> ingredients) {
//        if (view != null) {
//            view.showFilterOptions(ingredients != null ? ingredients : new ArrayList<>(), FilterType.INGREDIENT);
//        }
//    }

    @Override
    public void onIngredientFailure(String errorMessage) {
        if (view != null) {
            view.showError(errorMessage);
        }
    }

}