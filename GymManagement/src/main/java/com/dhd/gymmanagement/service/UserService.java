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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TrainerRepository trainerRepository;
    
    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    private Counter userCreatedCounter;
    private Counter userDeletedCounter;
    private Counter passwordChangedCounter;
    private Counter passwordResetCounter;
    
    @Autowired
    public void setMeterRegistry(MeterRegistry meterRegistry) {
        // Initialize metrics counters
        this.userCreatedCounter = Counter.builder("user.events")
                .tag("event", "created")
                .description("Số lượng người dùng được tạo")
                .register(meterRegistry);
        this.userDeletedCounter = Counter.builder("user.events")
                .tag("event", "deleted")
                .description("Số lượng người dùng bị đánh dấu xóa")
                .register(meterRegistry);
        this.passwordChangedCounter = Counter.builder("user.password.events")
                .tag("event", "changed")
                .description("Số lần đổi mật khẩu thành công")
                .register(meterRegistry);
        this.passwordResetCounter = Counter.builder("user.password.events")
                .tag("event", "reset")
                .description("Số lần reset mật khẩu")
                .register(meterRegistry);
    }
    
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
        User saved = userRepository.save(user);
        // Audit logging
        log.info("{\"audit\":true,\"action\":\"USER_CREATED\",\"user_id\":{},\"email\":\"{}\"}", saved.getUserId(), saved.getEmail());
        if (userCreatedCounter != null) userCreatedCounter.increment();
        return saved;
    }
    
    public User updateUser(Integer userId, User userDetails) {
        return userProfileService.updateUser(userId, userDetails);
    }
    
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        user.setIsDeleted(1);
        userRepository.save(user);
        log.info("{\"audit\":true,\"action\":\"USER_DELETED\",\"user_id\":{},\"email\":\"{}\"}", user.getUserId(), user.getEmail());
        if (userDeletedCounter != null) userDeletedCounter.increment();
        
        if (user.getRole() == User.Role.PT) {
            try {
                Trainer trainer = trainerRepository.findById(userId).orElse(null);
                if (trainer != null) {
                    trainer.setIsDeleted(1);
                    trainerRepository.save(trainer);
                    log.info("{\"audit\":true,\"action\":\"TRAINER_DELETED\",\"trainer_id\":{}}", trainer.getTrainerId());
                }
            } catch (Exception e) {
                log.warn("Lỗi xóa trainer khi xóa user_id={}: {}", userId, e.getMessage());
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
        log.info("{\"audit\":true,\"action\":\"PASSWORD_UPDATED\",\"user_id\":{},\"email\":\"{}\"}", user.getUserId(), user.getEmail());
        if (passwordChangedCounter != null) passwordChangedCounter.increment();
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
        log.info("{\"audit\":true,\"action\":\"PASSWORD_CHANGED\",\"user_id\":{},\"email\":\"{}\"}", user.getUserId(), user.getEmail());
        if (passwordChangedCounter != null) passwordChangedCounter.increment();
        return true;
    }
    
    public void resetPassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        userRepository.save(user);
        log.info("{\"audit\":true,\"action\":\"PASSWORD_RESET\",\"user_id\":{},\"email\":\"{}\"}", user.getUserId(), user.getEmail());
        if (passwordResetCounter != null) passwordResetCounter.increment();
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
