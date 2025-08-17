package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Date;
import java.time.LocalTime;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

                    @ManyToOne
                @JoinColumn(name = "user_id", nullable = false)
                private User user;

                @ManyToOne
                @JoinColumn(name = "trainer_id", nullable = false)
                private Trainer trainer;

    @Column(name = "session_date", nullable = false)
    private Date sessionDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.SCHEDULED;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "is_deleted")
    private Integer isDeleted = 0;

    public enum Status {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    // Getters and Setters
    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

                    public User getUser() { return user; }
                public void setUser(User user) { this.user = user; }

                public Trainer getTrainer() { return trainer; }
                public void setTrainer(Trainer trainer) { this.trainer = trainer; }

    public Date getSessionDate() { return sessionDate; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
