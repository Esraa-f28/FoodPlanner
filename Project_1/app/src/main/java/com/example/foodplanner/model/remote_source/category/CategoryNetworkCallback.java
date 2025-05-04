package com.example.foodplanner.model.remote_source.category;

import com.example.foodplanner.model.pojo.Category;

import java.util.List;

public interface CategoryNetworkCallback {
    void onCategorySuccess(List<Category> categories);
    void onCategoryFailure(String errorMessage);
}
