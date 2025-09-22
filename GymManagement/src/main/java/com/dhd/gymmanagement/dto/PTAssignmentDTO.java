package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.PTAssignment;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.entity.User;

import java.sql.Timestamp;

public class PTAssignmentDTO {
    private Integer assignmentId;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private Integer trainerId;
    private String trainerName;
    private String trainerEmail;
    private String trainerSpecialization;
    private PTAssignment.Status status;
    private String requestNotes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PTAssignmentDTO() {}

    public PTAssignmentDTO(PTAssignment assignment) {
        this.assignmentId = assignment.getAssignmentId();
        this.status = assignment.getStatus();
        this.requestNotes = assignment.getRequestNotes();
        this.createdAt = assignment.getCreatedAt();
        this.updatedAt = assignment.getUpdatedAt();

        if (assignment.getUser() != null) {
            User user = assignment.getUser();
            this.userId = user.getUserId();
            this.userName = user.getName();
            this.userEmail = user.getEmail();
            this.userPhone = user.getPhoneNumber();
        }

        if (assignment.getTrainer() != null) {
            Trainer trainer = assignment.getTrainer();
            this.trainerId = trainer.getTrainerId();
            this.trainerSpecialization = trainer.getSpecialization();
            
            if (trainer.getUser() != null) {
                User trainerUser = trainer.getUser();
                this.trainerName = trainerUser.getName();
                this.trainerEmail = trainerUser.getEmail();
            }
        }
    }


    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer assignmentId) { this.assignmentId = assignmentId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public Integer getTrainerId() { return trainerId; }
    public void setTrainerId(Integer trainerId) { this.trainerId = trainerId; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public String getTrainerEmail() { return trainerEmail; }
    public void setTrainerEmail(String trainerEmail) { this.trainerEmail = trainerEmail; }

    public String getTrainerSpecialization() { return trainerSpecialization; }
    public void setTrainerSpecialization(String trainerSpecialization) { this.trainerSpecialization = trainerSpecialization; }

    public PTAssignment.Status getStatus() { return status; }
    public void setStatus(PTAssignment.Status status) { this.status = status; }

    public String getRequestNotes() { return requestNotes; }
    public void setRequestNotes(String requestNotes) { this.requestNotes = requestNotes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
