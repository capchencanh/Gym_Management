package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserApiController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            String token = authorizationHeader.substring(7);
            
            // Validate JWT token và lấy thông tin user
            Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
            if (claims == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
            }

            String email = (String) claims.get("email");
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user");
            }

            if (user.getIsDeleted() == 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản đã bị xóa");
            }

            // Trả về thông tin user
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

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestBody Map<String, Object> updateRequest) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }

            String token = authorizationHeader.substring(7);
            Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
            if (claims == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
            }

            String email = (String) claims.get("email");
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user");
            }

            // Cập nhật thông tin user
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
}
