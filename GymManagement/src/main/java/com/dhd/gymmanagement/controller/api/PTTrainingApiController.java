package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.entity.TrainingSession;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.dto.TrainingSessionDTO;
import com.dhd.gymmanagement.service.TrainingSessionService;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pt/training")
public class PTTrainingApiController {

    @Autowired
    private TrainingSessionService trainingSessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private TrainerService trainerService;


    @PostMapping("/session")
    public ResponseEntity<?> createSession(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            Integer trainerId = (Integer) request.get("trainerId");
            String sessionDateStr = (String) request.get("sessionDate");
            String startTimeStr = (String) request.get("startTime");
            String endTimeStr = (String) request.get("endTime");
            String notes = (String) request.get("notes");

            User user = userService.getUserById(userId).orElse(null);
            Trainer trainer = trainerService.getTrainerById(trainerId).orElse(null);

            if (user == null || trainer == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy user hoặc trainer");
            }

            Date sessionDate = Date.valueOf(sessionDateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            TrainingSession session = trainingSessionService.createSession(
                user, trainer, sessionDate, startTime, endTime, notes);
            
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @PutMapping("/session/{sessionId}/status")
    public ResponseEntity<?> updateSessionStatus(@PathVariable Integer sessionId, 
                                               @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            TrainingSession.Status sessionStatus = TrainingSession.Status.valueOf(status.toUpperCase());
            
            TrainingSession session = trainingSessionService.updateSessionStatus(sessionId, sessionStatus);
            if (session != null) {
                return ResponseEntity.ok(session);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @PutMapping("/session/{sessionId}/notes")
    public ResponseEntity<?> updateSessionNotes(@PathVariable Integer sessionId, 
                                              @RequestBody Map<String, String> request) {
        try {
            String notes = request.get("notes");
            
            TrainingSession session = trainingSessionService.updateSessionNotes(sessionId, notes);
            if (session != null) {
                return ResponseEntity.ok(session);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<TrainingSession>> getSessionsByTrainer(@PathVariable Integer trainerId) {
        List<TrainingSession> sessions = trainingSessionService.getSessionsByTrainer(trainerId);
        return ResponseEntity.ok(sessions);
    }


    @GetMapping("/trainer/{trainerId}/date/{date}")
    public ResponseEntity<?> getSessionsByTrainerAndDate(
            @PathVariable Integer trainerId, @PathVariable String date) {
        try {
            Date sessionDate = Date.valueOf(date);
            List<TrainingSession> sessions = trainingSessionService.getSessionsByTrainerAndDate(trainerId, sessionDate);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ngày không hợp lệ");
        }
    }


    @GetMapping("/user/{userId}/trainer/{trainerId}")
    public ResponseEntity<List<TrainingSessionDTO>> getSessionsByUserAndTrainer(
            @PathVariable Integer userId, @PathVariable Integer trainerId) {
        List<TrainingSessionDTO> sessions = trainingSessionService.getSessionsByUserAndTrainer(userId, trainerId);
        return ResponseEntity.ok(sessions);
    }


    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable Integer sessionId) {
        try {
            trainingSessionService.deleteSession(sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}
