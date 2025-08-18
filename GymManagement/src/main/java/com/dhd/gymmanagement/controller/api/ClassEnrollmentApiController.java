package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.dto.ClassEnrollmentDTO;
import com.dhd.gymmanagement.entity.ClassEnrollment;
import com.dhd.gymmanagement.service.ClassEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClassEnrollmentApiController {

    @Autowired
    private ClassEnrollmentService classEnrollmentService;

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
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/enrollments")
    public ResponseEntity<ClassEnrollmentDTO> createEnrollment(@RequestBody EnrollmentRequest request) {
        try {
            ClassEnrollment enrollment = classEnrollmentService.enrollUser(request.getUserId(), request.getClassId());
            if (enrollment != null) {
                return ResponseEntity.ok(new ClassEnrollmentDTO(enrollment));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
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
