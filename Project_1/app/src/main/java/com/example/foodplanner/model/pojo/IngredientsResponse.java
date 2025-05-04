package com.example.foodplanner.model.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientsResponse {
    @SerializedName("meals")
    private List<Ingredients> Ingredients;
    public List<Ingredients> getIngredients() {
        return Ingredients;
    }
}
