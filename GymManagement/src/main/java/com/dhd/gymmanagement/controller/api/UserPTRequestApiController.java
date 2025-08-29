package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.entity.PTAssignment;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.UserAvailability;
import com.dhd.gymmanagement.dto.PTAssignmentDTO;
import com.dhd.gymmanagement.service.PTAssignmentService;
import com.dhd.gymmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/pt-request")
public class UserPTRequestApiController {

    @Autowired
    private PTAssignmentService ptAssignmentService;

    @Autowired
    private UserService userService;


    @PostMapping("/request")
    public ResponseEntity<?> requestPT(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            String requestNotes = (String) request.get("requestNotes");

            User user = userService.getUserById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy user");
            }

            PTAssignment assignment = ptAssignmentService.createAssignmentFromRequest(user, requestNotes);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/availability")
    public ResponseEntity<?> setAvailability(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            String dayOfWeekStr = (String) request.get("dayOfWeek");
            String startTimeStr = (String) request.get("startTime");
            String endTimeStr = (String) request.get("endTime");

            if (dayOfWeekStr == null || startTimeStr == null || endTimeStr == null) {
                return ResponseEntity.badRequest().body("Thiếu dữ liệu: dayOfWeek/startTime/endTime");
            }

            DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr.toUpperCase());
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            UserAvailability availability = userService.createUserAvailability(userId, dayOfWeek, startTime, endTime);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @GetMapping("/availability/{userId}")
    public ResponseEntity<List<UserAvailability>> getUserAvailability(@PathVariable Integer userId) {
        List<UserAvailability> availabilities = userService.getUserAvailabilities(userId);
        return ResponseEntity.ok(availabilities);
    }


    @PutMapping("/availability/{availabilityId}")
    public ResponseEntity<?> updateAvailability(@PathVariable Integer availabilityId, 
                                              @RequestBody Map<String, Object> request) {
        try {
            String dayOfWeekStr = (String) request.get("dayOfWeek");
            String startTimeStr = (String) request.get("startTime");
            String endTimeStr = (String) request.get("endTime");
            Boolean isAvailable = (Boolean) request.get("isAvailable");

            if (dayOfWeekStr == null || startTimeStr == null || endTimeStr == null) {
                return ResponseEntity.badRequest().body("Thiếu dữ liệu: dayOfWeek/startTime/endTime");
            }

            DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr.toUpperCase());
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            UserAvailability availability = userService.updateUserAvailability(
                availabilityId, dayOfWeek, startTime, endTime, isAvailable);
            return ResponseEntity.ok(availability);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @DeleteMapping("/availability/{availabilityId}")
    public ResponseEntity<?> deleteAvailability(@PathVariable Integer availabilityId) {
        try {
            userService.deleteUserAvailability(availabilityId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }


    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getPTRequestStatus(@PathVariable Integer userId) {
        try {
            List<PTAssignmentDTO> assignments = ptAssignmentService.getAssignmentsByUserAsDTO(userId);
            return ResponseEntity.ok(assignments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}
