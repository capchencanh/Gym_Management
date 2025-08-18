package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.entity.PTAssignment;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.dto.PTAssignmentDTO;
import com.dhd.gymmanagement.service.PTAssignmentService;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pt-assignments")
public class PTAssignmentApiController {

    @Autowired
    private PTAssignmentService ptAssignmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TrainerService trainerService;


    @GetMapping
    public ResponseEntity<List<PTAssignment>> getAllAssignments() {
        List<PTAssignment> assignments = ptAssignmentService.getPendingAssignments();
        return ResponseEntity.ok(assignments);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PTAssignment> getAssignmentById(@PathVariable Integer id) {
        PTAssignment assignment = ptAssignmentService.getAssignmentById(id);
        if (assignment != null) {
            return ResponseEntity.ok(assignment);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<?> createAssignment(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            Integer trainerId = (Integer) request.get("trainerId");

            User user = userService.getUserById(userId).orElse(null);
            Trainer trainer = trainerService.getTrainerById(trainerId).orElse(null);

            if (user == null || trainer == null) {
                return ResponseEntity.badRequest().body("User hoặc Trainer không tồn tại");
            }

            PTAssignment assignment = ptAssignmentService.createAssignment(user, trainer);


            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            PTAssignment.Status assignmentStatus = PTAssignment.Status.valueOf(status.toUpperCase());
            
            PTAssignment assignment = ptAssignmentService.changeStatus(id, assignmentStatus);
            if (assignment != null) {
                return ResponseEntity.ok(assignment);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Integer id) {
        try {
            ptAssignmentService.deleteAssignment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<PTAssignment>> getAssignmentsByTrainer(@PathVariable Integer trainerId) {
        List<PTAssignment> assignments = ptAssignmentService.getAssignmentsByTrainer(trainerId);
        return ResponseEntity.ok(assignments);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PTAssignmentDTO>> getAssignmentsByUser(@PathVariable Integer userId) {
        List<PTAssignmentDTO> assignments = ptAssignmentService.getAssignmentsByUserAsDTO(userId);
        return ResponseEntity.ok(assignments);
    }
}
