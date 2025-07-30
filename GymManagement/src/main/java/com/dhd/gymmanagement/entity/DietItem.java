package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "diet_items")
public class DietItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_item_id")
    private Integer dietItemId;

    @ManyToOne
    @JoinColumn(name = "diet_plan_id")
    private DietPlan dietPlan;

    @Column(name = "meal_name")
    private String mealName;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private MealType mealType = MealType.BREAKFAST;

    @Column
    private Integer calories;

    @Column
    private Integer protein;

    @Column
    private Integer carb;

    @Column
    private Integer fat;

    @Column(name = "serving_size")
    private String servingSize;

    @Column(name = "preparation_notes", columnDefinition = "text")
    private String preparationNotes;

    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK
    }

    public Integer getDietItemId() { return dietItemId; }
    public void setDietItemId(Integer dietItemId) { this.dietItemId = dietItemId; }
    public DietPlan getDietPlan() { return dietPlan; }
    public void setDietPlan(DietPlan dietPlan) { this.dietPlan = dietPlan; }
    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }
    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }
    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }
    public Integer getProtein() { return protein; }
    public void setProtein(Integer protein) { this.protein = protein; }
    public Integer getCarb() { return carb; }
    public void setCarb(Integer carb) { this.carb = carb; }
    public Integer getFat() { return fat; }
    public void setFat(Integer fat) { this.fat = fat; }
    public String getServingSize() { return servingSize; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }
    public String getPreparationNotes() { return preparationNotes; }
    public void setPreparationNotes(String preparationNotes) { this.preparationNotes = preparationNotes; }
}
