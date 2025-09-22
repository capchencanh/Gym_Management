package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.WorkoutLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Integer> {

    List<WorkoutLog> findByUser_UserIdAndIsDeleted(Integer userId, Integer isDeleted);

    List<WorkoutLog> findByUser_UserIdAndSessionDateBetweenAndIsDeleted(Integer userId, Date startDate, Date endDate, Integer isDeleted);

    @Query("SELECT wl FROM WorkoutLog wl WHERE wl.user.userId = :userId AND wl.sessionDate = :sessionDate AND wl.isDeleted = 0")
    List<WorkoutLog> findByUserIdAndSessionDate(@Param("userId") Integer userId, @Param("sessionDate") Date sessionDate);

    @Query("SELECT wl FROM WorkoutLog wl WHERE wl.user.userId = :userId AND wl.workoutType = :workoutType AND wl.isDeleted = 0 ORDER BY wl.sessionDate DESC")
    List<WorkoutLog> findByUserIdAndWorkoutType(@Param("userId") Integer userId, @Param("workoutType") String workoutType);
}
