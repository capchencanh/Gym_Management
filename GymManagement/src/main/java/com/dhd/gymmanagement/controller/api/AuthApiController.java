package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthApiController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");

            Map<String, Object> authResult = authService.authenticateUser(email, password);
            String jwtToken = (String) authResult.get("token");

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

            authResult.remove("token");
            return ResponseEntity.ok(authResult);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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

            Map<String, Object> result = authService.registerUser(name, email, password, phoneNumber);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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
