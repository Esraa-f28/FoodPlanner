// IngredientsAdapter.java
package com.example.foodplanner.mealdetails.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.mealdetails.view.MealIngredientExtractor;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    private List<MealIngredientExtractor.IngredientItem> ingredients;

    public IngredientsAdapter(List<MealIngredientExtractor.IngredientItem> ingredients) {
        this.ingredients = ingredients;
    }

    public void setIngredients(List<MealIngredientExtractor.IngredientItem> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        MealIngredientExtractor.IngredientItem ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ingredientImage;
        private final TextView ingredientName;
        private final TextView ingredientMeasure;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientImage = itemView.findViewById(R.id.ingredientImage);
            ingredientName = itemView.findViewById(R.id.ingredientName);
            ingredientMeasure = itemView.findViewById(R.id.ingredientMeasure);
        }

        public void bind(MealIngredientExtractor.IngredientItem ingredient) {
            ingredientName.setText(ingredient.getName());
            ingredientMeasure.setText(ingredient.getMeasure());

            Glide.with(itemView.getContext())
                    .load(ingredient.getImageUrl())
                    .placeholder(R.drawable.ic_ingredient_placeholder)
                    .error(R.drawable.ic_ingredient_error)
                    .into(ingredientImage);
        }
    }
}