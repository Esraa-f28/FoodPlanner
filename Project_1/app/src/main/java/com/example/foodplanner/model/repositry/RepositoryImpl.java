package com.example.foodplanner.model.repositry;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.foodplanner.model.local_source.IMealLocalDataSource;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;
import com.example.foodplanner.model.remote_source.IRemoteDataSource;
import com.example.foodplanner.model.remote_source.category.CategoryNetworkCallback;
import com.example.foodplanner.model.remote_source.country.CountryNetworkCallback;
import com.example.foodplanner.model.remote_source.ingredient.IngredientNetworkCallback;
import com.example.foodplanner.model.remote_source.meal.MealNetworkCallback;

import java.util.List;

public class RepositoryImpl implements Repository {

    private final IRemoteDataSource remoteDataSource;
    private final IMealLocalDataSource localDataSource;
    private static RepositoryImpl instance = null;

    private RepositoryImpl(IRemoteDataSource remoteDataSource, IMealLocalDataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
    }

    public static RepositoryImpl getInstance(IRemoteDataSource remoteDataSource, IMealLocalDataSource localDataSource) {
        if (instance == null) {
            instance = new RepositoryImpl(remoteDataSource, localDataSource);
        }
        return instance;
    }

    @Override
    public LiveData<Meal> getMealById(String mealId) {
        MediatorLiveData<Meal> result = new MediatorLiveData<>();

        // Observe local database
        LiveData<Meal> localMeal = localDataSource.getMealById(mealId);
        result.addSource(localMeal, meal -> {
            if (meal != null && isCompleteMeal(meal)) {
                result.setValue(meal); // Emit complete meal from local database
            } else {
                // Fetch from network
                remoteDataSource.getMealById(mealId, new MealNetworkCallback() {
                    @Override
                    public void onMealSuccess(List<Meal> meals) {
                        if (meals != null && !meals.isEmpty()) {
                            Meal meal = meals.get(0);
                            if (isCompleteMeal(meal)) {
                                localDataSource.insertMeal(meal); // Cache only complete meals
                                result.postValue(meal); // Emit network result
                            } else {
                                result.postValue(null);
                            }
                        } else {
                            result.postValue(null);
                        }
                    }

                    @Override
                    public void onMealFailure(String errorMessage) {
                        result.postValue(null); // Emit null to trigger error in presenter
                    }
                });
            }
        });

        return result;
    }

    // Helper method to check if a Meal object is complete
    public boolean isCompleteMeal(Meal meal) {
        return meal != null &&
                meal.getStrMeal() != null &&
                meal.getStrCategory() != null &&
                meal.getStrArea() != null &&
                meal.getStrInstructions() != null;
    }

    // ==================== Remote Methods ====================
    @Override
    public void getCategories(CategoryNetworkCallback callback) {
        remoteDataSource.getCategories(callback);
    }

    @Override
    public void getMealsByCategory(String categoryName, MealNetworkCallback callback) {
        remoteDataSource.getMealsByCategory(categoryName, callback);
    }

    @Override
    public void getMealById1(String mealId, MealNetworkCallback callback) {
        remoteDataSource.getMealById(mealId, callback);
    }

    @Override
    public void getMealsByCountry(String countryName, MealNetworkCallback callback) {
        remoteDataSource.getMealsByCountry(countryName, callback);
    }

    @Override
    public void getMealsByIngredient(String ingredientName, MealNetworkCallback callback) {
        remoteDataSource.getMealsByIngredient(ingredientName, callback);
    }

    @Override
    public void getCountries(CountryNetworkCallback callback) {
        remoteDataSource.getCountries(callback);
    }

    @Override
    public void getIngredients(IngredientNetworkCallback callback) {
        remoteDataSource.getIngredients(callback);
    }

    @Override
    public void getRandomMeal(MealNetworkCallback callback) {
        remoteDataSource.getRandomMeal(callback);
    }

    @Override
    public void searchMealsByName(String name, MealNetworkCallback callback) {
        remoteDataSource.searchMealsByName(name, callback);
    }

    @Override
    public void searchMealsByFirstLetter(String letter, MealNetworkCallback callback) {
        remoteDataSource.searchMealsByFirstLetter(letter, callback);
    }

    // ==================== Local Methods ====================
    @Override
    public void insertMeal(Meal meal) {
        if (isCompleteMeal(meal)) {
            localDataSource.insertMeal(meal); // Cache only complete meals
        } else {
            Log.e("RepositoryImpl", "Attempted to cache incomplete meal: " + (meal != null ? meal.getStrMeal() : "null"));
        }
    }

    @Override
    public void deleteMeal(Meal meal) {
        localDataSource.deleteMeal(meal);
    }

    @Override
    public LiveData<List<Meal>> getAllSavedMeals() {
        return localDataSource.getAllSavedMeals();
    }

    // ==================== Schedule Methods ====================
    @Override
    public LiveData<List<ScheduleMeal>> getAllScheduledMeals() {
        return localDataSource.getAllSchedMeals();
    }

    @Override
    public void insertScheduledMeal(ScheduleMeal meal) {
        localDataSource.insertSchedMeal(meal);
    }

    @Override
    public void deleteScheduledMeal(ScheduleMeal meal) {
        localDataSource.deleteSchedMeal(meal);
    }

    @Override
    public LiveData<ScheduleMeal> getScheduledMealById(String id) {
        return localDataSource.getSchedMealById(id);
    }

    @Override
    public LiveData<List<ScheduleMeal>> getMealsForDate(String date) {
        return localDataSource.getMealsForDate(date);
    }
}