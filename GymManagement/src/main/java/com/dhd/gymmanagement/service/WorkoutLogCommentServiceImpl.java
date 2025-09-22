package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.WorkoutLog;
import com.dhd.gymmanagement.entity.WorkoutLogComment;
import com.dhd.gymmanagement.repository.UserRepository;
import com.dhd.gymmanagement.repository.WorkoutLogCommentRepository;
import com.dhd.gymmanagement.repository.WorkoutLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class WorkoutLogCommentServiceImpl implements WorkoutLogCommentService {

    @Autowired
    private WorkoutLogCommentRepository commentRepository;

    @Autowired
    private WorkoutLogRepository workoutLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public WorkoutLogComment addComment(Integer logId, Integer ptId, String commentText) {

        WorkoutLog workoutLog = workoutLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Workout Log not found with id: " + logId));

        User pt = userRepository.findById(ptId)
                .orElseThrow(() -> new RuntimeException("PT not found with id: " + ptId));

        WorkoutLogComment comment = new WorkoutLogComment();
        comment.setWorkoutLog(workoutLog);
        comment.setPt(pt);
        comment.setComment(commentText);
        comment.setCreatedAt(Timestamp.from(Instant.now()));

        return commentRepository.save(comment);
    }

    @Override
    public List<WorkoutLogComment> getCommentsByLogId(Integer logId) {
        return commentRepository.findByWorkoutLog_LogIdOrderByCreatedAtAsc(logId);
    }
}