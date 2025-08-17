package com.dhd.gymmanagement.controller.pt;

import com.dhd.gymmanagement.entity.*;
import com.dhd.gymmanagement.service.*;
import com.dhd.gymmanagement.dto.TrainingSessionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/pt/dashboard")
public class PTDashboardController {
    
    @Autowired
    private PTAssignmentService ptAssignmentService;
    
                        @Autowired
    private TrainingSessionService trainingSessionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TrainerService trainerService;
    
    // Dashboard chính của PT
    @GetMapping
    public String dashboard(Model model) {
        // Lấy trainerId từ authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        // Tìm trainer theo email
        Trainer trainer = userService.findTrainerByEmail(email);
        if (trainer == null) {
            // Nếu không có trainer record, kiểm tra xem có trainer với user_id này không
            User user = userService.findByEmail(email);
            if (user != null && User.Role.PT.equals(user.getRole())) {
                // Kiểm tra xem có trainer record nào với user_id này không
                Trainer existingTrainer = trainerService.getTrainerById(user.getUserId()).orElse(null);
                if (existingTrainer != null) {
                    trainer = existingTrainer;
                } else {
                    // Tạo trainer record mới
                    Trainer newTrainer = new Trainer();
                    newTrainer.setUser(user);
                    newTrainer.setSpecialization("Fitness");
                    newTrainer.setIsDeleted(0);
                    trainer = trainerService.save(newTrainer);
                }
            } else {
                return "redirect:/login?error=unauthorized";
            }
        }
        
        Integer trainerId = trainer.getTrainerId();
        List<PTAssignment> assignments = ptAssignmentService.getAssignmentsByTrainer(trainerId);
        List<TrainingSession> todaySessions = trainingSessionService.getTodaySessions(trainerId);
        
        // Lấy session trong tuần tới
        Date nextWeek = Date.valueOf(LocalDate.now().plusWeeks(1));
        List<TrainingSession> upcomingSessions = trainingSessionService.getUpcomingSessionsByTrainer(trainerId, nextWeek);
        
        model.addAttribute("assignments", assignments);
        model.addAttribute("todaySessions", todaySessions);
        model.addAttribute("upcomingSessions", upcomingSessions);
        model.addAttribute("trainerId", trainerId);
        
        return "pt/dashboard";
    }
    
                        // Xem chi tiết assignment
    @GetMapping("/assignment/{assignmentId}")
    public String viewAssignment(@PathVariable Integer assignmentId, Model model) {
        PTAssignment assignment = ptAssignmentService.getAssignmentById(assignmentId);
        
        // Kiểm tra assignment có tồn tại không
        if (assignment == null) {
            // Redirect về dashboard với thông báo lỗi
            return "redirect:/pt/dashboard?error=assignment_not_found&id=" + assignmentId;
        }
        
        List<UserAvailability> userAvailabilities = userService.getUserAvailabilities(assignment.getUser().getUserId());
        
        // Lấy sessions nếu đã có trainer
        List<TrainingSessionDTO> sessions = new ArrayList<>();
        if (assignment.getTrainer() != null) {
            sessions = trainingSessionService.getSessionsByUserAndTrainer(
                assignment.getUser().getUserId(), 
                assignment.getTrainer().getTrainerId()
            );
        }

        model.addAttribute("assignment", assignment);
        model.addAttribute("userAvailabilities", userAvailabilities);
        model.addAttribute("sessions", sessions);

        return "pt/assignment-detail";
    }
    
    // Xem lịch tập theo ngày
    @GetMapping("/schedule")
    public String viewSchedule(@RequestParam Integer trainerId,
                             @RequestParam(required = false) String date,
                             Model model) {
        Date sessionDate;
        if (date != null && !date.isEmpty()) {
            sessionDate = Date.valueOf(date);
        } else {
            sessionDate = Date.valueOf(LocalDate.now());
        }
        
        List<TrainingSession> sessions = trainingSessionService.getSessionsByTrainerAndDate(trainerId, sessionDate);
        
        model.addAttribute("sessions", sessions);
        model.addAttribute("sessionDate", sessionDate);
        model.addAttribute("trainerId", trainerId);
        
        return "pt/schedule";
    }
    
