package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.PTAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PTAssignmentRepository extends JpaRepository<PTAssignment, Integer> {
    
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.status = :status AND pa.isDeleted = 0")
    List<PTAssignment> findByStatus(@Param("status") PTAssignment.Status status);
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.status = :status AND pa.isDeleted = 0")
    Page<PTAssignment> findByStatus(@Param("status") PTAssignment.Status status, Pageable pageable);
    
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.user.userId = :userId AND pa.isDeleted = 0")
    List<PTAssignment> findByUserId(@Param("userId") Integer userId);
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.user.userId = :userId AND pa.isDeleted = 0")
    Page<PTAssignment> findByUserId(@Param("userId") Integer userId, Pageable pageable);
    
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.trainer.trainerId = :trainerId AND pa.isDeleted = 0")
    List<PTAssignment> findByTrainerId(@Param("trainerId") Integer trainerId);
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.trainer.trainerId = :trainerId AND pa.isDeleted = 0")
    Page<PTAssignment> findByTrainerId(@Param("trainerId") Integer trainerId, Pageable pageable);
    
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.status = 'PENDING' AND pa.isDeleted = 0")
    List<PTAssignment> findPendingAssignments();
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.status = 'PENDING' AND pa.isDeleted = 0")
    Page<PTAssignment> findPendingAssignments(Pageable pageable);
    
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.status = 'ASSIGNED' AND pa.isDeleted = 0")
    List<PTAssignment> findAssignedAssignments();
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.status = 'ASSIGNED' AND pa.isDeleted = 0")
    Page<PTAssignment> findAssignedAssignments(Pageable pageable);
    
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.status = 'ACTIVE' AND pa.isDeleted = 0")
    List<PTAssignment> findActiveAssignments();
    @Query("SELECT pa FROM PTAssignment pa WHERE pa.status = 'ACTIVE' AND pa.isDeleted = 0")
    Page<PTAssignment> findActiveAssignments(Pageable pageable);
}
