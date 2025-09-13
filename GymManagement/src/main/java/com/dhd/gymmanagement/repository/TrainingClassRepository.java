package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.TrainingClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingClassRepository extends JpaRepository<TrainingClass, Integer> {
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0")
    List<TrainingClass> findAllActive();
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0")
    Page<TrainingClass> findAllActive(Pageable pageable);
    
    @Query("SELECT tc FROM TrainingClass tc LEFT JOIN FETCH tc.trainer t LEFT JOIN FETCH t.user u WHERE tc.isDeleted = 0")
    List<TrainingClass> findAllActiveWithTrainerAndUser();
    @Query(value = "SELECT tc FROM TrainingClass tc LEFT JOIN tc.trainer t LEFT JOIN t.user u WHERE tc.isDeleted = 0",
           countQuery = "SELECT COUNT(tc) FROM TrainingClass tc WHERE tc.isDeleted = 0")
    Page<TrainingClass> findAllActiveWithTrainerAndUser(Pageable pageable);
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer.trainerId = :trainerId")
    List<TrainingClass> findByTrainerId(@Param("trainerId") Integer trainerId);
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer.trainerId = :trainerId")
    Page<TrainingClass> findByTrainerId(@Param("trainerId") Integer trainerId, Pageable pageable);
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.name LIKE %:name%")
    List<TrainingClass> findByNameContaining(@Param("name") String name);
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.name LIKE %:name%")
    Page<TrainingClass> findByNameContaining(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer IS NULL")
    List<TrainingClass> findClassesWithoutTrainer();
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer IS NULL")
    Page<TrainingClass> findClassesWithoutTrainer(Pageable pageable);
    
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer IS NOT NULL")
    List<TrainingClass> findClassesWithTrainer();
    @Query("SELECT tc FROM TrainingClass tc WHERE tc.isDeleted = 0 AND tc.trainer IS NOT NULL")
    Page<TrainingClass> findClassesWithTrainer(Pageable pageable);
    
    @Query("SELECT COUNT(ce) FROM ClassEnrollment ce WHERE ce.trainingClass.classId = :classId AND (ce.isDeleted = 0 OR ce.isDeleted IS NULL)")
    Long countEnrolledUsers(@Param("classId") Integer classId);
    
    long countByIsDeleted(int isDeleted);
}
