package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.UserAvailability;
import com.dhd.gymmanagement.repository.UserRepository;
import com.dhd.gymmanagement.repository.UserAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
public class UserProfileService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAvailabilityRepository userAvailabilityRepository;
    
    @Autowired
    private Cloudinary cloudinary;
    
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
        
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        return userRepository.save(user);
    }
    
    public User save(User user) {
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }
    
    public void updateAvatar(String email, MultipartFile avatarFile) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new RuntimeException("Tập tin ảnh không được để trống.");
        }

        if (user.getAvatarUrl() != null && user.getAvatarUrl().contains("cloudinary.com")) {
            try {
                String[] urlParts = user.getAvatarUrl().split("/");
                String fileName = urlParts[urlParts.length - 1];
                String publicId = "user_avatars/" + fileName.substring(0, fileName.lastIndexOf("."));
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                System.err.println("Không thể xóa ảnh đại diện cũ: " + e.getMessage());
            }
        }

        // Upload new avatar
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(
                avatarFile.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "auto",
                        "folder", "user_avatars"
                ));

        String newAvatarUrl = (String) uploadResult.get("secure_url");
        user.setAvatarUrl(newAvatarUrl);
        save(user);
    }
    
    public UserAvailability createUserAvailability(Integer userId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        User user = userRepository.findById(userId)
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
}
