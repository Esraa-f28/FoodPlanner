package com.example.foodplanner.model.pojo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "sched_meals", primaryKeys = {"idMeal", "userId"})
public class ScheduleMeal {
    @NonNull
    public String idMeal;

    public String strMeal;
    public String strMealThumb;
    public String scheduledDate; // Format: YYYY-MM-DD
    public String mealTime; // e.g., Breakfast, Lunch, Dinner

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    @NonNull
    private String userId;

    public ScheduleMeal() {
    }

    public ScheduleMeal(String idMeal, String strMeal, String scheduledDate, String mealTime) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.scheduledDate = scheduledDate;
        this.mealTime = mealTime;
    }

    public ScheduleMeal(String idMeal, String strMeal, String strMealThumb, String date, String time) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
        this.scheduledDate = date;
        this.mealTime = time;
    }
    public ScheduleMeal(String idMeal, String strMeal, String strMealThumb, String date, String time,String userId) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
        this.scheduledDate = date;
        this.mealTime = time;
        this.userId = userId;
    }

    @NonNull
    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(@NonNull String idMeal) {
        this.idMeal = idMeal;
    }

    public String getStrMeal() {
        return strMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }


}