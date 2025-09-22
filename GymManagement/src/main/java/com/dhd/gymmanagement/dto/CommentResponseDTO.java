package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.WorkoutLogComment;
import java.time.format.DateTimeFormatter;

public class CommentResponseDTO {
    private String ptName;
    private String comment;
    private String createdAt;

    public CommentResponseDTO(WorkoutLogComment commentEntity) {
        this.ptName = commentEntity.getPt().getName();
        this.comment = commentEntity.getComment();
        this.createdAt = commentEntity.getCreatedAt().toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }


    public String getPtName() { return ptName; }
    public void setPtName(String ptName) { this.ptName = ptName; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
