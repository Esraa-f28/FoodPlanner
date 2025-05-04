package com.example.foodplanner.model.local_source;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;

@Database(entities = {Meal.class, ScheduleMeal.class}, version = 1, exportSchema = false)
public abstract class MealDatabase extends RoomDatabase {

    private static volatile MealDatabase instance;  // Use volatile for thread safety

    public abstract MealDAO mealDAO();
    public abstract SchedMealDAO schedMealDAO();

    public static MealDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (MealDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    MealDatabase.class,
                                    "meal_database"
                            ).fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}