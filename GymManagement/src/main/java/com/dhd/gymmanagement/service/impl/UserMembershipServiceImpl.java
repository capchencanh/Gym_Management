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
}
