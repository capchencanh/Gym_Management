package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserUserId(Integer userId);
    
    @Modifying
    @Query(value = "DELETE FROM password_reset_tokens WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserUserId(@Param("userId") Integer userId);
    
    @Modifying
    @Query(value = "DELETE FROM password_reset_tokens WHERE user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Integer userId);
}
