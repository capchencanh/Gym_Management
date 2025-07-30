package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "class_enrollments")
public class ClassEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Integer enrollmentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private TrainingClass trainingClass;

    @Column(name = "joined_at")
    private Timestamp joinedAt;

    @Column
    private Boolean attendance = false;

    @Column(name = "check_in_time")
    private Timestamp checkInTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ENROLLED;

    public enum Status {
        ENROLLED, COMPLETED, CANCELLED
    }

    public Integer getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Integer enrollmentId) { this.enrollmentId = enrollmentId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public TrainingClass getTrainingClass() { return trainingClass; }
    public void setTrainingClass(TrainingClass trainingClass) { this.trainingClass = trainingClass; }
    public Timestamp getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }
    public Boolean getAttendance() { return attendance; }
    public void setAttendance(Boolean attendance) { this.attendance = attendance; }
    public Timestamp getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Timestamp checkInTime) { this.checkInTime = checkInTime; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
