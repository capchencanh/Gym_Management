package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "plan_details")
public class PlanDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private TrainingPlan trainingPlan;

    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    @Column(name = "exercise_name")
    private String exerciseName;

    @Column
    private Integer sets;

    @Column
    private Integer reps;

    @Column
    private Double weight;

    @Column(name = "rest_seconds")
    private Integer restSeconds = 60;

    @Column(columnDefinition = "text")
    private String notes;

    public Integer getDetailId() { return detailId; }
    public void setDetailId(Integer detailId) { this.detailId = detailId; }
    public TrainingPlan getTrainingPlan() { return trainingPlan; }
    public void setTrainingPlan(TrainingPlan trainingPlan) { this.trainingPlan = trainingPlan; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Integer getRestSeconds() { return restSeconds; }
    public void setRestSeconds(Integer restSeconds) { this.restSeconds = restSeconds; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