    // Cập nhật trạng thái session
    @PostMapping("/session/status/{sessionId}")
    public String updateSessionStatus(@PathVariable Integer sessionId,
                                    @RequestParam TrainingSession.Status status,
                                    @RequestParam Integer trainerId,
                                    RedirectAttributes redirectAttributes) {
        try {
            trainingSessionService.updateSessionStatus(sessionId, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái session thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        
        return "redirect:/pt/dashboard?trainerId=" + trainerId;
    }
    
    // Thêm ghi chú cho session
    @PostMapping("/session/notes/{sessionId}")
    public String updateSessionNotes(@PathVariable Integer sessionId,
                                   @RequestParam String notes,
                                   @RequestParam Integer trainerId,
                                   RedirectAttributes redirectAttributes) {
        try {
            trainingSessionService.updateSessionNotes(sessionId, notes);
            redirectAttributes.addFlashAttribute("success", "Cập nhật ghi chú thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật ghi chú: " + e.getMessage());
        }
        
        return "redirect:/pt/dashboard?trainerId=" + trainerId;
    }
    
    // Tạo lịch tập mới
    @PostMapping("/session/create")
    public String createSession(@RequestParam Integer trainerId,
                              @RequestParam Integer userId,
                              @RequestParam String sessionDate,
                              @RequestParam String startTime,
                              @RequestParam String endTime,
                              @RequestParam String status,
                              @RequestParam(required = false) String notes,
                              @RequestParam Integer assignmentId,
                              RedirectAttributes redirectAttributes) {
        try {
            // Chuyển đổi string thành Date và LocalTime
            java.sql.Date date = java.sql.Date.valueOf(sessionDate);
            java.time.LocalTime start = java.time.LocalTime.parse(startTime);
            java.time.LocalTime end = java.time.LocalTime.parse(endTime);
            
            // Tạo training session mới
            TrainingSession newSession = new TrainingSession();
            newSession.setSessionDate(date);
            newSession.setStartTime(start);
            newSession.setEndTime(end);
            newSession.setStatus(TrainingSession.Status.valueOf(status));
            newSession.setNotes(notes);
            newSession.setIsDeleted(0);
            
            // Lấy user và trainer objects
            User user = userService.getUserById(userId).orElse(null);
            Trainer trainer = trainerService.getTrainerById(trainerId).orElse(null);
            
            if (user != null && trainer != null) {
                // Lưu session
                trainingSessionService.createSession(user, trainer, date, start, end, notes);
                
                // Cập nhật trạng thái assignment từ PENDING/ASSIGNED -> ACTIVE
                ptAssignmentService.updateAssignmentStatusById(assignmentId, PTAssignment.Status.ACTIVE);
            } else {
                throw new RuntimeException("Không tìm thấy user hoặc trainer");
            }
            
            redirectAttributes.addFlashAttribute("success", "Tạo lịch tập thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo lịch tập: " + e.getMessage());
        }
        
        return "redirect:/pt/dashboard/assignment/" + assignmentId; // Redirect về assignment detail với ID động
    }
    
    // Xem thống kê
    @GetMapping("/stats")
    public String viewStats(@RequestParam Integer trainerId, Model model) {
        List<PTAssignment> activeAssignments = ptAssignmentService.getAssignmentsByTrainer(trainerId);
        
        // Đếm số session theo trạng thái
        long totalSessions = 0;
        long completedSessions = 0;
        long cancelledSessions = 0;
        
                                    for (PTAssignment assignment : activeAssignments) {
            List<TrainingSessionDTO> sessions = trainingSessionService.getSessionsByUserAndTrainer(
                assignment.getUser().getUserId(), 
                assignment.getTrainer().getTrainerId()
            );
            totalSessions += sessions.size();

            for (TrainingSessionDTO session : sessions) {
                if (session.getStatus() == TrainingSession.Status.COMPLETED) {
                    completedSessions++;
                } else if (session.getStatus() == TrainingSession.Status.CANCELLED) {
                    cancelledSessions++;
                }
            }
        }
        
        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("completedSessions", completedSessions);
        model.addAttribute("cancelledSessions", cancelledSessions);
        model.addAttribute("activeAssignments", activeAssignments.size());
        model.addAttribute("trainerId", trainerId);
        
        return "pt/stats";
    }
    

}
