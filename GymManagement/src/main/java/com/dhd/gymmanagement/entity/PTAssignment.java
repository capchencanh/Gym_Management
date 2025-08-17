package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "pt_assignments")
public class PTAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Integer assignmentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = true)
    private Trainer trainer;

                    @Enumerated(EnumType.STRING)
                @Column(name = "status", nullable = false)
                private Status status = Status.PENDING;

                @Column(name = "request_notes", columnDefinition = "text")
                private String requestNotes; // Ghi chú từ user khi yêu cầu PT

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    public enum Status {
        PENDING,
        ASSIGNED,
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    // Getters and Setters
    public Integer getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Integer assignmentId) { this.assignmentId = assignmentId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Trainer getTrainer() { return trainer; }
    public void setTrainer(Trainer trainer) { this.trainer = trainer; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

                    public Integer getIsDeleted() { return isDeleted; }
                public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

                public String getRequestNotes() { return requestNotes; }
                public void setRequestNotes(String requestNotes) { this.requestNotes = requestNotes; }
}
