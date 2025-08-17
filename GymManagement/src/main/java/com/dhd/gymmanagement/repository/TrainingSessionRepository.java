package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Integer> {
    
    @Query("SELECT ts FROM TrainingSession ts WHERE ts.user.userId = :userId AND ts.isDeleted = 0")
    List<TrainingSession> findByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT ts FROM TrainingSession ts WHERE ts.trainer.trainerId = :trainerId AND ts.isDeleted = 0")
    List<TrainingSession> findByTrainerId(@Param("trainerId") Integer trainerId);
    
    @Query("SELECT ts FROM TrainingSession ts WHERE ts.trainer.trainerId = :trainerId AND ts.sessionDate = :sessionDate AND ts.isDeleted = 0")
    List<TrainingSession> findByTrainerIdAndDate(@Param("trainerId") Integer trainerId, @Param("sessionDate") Date sessionDate);
    
    @Query("SELECT ts FROM TrainingSession ts WHERE ts.user.userId = :userId AND ts.trainer.trainerId = :trainerId AND ts.isDeleted = 0")
    List<TrainingSession> findByUserIdAndTrainerId(@Param("userId") Integer userId, @Param("trainerId") Integer trainerId);
    
    @Query("SELECT ts FROM TrainingSession ts WHERE ts.status = :status AND ts.isDeleted = 0")
    List<TrainingSession> findByStatus(@Param("status") TrainingSession.Status status);
    
    @Query("SELECT ts FROM TrainingSession ts WHERE ts.trainer.trainerId = :trainerId AND ts.sessionDate >= :startDate AND ts.isDeleted = 0 ORDER BY ts.sessionDate, ts.startTime")
    List<TrainingSession> findUpcomingSessionsByTrainer(@Param("trainerId") Integer trainerId, @Param("startDate") Date startDate);
}
