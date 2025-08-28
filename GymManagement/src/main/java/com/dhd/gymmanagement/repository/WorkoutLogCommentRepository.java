package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.WorkoutLogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkoutLogCommentRepository extends JpaRepository<WorkoutLogComment, Integer> {


    List<WorkoutLogComment> findByWorkoutLog_LogIdOrderByCreatedAtAsc(Integer logId);
}