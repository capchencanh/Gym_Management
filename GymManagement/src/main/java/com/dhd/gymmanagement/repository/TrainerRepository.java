package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    
    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.user WHERE t.isDeleted = 0 OR t.isDeleted IS NULL")
    List<Trainer> findAllActiveWithUser();
    
    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.user")
    List<Trainer> findAllWithUser();
    
    List<Trainer> findByUser_NameContainingIgnoreCaseOrUser_EmailContainingIgnoreCaseOrUser_PhoneNumberContainingIgnoreCase(
        String name, String email, String phoneNumber);
    
    List<Trainer> findByUser_IsDeleted(Integer isDeleted);
}
