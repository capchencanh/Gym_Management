package com.dhd.gymmanagement.service.impl;

import com.dhd.gymmanagement.entity.UserMembership;
import com.dhd.gymmanagement.repository.UserMembershipRepository;
import com.dhd.gymmanagement.service.UserMembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserMembershipServiceImpl implements UserMembershipService {

    @Autowired
    private UserMembershipRepository userMembershipRepository;

    @Override
    public List<UserMembership> getAllUserMemberships() {
        return userMembershipRepository.findByIsDeleted(0);
    }

    @Override
    public Optional<UserMembership> getUserMembershipById(Integer membershipId) {
        return userMembershipRepository.findByMembershipIdAndIsDeleted(membershipId, 0);
    }

    @Override
    public List<UserMembership> getUserMembershipsByUserId(Integer userId) {
        return userMembershipRepository.findByUserIdAndIsDeleted(userId, 0);
    }

    @Override
    public List<UserMembership> getActiveMembershipsByUserId(Integer userId) {
        List<UserMembership> memberships = userMembershipRepository.findByUserIdAndIsDeleted(userId, 0);
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        return memberships.stream()
                .filter(m -> m.getStatus() == UserMembership.MembershipStatus.ACTIVE && m.getEndDate() != null && m.getEndDate().after(now))
                .toList();
    }

    @Override
    public UserMembership createUserMembership(UserMembership userMembership) {
        userMembership.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userMembership.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userMembership.setIsDeleted(0);
        return userMembershipRepository.save(userMembership);
    }

    @Override
    public UserMembership updateUserMembership(UserMembership userMembership) {
        userMembership.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userMembershipRepository.save(userMembership);
    }

    @Override
    public boolean deleteUserMembership(Integer membershipId) {
        Optional<UserMembership> membershipOpt = getUserMembershipById(membershipId);
        if (membershipOpt.isPresent()) {
            UserMembership membership = membershipOpt.get();
            membership.setIsDeleted(1);
            membership.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userMembershipRepository.save(membership);
            return true;
        }
        return false;
    }
    
    @Override
    public List<java.util.Map<String, Object>> getExpiredMembershipsByPackageId(Integer packageId) {
        List<java.util.Map<String, Object>> expiredMemberships = new java.util.ArrayList<>();
        
        // Lấy tất cả membership của package này
        List<UserMembership> memberships = userMembershipRepository.findByPackageIdAndIsDeleted(packageId, 0);
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        
        for (UserMembership membership : memberships) {
            // Kiểm tra nếu gói đã hết hạn
            if (membership.getEndDate() != null && membership.getEndDate().before(now)) {
                java.util.Map<String, Object> expiredData = new java.util.HashMap<>();
                expiredData.put("userId", membership.getUserId());
                // Chuyển timestamp thành string để tránh vấn đề serialize
                expiredData.put("endDate", membership.getEndDate().toString());
                expiredData.put("daysExpired", (now.getTime() - membership.getEndDate().getTime()) / (1000 * 60 * 60 * 24));
                expiredMemberships.add(expiredData);
            }
        }
        
        return expiredMemberships;
    }
    
    @Override
    public boolean renewMembership(Integer userId, Integer packageId, Integer months) {
        try {
            List<UserMembership> memberships = userMembershipRepository.findByUserIdAndPackageIdAndIsDeleted(userId, packageId, 0);
            
            if (memberships.isEmpty()) {
                return false;
            }
            
            UserMembership latestMembership = memberships.stream()
                    .filter(m -> m.getStatus() == UserMembership.MembershipStatus.ACTIVE)
                    .sorted((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()))
                    .findFirst()
                    .orElse(null);
            
            if (latestMembership == null) {
                UserMembership newMembership = new UserMembership();
                newMembership.setUserId(userId);
                newMembership.setPackageId(packageId);
                newMembership.setStartDate(new java.sql.Timestamp(System.currentTimeMillis()));
                
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime newEnd = now.plusMonths(months);
                newMembership.setEndDate(java.sql.Timestamp.valueOf(newEnd));
                
                newMembership.setStatus(UserMembership.MembershipStatus.ACTIVE);
                newMembership.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                newMembership.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                newMembership.setIsDeleted(0);
                
                userMembershipRepository.save(newMembership);
                return true;
            }
            
            java.sql.Timestamp newEndDate;
            if (latestMembership.getEndDate() != null) {
                java.time.LocalDateTime currentEnd = latestMembership.getEndDate().toLocalDateTime();
                java.time.LocalDateTime newEnd = currentEnd.plusMonths(months);
                newEndDate = java.sql.Timestamp.valueOf(newEnd);
            } else {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime newEnd = now.plusMonths(months);
                newEndDate = java.sql.Timestamp.valueOf(newEnd);
            }
            
            latestMembership.setEndDate(newEndDate);
            latestMembership.setStatus(UserMembership.MembershipStatus.ACTIVE);
            latestMembership.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            
            userMembershipRepository.save(latestMembership);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
