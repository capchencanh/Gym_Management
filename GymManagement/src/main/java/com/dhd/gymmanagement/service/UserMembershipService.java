package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.UserMembership;
import java.util.List;
import java.util.Optional;

public interface UserMembershipService {
    List<UserMembership> getAllUserMemberships();
    Optional<UserMembership> getUserMembershipById(Integer membershipId);
    List<UserMembership> getUserMembershipsByUserId(Integer userId);
    UserMembership createUserMembership(UserMembership userMembership);
    UserMembership updateUserMembership(UserMembership userMembership);
    boolean deleteUserMembership(Integer membershipId);
    List<UserMembership> getActiveMembershipsByUserId(Integer userId);
    
    List<java.util.Map<String, Object>> getExpiredMembershipsByPackageId(Integer packageId);
    
    boolean renewMembership(Integer userId, Integer packageId, Integer months);
}
