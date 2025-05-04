package com.example.foodplanner.mealdetails.view;

import com.example.foodplanner.model.pojo.Meal;

import java.util.ArrayList;
import java.util.List;

public class MealIngredientExtractor {
        public static class IngredientItem {
            private String name;
            private String measure;
            private String imageUrl;

            public IngredientItem(String name, String measure, String imageUrl) {
                this.name = name;
                this.measure = measure;
                this.imageUrl = imageUrl;
            }

            public String getName() { return name; }
            public String getMeasure() { return measure; }
            public String getImageUrl() { return imageUrl; }
        }

        public static List<IngredientItem> extractIngredients(Meal meal) {
            List<IngredientItem> ingredients = new ArrayList<>();

            for (int i = 1; i <= 20; i++) {
                try {
                    String name = (String) meal.getClass().getMethod("getStrIngredient" + i).invoke(meal);
                    String measure = (String) meal.getClass().getMethod("getStrMeasure" + i).invoke(meal);

                    if (name != null && !name.isEmpty()) {
                        String imageUrl = "https://www.themealdb.com/images/ingredients/" + name + ".png";
                        ingredients.add(new IngredientItem(name, measure, imageUrl));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ingredients;
        }
}

