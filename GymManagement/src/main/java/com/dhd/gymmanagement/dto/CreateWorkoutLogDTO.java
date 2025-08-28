package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.WorkoutLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;

public class WorkoutLogDTO {
    
    @JsonProperty("user_id")
    private Integer userId;
    
    @JsonProperty("session_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sessionDate;
    
    @JsonProperty("workout_type")
    private String workoutType;
    
    @JsonProperty("exercise_name")
    private String exerciseName;
    
    @JsonProperty("exercise_order")
    private Integer exerciseOrder;
    
    @JsonProperty("set_number")
    private Integer setNumber;
    
    @JsonProperty("weight_kg")
    private Double weightKg;
    
    private Integer reps;
    
    @JsonProperty("rest_seconds")
    private Integer restSeconds;
    
    @JsonProperty("duration_minutes")
    private Integer durationMinutes;
    
    @JsonProperty("calories_burned")
    private Double caloriesBurned;
    
    private String intensity;
    
    private String notes;
    
    public WorkoutLog toEntity() {
        WorkoutLog entity = new WorkoutLog();
        entity.setUserId(this.userId);
        entity.setSessionDate(this.sessionDate);
        entity.setWorkoutType(WorkoutLog.WorkoutType.valueOf(this.workoutType));
        entity.setExerciseName(this.exerciseName);
        entity.setExerciseOrder(this.exerciseOrder != null ? this.exerciseOrder : 1);
        entity.setSetNumber(this.setNumber);
        entity.setWeightKg(this.weightKg);
        entity.setReps(this.reps);
        entity.setRestSeconds(this.restSeconds != null ? this.restSeconds : 60);
        entity.setDurationMinutes(this.durationMinutes);
        entity.setCaloriesBurned(this.caloriesBurned);
        if (this.intensity != null) {
            entity.setIntensity(WorkoutLog.Intensity.valueOf(this.intensity));
        }
        entity.setNotes(this.notes);
        return entity;
    }
    
    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }
    
    public String getWorkoutType() { return workoutType; }
    public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }
    
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    
    public Integer getExerciseOrder() { return exerciseOrder; }
    public void setExerciseOrder(Integer exerciseOrder) { this.exerciseOrder = exerciseOrder; }
    
    public Integer getSetNumber() { return setNumber; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }
    
    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    
    public Integer getRestSeconds() { return restSeconds; }
    public void setRestSeconds(Integer restSeconds) { this.restSeconds = restSeconds; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public Double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Double caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
