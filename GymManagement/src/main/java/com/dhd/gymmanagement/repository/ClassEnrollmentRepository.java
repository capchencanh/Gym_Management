package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.ClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Integer> {
    
    @Query("SELECT ce FROM ClassEnrollment ce WHERE (ce.isDeleted = 0 OR ce.isDeleted IS NULL) AND ce.trainingClass.classId = :classId")
    List<ClassEnrollment> findByClassId(@Param("classId") Integer classId);
    
    @Query("SELECT ce FROM ClassEnrollment ce WHERE (ce.isDeleted = 0 OR ce.isDeleted IS NULL) AND ce.user.userId = :userId")
    List<ClassEnrollment> findByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT ce FROM ClassEnrollment ce WHERE (ce.isDeleted = 0 OR ce.isDeleted IS NULL) AND ce.trainingClass.classId = :classId AND ce.user.userId = :userId")
    Optional<ClassEnrollment> findByClassIdAndUserId(@Param("classId") Integer classId, @Param("userId") Integer userId);
    
    @Query("SELECT ce FROM ClassEnrollment ce JOIN FETCH ce.user JOIN FETCH ce.trainingClass WHERE (ce.isDeleted = 0 OR ce.isDeleted IS NULL) AND ce.trainingClass.classId = :classId")
    List<ClassEnrollment> findEnrolledUsersByClassId(@Param("classId") Integer classId);
    
    @Query("SELECT COUNT(ce) FROM ClassEnrollment ce WHERE (ce.isDeleted = 0 OR ce.isDeleted IS NULL) AND ce.trainingClass.classId = :classId")
    Long countEnrolledUsersByClassId(@Param("classId") Integer classId);
    
    @Query("SELECT ce FROM ClassEnrollment ce JOIN FETCH ce.user JOIN FETCH ce.trainingClass WHERE ce.isDeleted = 0 AND ce.trainingClass.classId = :classId")
    List<ClassEnrollment> findAllEnrollmentsByClassId(@Param("classId") Integer classId);
}
