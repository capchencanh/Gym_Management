package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    List<User> findByRole(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%")
    List<User> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND (u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%)")
    List<User> findByRoleAndKeyword(@Param("role") User.Role role, @Param("keyword") String keyword);

    List<User> findAllByIsDeleted(int isDeleted);
}
