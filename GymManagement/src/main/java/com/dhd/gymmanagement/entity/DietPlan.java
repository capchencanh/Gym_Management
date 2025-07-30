package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "diet_plans")
public class DietPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diet_plan_id")
    private Integer dietPlanId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plan_name")
    private String planName;

    @Column(name = "calories_target")
    private Integer caloriesTarget;

    @Column(name = "protein_target")
    private Integer proteinTarget;

    @Column(name = "carb_target")
    private Integer carbTarget;

    @Column(name = "fat_target")
    private Integer fatTarget;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public enum Status {
        ACTIVE, COMPLETED, PAUSED
    }

    public Integer getDietPlanId() { return dietPlanId; }
    public void setDietPlanId(Integer dietPlanId) { this.dietPlanId = dietPlanId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public Integer getCaloriesTarget() { return caloriesTarget; }
    public void setCaloriesTarget(Integer caloriesTarget) { this.caloriesTarget = caloriesTarget; }
    public Integer getProteinTarget() { return proteinTarget; }
    public void setProteinTarget(Integer proteinTarget) { this.proteinTarget = proteinTarget; }
    public Integer getCarbTarget() { return carbTarget; }
    public void setCarbTarget(Integer carbTarget) { this.carbTarget = carbTarget; }
    public Integer getFatTarget() { return fatTarget; }
    public void setFatTarget(Integer fatTarget) { this.fatTarget = fatTarget; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
