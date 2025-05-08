package com.example.foodplanner.weeklyplan.view;

import android.content.Context;
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

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.ViewHolder> {

    private final Context context;
    private List<ScheduleMeal> meals;
    private final Consumer<ScheduleMeal> onDeleteClick;
    private final Consumer<ScheduleMeal> onDetailsClick;
    private final Consumer<ScheduleMeal> onAddToCalendarClick;

    public CalenderAdapter(Context context, List<ScheduleMeal> meals,
                           Consumer<ScheduleMeal> onDeleteClick,
                           Consumer<ScheduleMeal> onDetailsClick,
                           Consumer<ScheduleMeal> onAddToCalendarClick) {
        this.context = context;
        this.meals = meals;
        this.onDeleteClick = onDeleteClick;
        this.onDetailsClick = onDetailsClick;
        this.onAddToCalendarClick = onAddToCalendarClick;
    }

    public void setMeals(List<ScheduleMeal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal_calender, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleMeal meal = meals.get(position);
        holder.mealName.setText(meal.getStrMeal());
        Glide.with(context)
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.mealImage);

        holder.deleteButton.setOnClickListener(v -> onDeleteClick.accept(meal));
        holder.itemView.setOnClickListener(v -> onDetailsClick.accept(meal));
        holder.addToCalendarButton.setOnClickListener(v -> onAddToCalendarClick.accept(meal));
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName;
        ImageButton deleteButton;
        ImageButton addToCalendarButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);
            deleteButton = itemView.findViewById(R.id.remove_button);
            addToCalendarButton = itemView.findViewById(R.id.add_to_calendar_button);
        }
    }
}