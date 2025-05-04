package com.example.foodplanner.model.remote_source;

import com.example.foodplanner.model.pojo.CategoryResponse;
import com.example.foodplanner.model.pojo.CountryResponse;
import com.example.foodplanner.model.pojo.IngredientsResponse;
import com.example.foodplanner.model.pojo.MealResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("categories.php")
    Call<CategoryResponse> getAllCategories();

    @GET("filter.php")
    Call<MealResponse> getMealsByCategory(@Query("c") String categoryName);

    @GET("filter.php")
    Call<MealResponse> getMealsByCountry(@Query("a") String countryName);

    @GET("filter.php")
    Call<MealResponse> getMealsByIngredient(@Query("i") String ingredientName);

    @GET("list.php?a=list")
    Call<CountryResponse> getAllCountries();

    @GET("list.php?i=list")
    Call<IngredientsResponse> getAllIngredients();

    @GET("random.php")
    Call<MealResponse> getRandomMeal();

    @GET("search.php")
    Call<MealResponse> searchMealsByName(@Query("s") String name);

    @GET("search.php")
    Call<MealResponse> searchMealsByFirstLetter(@Query("f") String letter);

    @GET("lookup.php")
    Call<MealResponse> getMealById(@Query("i") String mealId); // Added
}