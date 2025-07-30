package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "workout_logs")
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "exercise_name")
    private String exerciseName;

    @Column
    private Integer sets;

    @Column
    private Integer reps;

    @Column
    private Double weight;

    @Column(name = "duration_min")
    private Integer durationMin;

    @Column(name = "calories_burned")
    private Double caloriesBurned;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "logged_at")
    private Timestamp loggedAt;

    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }
    public Double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Double caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Timestamp getLoggedAt() { return loggedAt; }
    public void setLoggedAt(Timestamp loggedAt) { this.loggedAt = loggedAt; }
}
