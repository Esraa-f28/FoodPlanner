package com.example.foodplanner.model.remote_source.ingredient;

import com.example.foodplanner.model.pojo.Ingredients;

import java.util.List;

public interface IngredientNetworkCallback {
    void onIngredientSuccess(List<Ingredients> ingredients);
    void onIngredientFailure(String errorMessage);
}
