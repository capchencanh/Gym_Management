package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.WorkoutLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutLogResponseDTO {

    @JsonProperty("log_id")
    private Integer logId;

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("session_name")
    private String sessionName;

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

    @JsonProperty("comments")
    private List<CommentResponseDTO> comments;


    public WorkoutLogResponseDTO() {
    }


    public WorkoutLogResponseDTO(WorkoutLog entity) {
        this.logId = entity.getLogId();
        this.userId = entity.getUserId();
        this.sessionName = entity.getSessionName();
        this.sessionDate = entity.getSessionDate();
        this.workoutType = (entity.getWorkoutType() != null) ? entity.getWorkoutType().name() : null;
        this.exerciseName = entity.getExerciseName();
        this.exerciseOrder = entity.getExerciseOrder();
        this.setNumber = entity.getSetNumber();
        this.weightKg = entity.getWeightKg();
        this.reps = entity.getReps();
        this.restSeconds = entity.getRestSeconds();
        this.durationMinutes = entity.getDurationMinutes();
        this.caloriesBurned = entity.getCaloriesBurned();
        this.intensity = (entity.getIntensity() != null) ? entity.getIntensity().name() : null;
        this.notes = entity.getNotes();


        if (entity.getComments() != null) {
            this.comments = entity.getComments().stream()
                    .map(CommentResponseDTO::new)
                    .collect(Collectors.toList());
        }
    }



    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Date getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Date sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public Integer getExerciseOrder() {
        return exerciseOrder;
    }

    public void setExerciseOrder(Integer exerciseOrder) {
        this.exerciseOrder = exerciseOrder;
    }

    public Integer getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Integer getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<CommentResponseDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponseDTO> comments) {
        this.comments = comments;
    }
}
