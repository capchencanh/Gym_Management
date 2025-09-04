package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Integer> {
    
    List<UserMembership> findByIsDeleted(Integer isDeleted);
    
    Optional<UserMembership> findByMembershipIdAndIsDeleted(Integer membershipId, Integer isDeleted);
    
    List<UserMembership> findByUserIdAndIsDeleted(Integer userId, Integer isDeleted);
    
    List<UserMembership> findByPackageIdAndIsDeleted(Integer packageId, Integer isDeleted);
    
    List<UserMembership> findByUserIdAndPackageIdAndIsDeleted(Integer userId, Integer packageId, Integer isDeleted);
}
