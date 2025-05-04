package com.example.foodplanner.homepage.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.foodplanner.FragmentCommunication;
import com.example.foodplanner.R;
import com.example.foodplanner.model.pojo.Meal;
import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> {
    private static final String TAG = "MealAdapter";
    private List<Meal> meals;
    private FragmentCommunication communicator;

    public MealAdapter(List<Meal> meals, FragmentCommunication communicator) {
        this.meals = meals != null ? meals : new ArrayList<>();
        this.communicator = communicator;
        Log.d(TAG, "Initialized with " + this.meals.size() + " meals, communicator: " + (communicator != null));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = meals.get(position);
        if (meal == null) {
            Log.e(TAG, "Meal at position " + position + " is null");
            holder.mealName.setText("Unknown");
            return;
        }
        Log.d(TAG, "Binding meal: " + (meal.getStrMeal() != null ? meal.getStrMeal() : "null") +
                ", idMeal: " + (meal.getIdMeal() != null ? meal.getIdMeal() : "null"));

        holder.mealName.setText(meal.getStrMeal() != null ? meal.getStrMeal() : "Unknown");
        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.mealImage);

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Clicked meal: " + (meal.getStrMeal() != null ? meal.getStrMeal() : "null") +
                    ", idMeal: " + (meal.getIdMeal() != null ? meal.getIdMeal() : "null"));
            if (communicator != null && meal.getIdMeal() != null) {
                communicator.navigateToMealDetails(meal);
            } else {
                Log.e(TAG, "Cannot navigate: communicator=" + communicator + ", mealId=" + meal.getIdMeal());
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public void updateMeals(List<Meal> newMeals) {
        this.meals = newMeals != null ? newMeals : new ArrayList<>();
        Log.d(TAG, "Updated meals: count=" + this.meals.size());
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mealName;
        ImageView mealImage;

        ViewHolder(View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.meal_name);
            mealImage = itemView.findViewById(R.id.meal_image);
        }
    }
}