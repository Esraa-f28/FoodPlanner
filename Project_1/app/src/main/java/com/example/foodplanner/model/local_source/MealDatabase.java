package com.example.foodplanner.model.local_source;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.foodplanner.model.local_source.MealDAO;
import com.example.foodplanner.model.local_source.SchedMealDAO;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;

@Database(entities = {Meal.class, ScheduleMeal.class}, version = 3, exportSchema = false)
public abstract class MealDatabase extends RoomDatabase {
    private static MealDatabase instance;

    public abstract MealDAO mealDAO();
    public abstract SchedMealDAO schedMealDAO();

    public static synchronized MealDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MealDatabase.class, "meal_database")
                    .fallbackToDestructiveMigration()
                    //.addMigrations(MIGRATION_1_2)
                    .build();
        }
        return instance;
    }

//    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            // Add userId column to meals_table
//            database.execSQL("ALTER TABLE meals_table ADD COLUMN userId TEXT DEFAULT NULL");
//            // Add userId column to schedule_meals_table
//            database.execSQL("ALTER TABLE sched_meals ADD COLUMN userId TEXT DEFAULT NULL");
//        }
//    };
}