package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.TrainingSession;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.Trainer;

import java.sql.Date;
import java.time.LocalTime;

public class TrainingSessionDTO {
    private Integer sessionId;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Integer trainerId;
    private String trainerName;
    private Date sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private TrainingSession.Status status;
    private String notes;
    private Integer isDeleted;

    public TrainingSessionDTO() {}

    public TrainingSessionDTO(TrainingSession session) {
        this.sessionId = session.getSessionId();
        this.sessionDate = session.getSessionDate();
        this.startTime = session.getStartTime();
        this.endTime = session.getEndTime();
        this.status = session.getStatus();
        this.notes = session.getNotes();
        this.isDeleted = session.getIsDeleted();

        if (session.getUser() != null) {
            User user = session.getUser();
            this.userId = user.getUserId();
            this.userName = user.getName();
            this.userEmail = user.getEmail();
        }

        if (session.getTrainer() != null) {
            Trainer trainer = session.getTrainer();
            this.trainerId = trainer.getTrainerId();
            
            if (trainer.getUser() != null) {
                User trainerUser = trainer.getUser();
                this.trainerName = trainerUser.getName();
            }
        }
    }

    // Getters and Setters
    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Integer getTrainerId() { return trainerId; }
    public void setTrainerId(Integer trainerId) { this.trainerId = trainerId; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public TrainingSession.Status getStatus() { return status; }
    public void setStatus(TrainingSession.Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
