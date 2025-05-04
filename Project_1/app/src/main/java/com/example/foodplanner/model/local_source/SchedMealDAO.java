package com.example.foodplanner.model.local_source;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.foodplanner.model.pojo.ScheduleMeal;

import java.util.List;

@Dao
public interface SchedMealDAO {

    @Query("SELECT * FROM sched_meals")
    LiveData<List<ScheduleMeal>> getScheduledMeals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScheduledMeal(ScheduleMeal meal);

    @Query("SELECT * FROM sched_meals WHERE idMeal = :id")
    LiveData<ScheduleMeal> getScheduledMealById(String id);

    @Delete
    void deleteScheduledMeal(ScheduleMeal meal);

    @Query("SELECT * FROM sched_meals WHERE scheduledDate = :date ORDER BY mealTime")
    LiveData<List<ScheduleMeal>> getMealsForDate(String date);
}