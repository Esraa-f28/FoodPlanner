package com.example.foodplanner.model.pojo;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("strCategory")
    private String strCategory;

    @SerializedName("strCategoryThumb")
    private String strCategoryThumb;

    public String getStrCategory() {
        return strCategory;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public String getStrCategoryThumb() {
        return strCategoryThumb;
    }

    public void setStrCategoryThumb(String strCategoryThumb) {
        this.strCategoryThumb = strCategoryThumb;
    }
}