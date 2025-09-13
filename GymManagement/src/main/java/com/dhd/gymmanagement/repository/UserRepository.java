package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.isDeleted = 0")
    boolean existsByEmailAndIsDeletedFalse(@Param("email") String email);
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.phoneNumber = :phoneNumber AND u.isDeleted = 0")
    boolean existsByPhoneNumberAndIsDeletedFalse(@Param("phoneNumber") String phoneNumber);
    
    List<User> findByRole(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isDeleted = 0")
    List<User> findByRoleAndIsDeletedFalse(@Param("role") User.Role role);
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isDeleted = 0")
    Page<User> findByRoleAndIsDeletedFalse(@Param("role") User.Role role, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isDeleted = 0 AND (u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%)")
    List<User> findByRoleAndKeywordAndIsDeletedFalse(@Param("role") User.Role role, @Param("keyword") String keyword);
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isDeleted = 0 AND (u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%)")
    Page<User> findByRoleAndKeywordAndIsDeletedFalse(@Param("role") User.Role role, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%")
    List<User> findByKeyword(@Param("keyword") String keyword);
    @Query("SELECT u FROM User u WHERE (u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%) AND u.isDeleted = 0")
    Page<User> findByKeywordAndIsDeletedFalse(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND (u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phoneNumber LIKE %:keyword%)")
    List<User> findByRoleAndKeyword(@Param("role") User.Role role, @Param("keyword") String keyword);

    List<User> findAllByIsDeleted(int isDeleted);
    Page<User> findAllByIsDeleted(int isDeleted, Pageable pageable);
    
    long countByIsDeleted(int isDeleted);
    
    long countByRole(User.Role role);
    
    long countByRoleAndIsDeletedFalse(User.Role role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE YEAR(u.createdAt) = :year AND MONTH(u.createdAt) = :month AND u.isDeleted = 0")
    long countByCreatedAtMonth(@Param("year") int year, @Param("month") int month);
}
