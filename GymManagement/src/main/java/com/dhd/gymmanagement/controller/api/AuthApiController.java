package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("Email và mật khẩu không được để trống");
            }

            User user = userService.getUserByEmail(email)
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email không tồn tại");
            }

            if (user.getIsDeleted() == 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản đã bị xóa");
            }

            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mật khẩu không đúng");
            }



            String jwtToken = JwtUtils.generateToken(
                user.getUserId().longValue(),
                user.getEmail(),
                user.getRole().name()
            );

            boolean isSecure = httpRequest.isSecure();
            String origin = httpRequest.getHeader("Origin");
            if (origin != null && origin.startsWith("http://localhost")) {
                isSecure = false;
            }
            ResponseCookie responseCookie = ResponseCookie.from("jwt_token", jwtToken)
                    .httpOnly(true)
                    .secure(isSecure)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(7L * 24 * 60 * 60)
                    .build();
            httpResponse.addHeader("Set-Cookie", responseCookie.toString());

            Map<String, Object> response = new HashMap<>();
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
    public ResponseEntity<?> register(@RequestBody Map<String, Object> registerRequest) {
        try {
            String name = (String) registerRequest.get("name");
            String email = (String) registerRequest.get("email");
            String password = (String) registerRequest.get("password");
            String phoneNumber = (String) registerRequest.get("phoneNumber");


            if (name == null || email == null || password == null) {
                return ResponseEntity.badRequest().body("Tên, email và mật khẩu không được để trống");
            }

            if (password.length() < 6) {
                return ResponseEntity.badRequest().body("Mật khẩu phải có ít nhất 6 ký tự");
            }


            if (userService.getUserByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại trong hệ thống");
            }


            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                if (userService.getUserByPhoneNumber(phoneNumber).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Số điện thoại đã tồn tại trong hệ thống");
                }
            }


            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPasswordHash(passwordEncoder.encode(password));
            newUser.setPhoneNumber(phoneNumber);
            newUser.setRole(User.Role.USER);
            newUser.setIsDeleted(0);

            userService.createUser(newUser);

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
    public ResponseEntity<?> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        boolean isSecure = httpRequest.isSecure();
        String origin = httpRequest.getHeader("Origin");
        if (origin != null && origin.startsWith("http://localhost")) {
            isSecure = false;
        }
        ResponseCookie responseCookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        httpResponse.addHeader("Set-Cookie", responseCookie.toString());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Đăng xuất thành công");
        return ResponseEntity.ok(response);
    }
}
