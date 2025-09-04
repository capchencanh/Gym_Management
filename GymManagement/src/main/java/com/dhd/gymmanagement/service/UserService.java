package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.entity.UserAvailability;
import com.dhd.gymmanagement.repository.UserRepository;
import com.dhd.gymmanagement.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TrainerRepository trainerRepository;
    
    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        return userRepository.findAllByIsDeleted(0);
    }
    
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllByIsDeleted(0, pageable);
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
    
    public Page<User> getUsersByRole(User.Role role, Pageable pageable) {
        return userRepository.findByRoleAndIsDeletedFalse(role, pageable);
    }
    
    public List<User> searchUsers(String keyword) {
        return userRepository.findByKeyword(keyword);
    }
    
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        return userRepository.findByKeywordAndIsDeletedFalse(keyword, pageable);
    }
    
    public List<User> searchUsersByRole(User.Role role, String keyword) {
        return userRepository.findByRoleAndKeywordAndIsDeletedFalse(role, keyword);
    }
    
    public Page<User> searchUsersByRole(User.Role role, String keyword, Pageable pageable) {
        return userRepository.findByRoleAndKeywordAndIsDeletedFalse(role, keyword, pageable);
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
        return userProfileService.updateUser(userId, userDetails);
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
        return userProfileService.save(user);
    }
    
    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
    
    public void updatePassword(User user, String newPassword) {
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
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
    
    public void updateAvatar(String email, MultipartFile avatarFile) throws IOException {
        userProfileService.updateAvatar(email, avatarFile);
    }
    
    public Trainer findTrainerByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null || user.getIsDeleted() == 1 || !User.Role.PT.equals(user.getRole())) {
            return null;
        }
        
        return trainerRepository.findById(user.getUserId()).orElse(null);
    }
    
    public List<UserAvailability> getUserAvailabilities(Integer userId) {
        return userProfileService.getUserAvailabilities(userId);
    }
    
    public UserAvailability createUserAvailability(Integer userId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return userProfileService.createUserAvailability(userId, dayOfWeek, startTime, endTime);
    }
    
    public UserAvailability updateUserAvailability(Integer availabilityId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Boolean isAvailable) {
        return userProfileService.updateUserAvailability(availabilityId, dayOfWeek, startTime, endTime, isAvailable);
    }
    
    public void deleteUserAvailability(Integer availabilityId) {
        userProfileService.deleteUserAvailability(availabilityId);
    }
    
    public List<java.util.Map<String, Object>> getUserMonthlyData() {
        List<java.util.Map<String, Object>> monthlyData = new java.util.ArrayList<>();
        
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusMonths(i);
            String monthName = date.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.forLanguageTag("vi"));
            
            java.util.Map<String, Object> monthData = new java.util.HashMap<>();
            monthData.put("month", monthName);
            
            long userCount = userRepository.countByCreatedAtMonth(date.getYear(), date.getMonthValue());
            monthData.put("count", userCount);
            
            monthlyData.add(monthData);
        }
        
        return monthlyData;
    }
}
