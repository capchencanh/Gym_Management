package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.UserAvailability;
import com.dhd.gymmanagement.repository.UserRepository;
import com.dhd.gymmanagement.repository.UserAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.repository.TrainerRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private TrainerRepository trainerRepository;
    
    @Autowired
    private UserAvailabilityRepository userAvailabilityRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAllByIsDeleted(0);
    }
    
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRoleAndIsDeletedFalse(role);
    }
    
    public List<User> searchUsers(String keyword) {
        return userRepository.findByKeyword(keyword);
    }
    
    public List<User> searchUsersByRole(User.Role role, String keyword) {
        return userRepository.findByRoleAndKeywordAndIsDeletedFalse(role, keyword);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        return userRepository.save(user);
    }
    
    public User updateUser(Integer userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (!user.getPhoneNumber().equals(userDetails.getPhoneNumber()) && 
            userRepository.existsByPhoneNumber(userDetails.getPhoneNumber())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setGender(userDetails.getGender());
        user.setBirthdate(userDetails.getBirthdate());
        user.setHeight(userDetails.getHeight());
        user.setWeight(userDetails.getWeight());
        user.setFitnessGoal(userDetails.getFitnessGoal());
        user.setRole(userDetails.getRole());
        
        if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }
        
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setIsDeleted(1);
        userRepository.save(user);
        

        if (user.getRole() == User.Role.PT) {
            try {
                Trainer trainer = trainerRepository.findById(userId).orElse(null);
                if (trainer != null) {
                    trainer.setIsDeleted(1);
                    trainerRepository.save(trainer);
                }
            } catch (Exception e) {

            }
        }
    }
    
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return false;
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        
        return true;
    }
    
    public void resetPassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        userRepository.save(user);
    }
    
    public long countUsers() {
        return userRepository.countByIsDeleted(0);
    }
    
    public long countUsersByRole(User.Role role) {

        return userRepository.countByRole(role);
    }
    
    public long countActiveUsersByRole(User.Role role) {

        return userRepository.countByRoleAndIsDeletedFalse(role);
    }
    

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    public User save(User user) {
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }
    
    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
    
    public void updatePassword(User user, String newPassword) {
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
    }
    

    public UserAvailability createUserAvailability(Integer userId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        User user = getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        UserAvailability availability = new UserAvailability();
        availability.setUser(user);
        availability.setDayOfWeek(dayOfWeek);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setIsAvailable(true);
        
        return userAvailabilityRepository.save(availability);
    }
    

    public List<UserAvailability> getUserAvailabilities(Integer userId) {
        return userAvailabilityRepository.findByUserId(userId);
    }
    

    public UserAvailability updateUserAvailability(Integer availabilityId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Boolean isAvailable) {
        UserAvailability availability = userAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy availability"));
        
        availability.setDayOfWeek(dayOfWeek);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setIsAvailable(isAvailable);
        
        return userAvailabilityRepository.save(availability);
    }
    

    public void deleteUserAvailability(Integer availabilityId) {
        UserAvailability availability = userAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy availability"));
        
        availability.setIsDeleted(1);
        userAvailabilityRepository.save(availability);
    }
    

    public Trainer findTrainerByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null || user.getIsDeleted() == 1 || !User.Role.PT.equals(user.getRole())) {
            return null;
        }
        

        return trainerRepository.findById(user.getUserId()).orElse(null);
    }
    

}
