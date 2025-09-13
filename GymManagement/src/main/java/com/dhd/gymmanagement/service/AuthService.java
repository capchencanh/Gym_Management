package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    @Autowired
    private UserAuthService userAuthService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    public Map<String, Object> authenticateUser(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email và mật khẩu không được để trống");
        }

        User user = userAuthService.getUserByEmail(email)
                .orElse(null);

        if (user == null) {
            throw new RuntimeException("Email không tồn tại");
        }

        if (user.getIsDeleted() == 1) {
            throw new RuntimeException("Tài khoản đã bị xóa");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        String jwtToken;
        try {
            jwtToken = JwtUtils.generateToken(
                user.getUserId().longValue(),
                user.getEmail(),
                user.getRole().name()
            );
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo token: " + e.getMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng nhập thành công");
        response.put("token", jwtToken);
        response.put("user", Map.of(
            "user_id", user.getUserId(),
            "email", user.getEmail(),
            "name", user.getName(),
            "role", user.getRole().name()
        ));

        return response;
    }
    
    public Map<String, Object> registerUser(String name, String email, String password, String phoneNumber) {
        if (name == null || email == null || password == null) {
            throw new IllegalArgumentException("Tên, email và mật khẩu không được để trống");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }

        if (phoneNumber != null && !phoneNumber.trim().isEmpty() && !phoneNumber.matches("^[0-9]{10,11}$")) {
            throw new IllegalArgumentException("Số điện thoại phải có 10-11 chữ số");
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setPhoneNumber(phoneNumber != null && !phoneNumber.trim().isEmpty() ? phoneNumber : null);
        newUser.setRole(User.Role.USER);
        newUser.setIsDeleted(0);

        User savedUser = userAuthService.createUser(newUser);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng ký thành công");
        response.put("user", Map.of(
            "user_id", savedUser.getUserId(),
            "email", savedUser.getEmail(),
            "name", savedUser.getName(),
            "role", savedUser.getRole().name()
        ));

        return response;
    }
}
