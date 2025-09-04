package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.UserMembership;
import com.dhd.gymmanagement.service.UserMembershipService;
import com.dhd.gymmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMembershipService userMembershipService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa xác thực");
            }

            String email = authentication.getName();
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user");
            }

            if (user.getIsDeleted() == 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản đã bị xóa");
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("user_id", user.getUserId());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("role", user.getRole().name());
            userInfo.put("phone_number", user.getPhoneNumber());
            userInfo.put("gender", user.getGender());
            userInfo.put("birthdate", user.getBirthdate());
            userInfo.put("height", user.getHeight());
            userInfo.put("weight", user.getWeight());
            userInfo.put("fitness_goal", user.getFitnessGoal());
            userInfo.put("avatar_url", user.getAvatarUrl());

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @GetMapping("/memberships/active")
    public ResponseEntity<?> getActiveMemberships(@RequestParam Integer userId) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body("userId is required");
            }
            java.util.List<UserMembership> active = userMembershipService.getActiveMembershipsByUserId(userId);
            java.util.List<java.util.Map<String, Object>> data = new java.util.ArrayList<>();
            for (UserMembership m : active) {
                java.util.Map<String, Object> item = new java.util.HashMap<>();
                item.put("membership_id", m.getMembershipId());
                item.put("package_id", m.getPackageId());
                item.put("start_date", m.getStartDate());
                item.put("end_date", m.getEndDate());
                data.add(item);
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa xác thực");
            }

            String email = authentication.getName();
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user");
            }

            if (updateRequest.containsKey("name")) {
                user.setName((String) updateRequest.get("name"));
            }
            if (updateRequest.containsKey("phone_number")) {
                user.setPhoneNumber((String) updateRequest.get("phone_number"));
            }
            if (updateRequest.containsKey("gender")) {
                user.setGender((String) updateRequest.get("gender"));
            }
            if (updateRequest.containsKey("birthdate")) {
                user.setBirthdate(java.sql.Date.valueOf((String) updateRequest.get("birthdate")));
            }
            if (updateRequest.containsKey("height")) {
                user.setHeight((Double) updateRequest.get("height"));
            }
            if (updateRequest.containsKey("weight")) {
                user.setWeight((Double) updateRequest.get("weight"));
            }
            if (updateRequest.containsKey("fitness_goal")) {
                user.setFitnessGoal((String) updateRequest.get("fitness_goal"));
            }

            User updatedUser = userService.updateUser(user.getUserId(), user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cập nhật profile thành công");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("avatarFile") MultipartFile avatarFile) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa xác thực");
            }

            String email = authentication.getName();
            userService.updateAvatar(email, avatarFile);

            User updatedUser = userService.getUserByEmail(email).orElse(null);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cập nhật ảnh đại diện thành công");
            response.put("avatar_url", updatedUser.getAvatarUrl());

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi tải ảnh lên: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa xác thực");
            }

            String email = authentication.getName();
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user");
            }

            String currentPassword = passwordRequest.get("currentPassword");
            String newPassword = passwordRequest.get("newPassword");

            if (!userService.checkPassword(user, currentPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu hiện tại không chính xác");
            }

            if (newPassword.length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu mới phải có ít nhất 6 ký tự");
            }

            userService.updatePassword(user, newPassword);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đổi mật khẩu thành công");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }
}
