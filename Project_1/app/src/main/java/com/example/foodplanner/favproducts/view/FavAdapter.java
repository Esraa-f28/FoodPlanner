package com.example.foodplanner.favproducts.view;

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
import com.example.foodplanner.FragmentCommunication;
import com.example.foodplanner.R;
import com.example.foodplanner.model.pojo.Meal;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavoriteViewHolder> {

    private Context context;
    private List<Meal> meals;
    private OnFavClickListener listener;
    private FragmentCommunication communicator; // Added for navigation

    public FavAdapter(Context context, OnFavClickListener listener, FragmentCommunication communicator) {
        this.context = context;
        this.listener = listener;
        this.communicator = communicator;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_meal, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.bind(meal);
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView mealImage;
        private TextView mealName, mealCategory;
        private ImageButton removeButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);
            mealCategory = itemView.findViewById(R.id.meal_category);
            removeButton = itemView.findViewById(R.id.remove_button);

            // Add click listener for the entire item view
            itemView.setOnClickListener(v -> {
                Meal meal = meals.get(getAdapterPosition());
                if (communicator != null && meal != null) {
                    communicator.navigateToMealDetails(meal);
                }
            });
        }

        public void bind(Meal meal) {
            mealName.setText(meal.getStrMeal());
            mealCategory.setText(meal.getStrCategory());

            Glide.with(context)
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(mealImage);

            removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClick(meal);
                }
            });
        }
    }
}