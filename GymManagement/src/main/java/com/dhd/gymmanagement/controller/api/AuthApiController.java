package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.dto.ApiResponse;
import com.dhd.gymmanagement.dto.LoginRequest;
import com.dhd.gymmanagement.dto.RegisterRequest;
import com.dhd.gymmanagement.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthApiController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest loginRequest, 
            HttpServletRequest httpRequest, 
            HttpServletResponse httpResponse) {
        try {
            Map<String, Object> authResult = authService.authenticateUser(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
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
            ApiResponse<Map<String, Object>> response = ApiResponse.success(authResult, "Đăng nhập thành công");
            response.setPath(httpRequest.getRequestURI());
            
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.error(e.getMessage(), "Dữ liệu không hợp lệ");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.error(e.getMessage(), "Xác thực thất bại");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.error("Lỗi hệ thống", "Có lỗi xảy ra trong quá trình xử lý");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            HttpServletRequest httpRequest) {
        try {
            Map<String, Object> result = authService.registerUser(
                registerRequest.getName(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getPhoneNumber()
            );
            
            ApiResponse<Map<String, Object>> response = ApiResponse.success(result, "Đăng ký thành công");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.error(e.getMessage(), "Dữ liệu không hợp lệ");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.error(e.getMessage(), "Tài khoản đã tồn tại");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.error("Lỗi hệ thống", "Có lỗi xảy ra trong quá trình xử lý");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout(
            HttpServletRequest httpRequest, 
            HttpServletResponse httpResponse) {
        try {
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

            Map<String, String> data = new HashMap<>();
            data.put("message", "Đăng xuất thành công");
            
            ApiResponse<Map<String, String>> response = ApiResponse.success(data, "Đăng xuất thành công");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<Map<String, String>> response = ApiResponse.error("Lỗi hệ thống", "Có lỗi xảy ra trong quá trình đăng xuất");
            response.setPath(httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
