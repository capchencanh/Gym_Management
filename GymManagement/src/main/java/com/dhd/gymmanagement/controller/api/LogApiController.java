package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.dto.WorkoutLogDTO;
import com.dhd.gymmanagement.dto.WorkoutLogResponseDTO;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.WorkoutLog;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.service.WorkoutLogService;
import com.dhd.gymmanagement.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workout-logs")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LogApiController {

    private static final Logger logger = LoggerFactory.getLogger(LogApiController.class);

    @Autowired
    private WorkoutLogService workoutLogService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserWorkoutLogs(@PathVariable Integer userId,
                                                @RequestHeader("Authorization") String authorizationHeader) {
        try {

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }
            String token = authorizationHeader.substring(7);
            Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
            if (claims == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
            }
            String tokenEmail = (String) claims.get("email");
            User tokenUser = userService.getUserByEmail(tokenEmail).orElse(null);
            if (tokenUser == null || !tokenUser.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền truy cập dữ liệu này");
            }


            List<WorkoutLog> logs = workoutLogService.findByUserId(userId);

            // Convert to Response DTOs
            List<WorkoutLogResponseDTO> logDTOs = logs.stream()
                    .map(WorkoutLogResponseDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(logDTOs);

        } catch (Exception e) {
            logger.error("Error getting workout logs: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createWorkoutLog(@RequestBody WorkoutLogDTO createDTO,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        try {

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }
            String token = authorizationHeader.substring(7);
            Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
            if (claims == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
            }
            String tokenEmail = (String) claims.get("email");
            User tokenUser = userService.getUserByEmail(tokenEmail).orElse(null);
            if (tokenUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User không tồn tại");
            }


            WorkoutLog workoutLog = createDTO.toEntity();


            if (workoutLog.getSessionDate() == null) {
                workoutLog.setSessionDate(new Date(System.currentTimeMillis()));
            }
            if (workoutLog.getExerciseOrder() == null) {
                workoutLog.setExerciseOrder(1);
            }
            if (workoutLog.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user_id không được để trống");
            }
            if (workoutLog.getWorkoutType() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("workout_type không được để trống");
            }
            if (workoutLog.getExerciseName() == null || workoutLog.getExerciseName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("exercise_name không được để trống");
            }


            WorkoutLog savedLog = workoutLogService.save(workoutLog);


            WorkoutLogResponseDTO responseDTO = new WorkoutLogResponseDTO(savedLog);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (Exception e) {
            logger.error("Error saving workout log: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @GetMapping("/{logId}")
    public ResponseEntity<?> getWorkoutLog(@PathVariable Integer logId,
                                           @RequestHeader("Authorization") String authorizationHeader) {
        try {

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }
            String token = authorizationHeader.substring(7);
            Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
            if (claims == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
            }

            WorkoutLog log = workoutLogService.findById(logId).orElse(null);
            if (log == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy workout log");
            }


            String tokenEmail = (String) claims.get("email");
            User tokenUser = userService.getUserByEmail(tokenEmail).orElse(null);
            if (tokenUser == null || !tokenUser.getUserId().equals(log.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền truy cập log này");
            }


            // Convert to Response DTO
            WorkoutLogResponseDTO responseDTO = new WorkoutLogResponseDTO(log);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            logger.error("Error getting workout log: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<?> deleteWorkoutLog(@PathVariable Integer logId,
                                              @RequestHeader("Authorization") String authorizationHeader) {

        try {

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
            }
            String token = authorizationHeader.substring(7);
            Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
            if (claims == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn");
            }
            WorkoutLog log = workoutLogService.findById(logId).orElse(null);
            if (log == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy workout log");
            }
            String tokenEmail = (String) claims.get("email");
            User tokenUser = userService.getUserByEmail(tokenEmail).orElse(null);
            if (tokenUser == null || !tokenUser.getUserId().equals(log.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền xóa log này");
            }

            workoutLogService.deleteById(logId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa workout log thành công");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting workout log: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }
}