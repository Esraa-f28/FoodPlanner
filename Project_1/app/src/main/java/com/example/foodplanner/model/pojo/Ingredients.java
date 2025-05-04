package com.example.foodplanner.model.pojo;

import com.google.gson.annotations.SerializedName;

public class Ingredients {
    @SerializedName("strIngredient")
    private String strIngredient;

    private String strIngredientThumb;

    public String getStrIngredient() {
        return strIngredient;
    }

    public void setStrIngredient(String strIngredient) {
        this.strIngredient = strIngredient;
    }

    public String getStrIngredientThumb() {
        return strIngredientThumb != null ? strIngredientThumb : "https://www.themealdb.com/images/ingredients/" + strIngredient + ".png";
    }

    public void setStrIngredientThumb(String strIngredientThumb) {
        this.strIngredientThumb = strIngredientThumb;
    }
}