package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "workout_logs")
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private Integer userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "session_date", nullable = false)
    @JsonProperty("session_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "workout_type", nullable = false)
    @JsonProperty("workout_type")
    private WorkoutType workoutType;

    @Column(name = "exercise_name", nullable = false)
    @JsonProperty("exercise_name")
    private String exerciseName;

    @Column(name = "exercise_order", nullable = false)
    @JsonProperty("exercise_order")
    private Integer exerciseOrder;

    @Column(name = "set_number")
    @JsonProperty("set_number")
    private Integer setNumber;

    @Column(name = "weight_kg")
    @JsonProperty("weight_kg")
    private Double weightKg;

    @Column
    private Integer reps;

    @Column(name = "rest_seconds")
    @JsonProperty("rest_seconds")
    private Integer restSeconds = 60;

    @Column(name = "duration_minutes")
    @JsonProperty("duration_minutes")
    private Integer durationMinutes;

    @Column(name = "calories_burned")
    @JsonProperty("calories_burned")
    private Double caloriesBurned;

    @Enumerated(EnumType.STRING)
    @Column
    private Intensity intensity = Intensity.medium;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "created_at")
    @JsonProperty("created_at")
    private Timestamp createdAt;

    @Column(name = "is_deleted")
    @JsonProperty("is_deleted")
    private Integer isDeleted = 0;

    public enum WorkoutType {
        strength, cardio
    }

    public enum Intensity {
        low, medium, high
    }

    // Getters and Setters
    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }
    
    public WorkoutType getWorkoutType() { return workoutType; }
    public void setWorkoutType(WorkoutType workoutType) { this.workoutType = workoutType; }
    
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
    
    public Intensity getIntensity() { return intensity; }
    public void setIntensity(Intensity intensity) { this.intensity = intensity; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
