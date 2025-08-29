package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Rate limiting storage - trong production nên dùng Redis
    private static final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 5; // Tối đa 5 lần thử trong 15 phút
    private static final int MAX_REGISTER_ATTEMPTS = 3; // Tối đa 3 lần đăng ký trong 15 phút
    private static final long RATE_LIMIT_WINDOW_MINUTES = 15;

    // Password complexity patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    private static final int MIN_PASSWORD_LENGTH = 8;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest request) {
        try {
            String clientIp = getClientIpAddress(request);
            String rateLimitKey = "login:" + clientIp;

            // Kiểm tra rate limiting
            if (isRateLimited(rateLimitKey, MAX_LOGIN_ATTEMPTS)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Quá nhiều lần thử đăng nhập. Vui lòng thử lại sau " + RATE_LIMIT_WINDOW_MINUTES + " phút.");
            }

            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.badRequest().body("Email và mật khẩu không được để trống");
            }

            // Validate email format
            if (!isValidEmail(email)) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.badRequest().body("Định dạng email không hợp lệ");
            }

            User user = userService.getUserByEmail(email.toLowerCase().trim())
                    .orElse(null);

            if (user == null) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không đúng");
            }

            if (user.getIsDeleted() == 1) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản đã bị xóa");
            }

            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email hoặc mật khẩu không đúng");
            }

            // Reset rate limit sau khi đăng nhập thành công
            resetRateLimit(rateLimitKey);

            // Tạo JWT token
            String jwtToken = JwtUtils.generateToken(
                    user.getUserId().longValue(),
                    user.getEmail(),
                    user.getRole().name()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("message", "Đăng nhập thành công");
            response.put("user", Map.of(
                    "user_id", user.getUserId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole().name()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> registerRequest, HttpServletRequest request) {
        try {
            String clientIp = getClientIpAddress(request);
            String rateLimitKey = "register:" + clientIp;

            // Kiểm tra rate limiting
            if (isRateLimited(rateLimitKey, MAX_REGISTER_ATTEMPTS)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Quá nhiều lần thử đăng ký. Vui lòng thử lại sau " + RATE_LIMIT_WINDOW_MINUTES + " phút.");
            }

            String name = (String) registerRequest.get("name");
            String email = (String) registerRequest.get("email");
            String password = (String) registerRequest.get("password");
            String phoneNumber = (String) registerRequest.get("phoneNumber");

            // Basic validation
            if (name == null || email == null || password == null ||
                    name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.badRequest().body("Tên, email và mật khẩu không được để trống");
            }

            // Validate name length
            if (name.trim().length() < 2 || name.trim().length() > 50) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.badRequest().body("Tên phải từ 2-50 ký tự");
            }

            // Validate email format
            if (!isValidEmail(email)) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.badRequest().body("Định dạng email không hợp lệ");
            }

            // Enhanced password validation
            String passwordValidationError = validatePasswordComplexity(password);
            if (passwordValidationError != null) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.badRequest().body(passwordValidationError);
            }

            // Validate phone number format if provided
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                if (!isValidPhoneNumber(phoneNumber.trim())) {
                    incrementRateLimit(rateLimitKey);
                    return ResponseEntity.badRequest().body("Số điện thoại không hợp lệ");
                }
            }

            // Check existing user
            if (userService.getUserByEmail(email.toLowerCase().trim()).isPresent()) {
                incrementRateLimit(rateLimitKey);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại trong hệ thống");
            }

            // Check existing phone number
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                if (userService.getUserByPhoneNumber(phoneNumber.trim()).isPresent()) {
                    incrementRateLimit(rateLimitKey);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Số điện thoại đã tồn tại trong hệ thống");
                }
            }

            User newUser = new User();
            newUser.setName(name.trim());
            newUser.setEmail(email.toLowerCase().trim());
            newUser.setPasswordHash(passwordEncoder.encode(password));
            newUser.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
            newUser.setRole(User.Role.USER);
            newUser.setIsDeleted(0);

            userService.createUser(newUser);

            // Reset rate limit sau khi đăng ký thành công
            resetRateLimit(rateLimitKey);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng ký thành công");
            response.put("user", Map.of(
                    "user_id", newUser.getUserId(),
                    "email", newUser.getEmail(),
                    "name", newUser.getName(),
                    "role", newUser.getRole().name()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đăng xuất thành công");
        return ResponseEntity.ok(response);
    }

    // ============= UTILITY METHODS =============

    private String validatePasswordComplexity(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự";
        }

        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            return "Mật khẩu phải có ít nhất 1 chữ hoa";
        }

        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            return "Mật khẩu phải có ít nhất 1 chữ thường";
        }

        if (!DIGIT_PATTERN.matcher(password).matches()) {
            return "Mật khẩu phải có ít nhất 1 chữ số";
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            return "Mật khẩu phải có ít nhất 1 ký tự đặc biệt (!@#$%^&*...)";
        }

        // Check for common weak passwords
        if (isCommonPassword(password)) {
            return "Mật khẩu quá phổ biến, vui lòng chọn mật khẩu khác";
        }

        return null;
    }

    private boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        String[] commonPasswords = {
                "password", "123456", "12345678", "qwerty", "abc123",
                "password123", "admin", "letmein", "welcome", "monkey"
        };

        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email.trim()).matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Vietnam phone number format
        String phoneRegex = "^(\\+84|0)[0-9]{9,10}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phoneNumber.replaceAll("\\s+", "")).matches();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private boolean isRateLimited(String key, int maxAttempts) {
        RateLimitInfo rateLimitInfo = rateLimitMap.get(key);
        if (rateLimitInfo == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(rateLimitInfo.windowEnd)) {
            rateLimitMap.remove(key);
            return false;
        }

        return rateLimitInfo.attempts >= maxAttempts;
    }

    private void incrementRateLimit(String key) {
        LocalDateTime now = LocalDateTime.now();
        RateLimitInfo rateLimitInfo = rateLimitMap.get(key);

        if (rateLimitInfo == null || now.isAfter(rateLimitInfo.windowEnd)) {
            rateLimitInfo = new RateLimitInfo(1, now.plusMinutes(RATE_LIMIT_WINDOW_MINUTES));
        } else {
            rateLimitInfo.attempts++;
        }

        rateLimitMap.put(key, rateLimitInfo);
    }

    private void resetRateLimit(String key) {
        rateLimitMap.remove(key);
    }

    private static class RateLimitInfo {
        int attempts;
        LocalDateTime windowEnd;

        RateLimitInfo(int attempts, LocalDateTime windowEnd) {
            this.attempts = attempts;
            this.windowEnd = windowEnd;
        }
    }
}