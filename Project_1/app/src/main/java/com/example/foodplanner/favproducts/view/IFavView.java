package com.example.foodplanner.favproducts.view;
import com.example.foodplanner.model.pojo.Meal;

import java.util.List;

public interface IFavView {
    void showFavorites(List<Meal> meals);
    void showError(String message);
    void onMealRemoved(Meal meal);
}