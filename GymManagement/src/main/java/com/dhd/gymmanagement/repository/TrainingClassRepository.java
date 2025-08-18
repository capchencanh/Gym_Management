package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.TrainingClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingClassRepository extends JpaRepository<TrainingClass, Integer> {
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0")
    List<TrainingClass> findAllActive();
    
    @Query("SELECT tc FROM TrainingClass tc LEFT JOIN FETCH tc.trainer t LEFT JOIN FETCH t.user WHERE tc.isDeleted = 0")
    List<TrainingClass> findAllActiveWithTrainerAndUser();
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer.trainerId = :trainerId")
    List<TrainingClass> findByTrainerId(@Param("trainerId") Integer trainerId);
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.name LIKE %:name%")
    List<TrainingClass> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer IS NULL")
    List<TrainingClass> findClassesWithoutTrainer();
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer IS NOT NULL")
    List<TrainingClass> findClassesWithTrainer();
    
    @Query("SELECT COUNT(ce) FROM ClassEnrollment ce WHERE ce.trainingClass.classId = :classId AND ce.isDeleted = 0 AND ce.status = 'ENROLLED'")
    Long countEnrolledUsers(@Param("classId") Integer classId);
}
