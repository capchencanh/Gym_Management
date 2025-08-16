package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.WorkoutLog;
import com.dhd.gymmanagement.repository.WorkoutLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class WorkoutLogService {

    @Autowired
    private WorkoutLogRepository workoutLogRepository;

    public List<WorkoutLog> findByUserId(Integer userId) {
        return workoutLogRepository.findByUser_UserIdAndIsDeleted(userId, 0);
    }

    public Optional<WorkoutLog> findById(Integer logId) {
        return workoutLogRepository.findById(logId);
    }

    public WorkoutLog save(WorkoutLog workoutLog) {
        if (workoutLog.getCreatedAt() == null) {
            workoutLog.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        return workoutLogRepository.save(workoutLog);
    }

    public void deleteById(Integer logId) {
        Optional<WorkoutLog> logOpt = workoutLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            WorkoutLog log = logOpt.get();
            log.setIsDeleted(1);
            workoutLogRepository.save(log);
        }
    }

    public List<WorkoutLog> findByUserIdAndDateRange(Integer userId, java.sql.Date startDate, java.sql.Date endDate) {
        return workoutLogRepository.findByUser_UserIdAndSessionDateBetweenAndIsDeleted(userId, startDate, endDate, 0);
    }
}
