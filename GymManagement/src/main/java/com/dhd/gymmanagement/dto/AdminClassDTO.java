package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.TrainingClass;
import java.sql.Timestamp;

public class AdminClassDTO {
    private Integer classId;
    private String name;
    private String description;
    private String trainerName;
    private String schedule;
    private String startTime;
    private Integer maxParticipants;
    private Integer currentEnrollmentCount;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public AdminClassDTO() {}

    public AdminClassDTO(TrainingClass trainingClass) {
        this.classId = trainingClass.getClassId();
        this.name = trainingClass.getName();
        this.description = trainingClass.getDescription();
        this.schedule = trainingClass.getSchedule();
        this.startTime = trainingClass.getStartTime();
        this.maxParticipants = trainingClass.getMaxParticipants();
        

        if (trainingClass.getTrainer() != null && 
            trainingClass.getTrainer().getUser() != null && 
            trainingClass.getTrainer().getUser().getName() != null) {
            this.trainerName = trainingClass.getTrainer().getUser().getName();
        } else {
            this.trainerName = null;
        }
        

        if (trainingClass.getTrainer() != null) {
            this.status = "Hoạt động";
        } else {
            this.status = "Chưa có PT";
        }
        
        this.createdAt = trainingClass.getCreatedAt();
        this.updatedAt = trainingClass.getUpdatedAt();
    }

    // Getters and Setters
    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getCurrentEnrollmentCount() {
        return currentEnrollmentCount;
    }

    public void setCurrentEnrollmentCount(Integer currentEnrollmentCount) {
        this.currentEnrollmentCount = currentEnrollmentCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
