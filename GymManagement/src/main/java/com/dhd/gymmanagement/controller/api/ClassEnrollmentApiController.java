package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.dto.ClassEnrollmentDTO;
import com.dhd.gymmanagement.entity.ClassEnrollment;
import com.dhd.gymmanagement.service.ClassEnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClassEnrollmentApiController {

    @Autowired
    private ClassEnrollmentService classEnrollmentService;

    private static final Logger logger = LoggerFactory.getLogger(ClassEnrollmentApiController.class);

    @GetMapping("/enrollments")
    public ResponseEntity<List<ClassEnrollmentDTO>> getAllEnrollments() {
        try {

            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/enrollments/user/{userId}")
    public ResponseEntity<List<ClassEnrollmentDTO>> getEnrollmentsByUser(@PathVariable Integer userId) {
        try {
            List<ClassEnrollment> enrollments = classEnrollmentService.getEnrollmentsByUser(userId);
            List<ClassEnrollmentDTO> enrollmentDTOs = enrollments.stream()
                .map(ClassEnrollmentDTO::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(enrollmentDTOs);
        } catch (Exception e) {
            logger.error("Error getEnrollmentsByUser {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/enrollments")
    public ResponseEntity<?> createEnrollment(@RequestBody Map<String, Object> request) {
        try {
            final Integer userId;
            final Integer classId;
            try {
                Object u = request.get("userId");
                Object c = request.get("classId");
                userId = (u == null) ? null : ((u instanceof Number) ? ((Number) u).intValue() : Integer.valueOf(u.toString()));
                classId = (c == null) ? null : ((c instanceof Number) ? ((Number) c).intValue() : Integer.valueOf(c.toString()));
            } catch (Exception ex) {
                logger.error("Parse payload error: {}", ex.getMessage());
                return ResponseEntity.badRequest().body(Map.of("message", "Payload không hợp lệ"));
            }
            if (userId == null || classId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Thiếu userId hoặc classId"));
            }
            boolean alreadyEnrolled = classEnrollmentService.isUserEnrolled(userId, classId);
            if (alreadyEnrolled) {
                List<ClassEnrollment> list = classEnrollmentService.getEnrollmentsByUser(userId);
                ClassEnrollment existing = list.stream()
                        .filter(e -> e.getTrainingClass() != null && e.getTrainingClass().getClassId().equals(classId))
                        .findFirst()
                        .orElse(null);
                if (existing != null) {
                    return ResponseEntity.ok(new ClassEnrollmentDTO(existing));
                }
            }

            ClassEnrollment enrollment = classEnrollmentService.enrollUser(userId, classId);
            if (enrollment != null) {
                return ResponseEntity.ok(new ClassEnrollmentDTO(enrollment));
            }

            return ResponseEntity.ok(Map.of("message", "Không thể tham gia lớp. Vui lòng kiểm tra lại."));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Không thể tham gia lớp. Vui lòng thử lại sau."));
        }
    }

    @PutMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<ClassEnrollmentDTO> updateEnrollmentStatus(
            @PathVariable Integer enrollmentId,
            @RequestBody StatusUpdateRequest request) {
        try {
            ClassEnrollment enrollment = classEnrollmentService.getEnrollmentById(enrollmentId);
            if (enrollment != null) {

                boolean success = false;
                switch (request.getStatus()) {
                    case "COMPLETED":
                        success = classEnrollmentService.completeEnrollment(enrollmentId);
                        break;
                    case "CANCELLED":
                        success = classEnrollmentService.cancelEnrollment(enrollmentId);
                        break;
                    default:
                        return ResponseEntity.badRequest().build();
                }
                
                if (success) {
                    ClassEnrollment updatedEnrollment = classEnrollmentService.getEnrollmentById(enrollmentId);
                    return ResponseEntity.ok(new ClassEnrollmentDTO(updatedEnrollment));
                } else {
                    return ResponseEntity.internalServerError().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    public static class EnrollmentRequest {
        private Integer userId;
        private Integer classId;

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public Integer getClassId() { return classId; }
        public void setClassId(Integer classId) { this.classId = classId; }
    }

    public static class StatusUpdateRequest {
        private String status;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
