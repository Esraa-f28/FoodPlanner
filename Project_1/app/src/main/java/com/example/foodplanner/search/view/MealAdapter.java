package com.example.foodplanner.search.view;

import android.content.Context;
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

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private Context context;
    private List<Meal> meals;
    private FragmentCommunication communicator;

    public MealAdapter(List<Meal> meals, FragmentCommunication communicator) {
        this.meals = meals;
        this.communicator = communicator;
    }

    public void updateMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private ImageView mealImage;
        private TextView mealName;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);

            itemView.setOnClickListener(v -> {
                Meal meal = meals.get(getAdapterPosition());
                if (communicator != null && meal != null && meal.getIdMeal() != null) {
                    communicator.navigateToMealDetails(meal);
                }
            });
        }

        public void bind(Meal meal) {
            mealName.setText(meal.getStrMeal() != null ? meal.getStrMeal() : "Unknown");
            Glide.with(context)
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(mealImage);
        }
    }
}