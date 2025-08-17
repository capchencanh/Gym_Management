package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.UserAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface UserAvailabilityRepository extends JpaRepository<UserAvailability, Integer> {
    
    @Query("SELECT ua FROM UserAvailability ua WHERE ua.user.userId = :userId AND ua.isDeleted = 0")
    List<UserAvailability> findByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT ua FROM UserAvailability ua WHERE ua.user.userId = :userId AND ua.dayOfWeek = :dayOfWeek AND ua.isDeleted = 0")
    List<UserAvailability> findByUserIdAndDayOfWeek(@Param("userId") Integer userId, @Param("dayOfWeek") DayOfWeek dayOfWeek);
    
    @Query("SELECT ua FROM UserAvailability ua WHERE ua.user.userId = :userId AND ua.isAvailable = true AND ua.isDeleted = 0")
    List<UserAvailability> findAvailableByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT ua FROM UserAvailability ua WHERE ua.isAvailable = true AND ua.isDeleted = 0")
    List<UserAvailability> findAllAvailable();
}
