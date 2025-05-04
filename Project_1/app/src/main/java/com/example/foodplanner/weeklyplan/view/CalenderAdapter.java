package com.example.foodplanner.weeklyplan.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.model.pojo.ScheduleMeal;

import java.util.List;
import java.util.function.Consumer;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.ScheduleMealViewHolder> {

    private static final String TAG = "CalenderAdapter";
    private Context context;
    private List<ScheduleMeal> meals;
    private Consumer<ScheduleMeal> onDeleteClick;
    private Consumer<ScheduleMeal> onMealClick;

    public CalenderAdapter(Context context, List<ScheduleMeal> meals, Consumer<ScheduleMeal> onDeleteClick, Consumer<ScheduleMeal> onMealClick) {
        this.context = context != null ? context.getApplicationContext() : null;
        this.meals = meals;
        this.onDeleteClick = onDeleteClick;
        this.onMealClick = onMealClick;
        Log.d(TAG, "Adapter created, context: " + (this.context != null ? "not null" : "null"));
    }

    public void setMeals(List<ScheduleMeal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
        Log.d(TAG, "setMeals called, meals size: " + (meals != null ? meals.size() : 0));
    }

    @NonNull
    @Override
    public ScheduleMealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext().getApplicationContext();
        Log.d(TAG, "onCreateViewHolder, context set: " + (context != null ? "not null" : "null"));
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_meal, parent, false);
        return new ScheduleMealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleMealViewHolder holder, int position) {
        ScheduleMeal meal = meals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    class ScheduleMealViewHolder extends RecyclerView.ViewHolder {
        private ImageView mealImage;
        private TextView mealName, mealTime;
        private ImageButton deleteButton;

        public ScheduleMealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);
            mealTime = itemView.findViewById(R.id.meal_time);
            deleteButton = itemView.findViewById(R.id.delete_button);
            Log.d(TAG, "ViewHolder created, mealImage: " + (mealImage != null ? "not null" : "null") +
                    ", deleteButton: " + (deleteButton != null ? "not null" : "null"));

            itemView.setOnClickListener(v -> {
                ScheduleMeal meal = meals.get(getAdapterPosition());
                if (onMealClick != null && meal != null) {
                    onMealClick.accept(meal);
                }
            });

            if (deleteButton != null) {
                deleteButton.setOnClickListener(v -> {
                    ScheduleMeal meal = meals.get(getAdapterPosition());
                    if (onDeleteClick != null && meal != null) {
                        onDeleteClick.accept(meal);
                    }
                });
            } else {
                Log.w(TAG, "deleteButton is null");
            }
        }

        public void bind(ScheduleMeal meal) {
            mealName.setText(meal.getStrMeal() != null ? meal.getStrMeal() : "Unknown");
            mealTime.setText(meal.getMealTime() != null ? meal.getMealTime() : "Unknown");
            Log.d(TAG, "Binding meal: " + (meal.getStrMeal() != null ? meal.getStrMeal() : "null") +
                    ", thumb: " + meal.getStrMealThumb() +
                    ", context: " + (context != null ? "not null" : "null"));
            if (mealImage != null && context != null) {
                String thumbnail = meal.getStrMealThumb();
                if (thumbnail != null && !thumbnail.isEmpty()) {
                    Glide.with(context)
                            .load(thumbnail)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(mealImage);
                } else {
                    Glide.with(context)
                            .load(R.drawable.placeholder)
                            .into(mealImage);
                }
            } else {
                Log.w(TAG, "Cannot load image, mealImage: " + (mealImage != null ? "not null" : "null") +
                        ", context: " + (context != null ? "not null" : "null"));
            }
        }
    }
}