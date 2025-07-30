package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "workout_log_comments")
public class WorkoutLogComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "log_id", nullable = false)
    private WorkoutLog workoutLog;

    @ManyToOne
    @JoinColumn(name = "pt_id", nullable = false)
    private User pt;

    @Column(columnDefinition = "text", nullable = false)
    private String comment;

    @Column(name = "created_at")
    private Timestamp createdAt;

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }
    public WorkoutLog getWorkoutLog() { return workoutLog; }
    public void setWorkoutLog(WorkoutLog workoutLog) { this.workoutLog = workoutLog; }
    public User getPt() { return pt; }
    public void setPt(User pt) { this.pt = pt; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
