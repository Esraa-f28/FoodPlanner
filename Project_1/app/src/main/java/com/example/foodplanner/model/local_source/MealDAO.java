package com.example.foodplanner.model.local_source;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.foodplanner.model.pojo.Meal;

import java.util.List;

@Dao
public interface MealDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeal(Meal meal);

    @Delete
    void deleteMeal(Meal meal);

    @Query("SELECT * FROM meals_table")
    LiveData<List<Meal>> getAllMeals();

    @Query("SELECT * FROM meals_table WHERE idMeal = :mealId")
    LiveData<Meal> getMealById(String mealId);

    @Query("SELECT * FROM meals_table WHERE userId = :userId")
    LiveData<List<Meal>> getMealsByUserId(String userId);

    @Query("SELECT * FROM meals_table WHERE idMeal = :mealId AND userId = :userId LIMIT 1")
    LiveData<Meal> getMealByIdAndUserId(String mealId, String userId);

    // Add synchronous query for debugging
    @Query("SELECT * FROM meals_table WHERE idMeal = :mealId")
    Meal getMealByIdSync(String mealId);
}