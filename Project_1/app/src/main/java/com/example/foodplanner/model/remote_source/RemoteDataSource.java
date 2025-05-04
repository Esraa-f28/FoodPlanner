package com.example.foodplanner.model.remote_source;

import com.example.foodplanner.model.pojo.CategoryResponse;
import com.example.foodplanner.model.pojo.CountryResponse;
import com.example.foodplanner.model.pojo.IngredientsResponse;
import com.example.foodplanner.model.pojo.MealResponse;
import com.example.foodplanner.model.remote_source.category.CategoryNetworkCallback;
import com.example.foodplanner.model.remote_source.country.CountryNetworkCallback;
import com.example.foodplanner.model.remote_source.ingredient.IngredientNetworkCallback;
import com.example.foodplanner.model.remote_source.meal.MealNetworkCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;

public class RemoteDataSource implements IRemoteDataSource {
    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private ApiService service;
    private static RemoteDataSource instance = null;

    private RemoteDataSource() {
        service = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    public static RemoteDataSource getInstance() {
        if (instance == null) {
            instance = new RemoteDataSource();
        }
        return instance;
    }

    @Override
    public void getMealById(String mealId, MealNetworkCallback callback) {
        Call<MealResponse> call = service.getMealById(mealId);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onMealSuccess(response.body().getMeals());
                } else {
                    callback.onMealSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onMealFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getCategories(CategoryNetworkCallback callback) {
        Call<CategoryResponse> call = service.getAllCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCategories() != null) {
                    callback.onCategorySuccess(response.body().getCategories());
                } else {
                    callback.onCategorySuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                callback.onCategoryFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getMealsByCategory(String categoryName, MealNetworkCallback callback) {
        Call<MealResponse> call = service.getMealsByCategory(categoryName);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onMealSuccess(response.body().getMeals());
                } else {
                    callback.onMealSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onMealFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getMealsByCountry(String countryName, MealNetworkCallback callback) {
        Call<MealResponse> call = service.getMealsByCountry(countryName);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onMealSuccess(response.body().getMeals());
                } else {
                    callback.onMealSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onMealFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getMealsByIngredient(String ingredientName, MealNetworkCallback callback) {
        Call<MealResponse> call = service.getMealsByIngredient(ingredientName);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onMealSuccess(response.body().getMeals());
                } else {
                    callback.onMealSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onMealFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getCountries(CountryNetworkCallback callback) {
        Call<CountryResponse> call = service.getAllCountries();
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCountries() != null) {
                    callback.onCountrySuccess(response.body().getCountries());
                } else {
                    callback.onCountrySuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                callback.onCountryFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getIngredients(IngredientNetworkCallback callback) {
        Call<IngredientsResponse> call = service.getAllIngredients();
        call.enqueue(new Callback<IngredientsResponse>() {
            @Override
            public void onResponse(Call<IngredientsResponse> call, Response<IngredientsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getIngredients() != null) {
                    callback.onIngredientSuccess(response.body().getIngredients());
                } else {
                    callback.onIngredientSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<IngredientsResponse> call, Throwable t) {
                callback.onIngredientFailure(t.getMessage());
            }
        });
    }

    @Override
    public void getRandomMeal(MealNetworkCallback callback) {
        Call<MealResponse> call = service.getRandomMeal();
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onMealSuccess(response.body().getMeals());
                } else {
                    callback.onMealSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onMealFailure(t.getMessage());
            }
        });
    }

    @Override
    public void searchMealsByName(String name, MealNetworkCallback callback) {
        Call<MealResponse> call = service.searchMealsByName(name);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onMealSuccess(response.body().getMeals());
                } else {
                    callback.onMealSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onMealFailure(t.getMessage());
            }
        });
    }

    @Override
    public void searchMealsByFirstLetter(String letter, MealNetworkCallback callback) {
        Call<MealResponse> call = service.searchMealsByFirstLetter(letter);
        call.enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    callback.onMealSuccess(response.body().getMeals());
                } else {
                    callback.onMealSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onMealFailure(t.getMessage());
            }
        });
    }
}