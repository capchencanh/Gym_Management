package com.dhd.gymmanagement.dto;

import com.dhd.gymmanagement.entity.ClassEnrollment;
import java.sql.Timestamp;

public class ClassEnrollmentDTO {
    private Integer enrollmentId;
    private Integer userId;
    private Integer classId;
    private String className;
    private Timestamp joinedAt;
    private Boolean attendance;
    private Timestamp checkInTime;
    private String status;
    private Integer isDeleted;

    public ClassEnrollmentDTO() {}

    public ClassEnrollmentDTO(ClassEnrollment enrollment) {
        this.enrollmentId = enrollment.getEnrollmentId();
        this.userId = enrollment.getUser() != null ? enrollment.getUser().getUserId() : null;
        this.classId = enrollment.getTrainingClass() != null ? enrollment.getTrainingClass().getClassId() : null;
        this.className = enrollment.getTrainingClass() != null ? enrollment.getTrainingClass().getName() : null;
        this.joinedAt = enrollment.getJoinedAt();
        this.attendance = enrollment.getAttendance();
        this.checkInTime = enrollment.getCheckInTime();
        this.status = enrollment.getStatus().name();
        this.isDeleted = enrollment.getIsDeleted();
    }


    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Boolean getAttendance() {
        return attendance;
    }

    public void setAttendance(Boolean attendance) {
        this.attendance = attendance;
    }

    public Timestamp getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Timestamp checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
