package com.dhd.gymmanagement.controller.pt;

import com.dhd.gymmanagement.entity.*;
import com.dhd.gymmanagement.service.*;
import com.dhd.gymmanagement.dto.TrainingSessionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.LinkedHashMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private WorkoutLogService workoutLogService;

    @Autowired
    private WorkoutLogCommentService workoutLogCommentService;

    @GetMapping
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();


        Trainer trainer = userService.findTrainerByEmail(email);
        if (trainer == null) {

            User user = userService.findByEmail(email);
            if (user != null && User.Role.PT.equals(user.getRole())) {

                Trainer existingTrainer = trainerService.getTrainerById(user.getUserId()).orElse(null);
                if (existingTrainer != null) {
                    trainer = existingTrainer;
                } else {
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


        Date nextWeek = Date.valueOf(LocalDate.now().plusWeeks(1));
        List<TrainingSession> upcomingSessions = trainingSessionService.getUpcomingSessionsByTrainer(trainerId, nextWeek);

        model.addAttribute("assignments", assignments);
        model.addAttribute("todaySessions", todaySessions);
        model.addAttribute("upcomingSessions", upcomingSessions);
        model.addAttribute("trainerId", trainerId);

        return "pt/dashboard";
    }


    @GetMapping("/assignment/{assignmentId}")
    public String viewAssignment(@PathVariable Integer assignmentId, Model model) {
        PTAssignment assignment = ptAssignmentService.getAssignmentById(assignmentId);


        if (assignment == null) {

            return "redirect:/pt/dashboard?error=assignment_not_found&id=" + assignmentId;
        }

        List<UserAvailability> userAvailabilities = userService.getUserAvailabilities(assignment.getUser().getUserId());


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


        List<TrainingSession> rawSessions = trainingSessionService.getSessionsByTrainerAndDate(trainerId, sessionDate);
        List<TrainingSessionDTO> sessions = rawSessions.stream()
            .map(TrainingSessionDTO::new)
            .collect(Collectors.toList());

        model.addAttribute("sessions", sessions);
        model.addAttribute("sessionDate", sessionDate);
        model.addAttribute("trainerId", trainerId);

        return "pt/schedule";
    }


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


        try {

            List<PTAssignment> allAssignments = ptAssignmentService.getAllAssignments();
            for (PTAssignment assignment : allAssignments) {
                if (assignment.getUser() != null && assignment.getTrainer() != null) {
                    List<TrainingSessionDTO> sessions = trainingSessionService.getSessionsByUserAndTrainer(
                        assignment.getUser().getUserId(),
                        assignment.getTrainer().getTrainerId()
                    );
                    for (TrainingSessionDTO sessionDTO : sessions) {
                        if (sessionDTO.getSessionId().equals(sessionId)) {
                            return "redirect:/pt/dashboard/assignment/" + assignment.getAssignmentId();
                        }
                    }
                }
            }
        } catch (Exception e) {

        }

        return "redirect:/pt/dashboard?trainerId=" + trainerId;
    }


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


        try {

            List<PTAssignment> allAssignments = ptAssignmentService.getAllAssignments();
            for (PTAssignment assignment : allAssignments) {
                if (assignment.getUser() != null && assignment.getTrainer() != null) {
                    List<TrainingSessionDTO> sessions = trainingSessionService.getSessionsByUserAndTrainer(
                        assignment.getUser().getUserId(),
                        assignment.getTrainer().getTrainerId()
                    );
                    for (TrainingSessionDTO sessionDTO : sessions) {
                        if (sessionDTO.getSessionId().equals(sessionId)) {
                            return "redirect:/pt/dashboard/assignment/" + assignment.getAssignmentId();
                        }
                    }
                }
            }
        } catch (Exception e) {

        }

        return "redirect:/pt/dashboard?trainerId=" + trainerId;
    }

    @GetMapping("/client/{userId}/logs")
    public String viewClientWorkoutLogs(@PathVariable("userId") Integer userId, Model model, Authentication authentication) {

        User client = userService.getUserById(userId).orElse(null);
        if (client == null) {
            return "redirect:/pt/dashboard?error=client_not_found";
        }


        String ptEmail = authentication.getName();
        User currentPt = userService.findByEmail(ptEmail);
        if (currentPt == null || !User.Role.PT.equals(currentPt.getRole())) {
            return "redirect:/login?error=unauthorized";
        }


        List<WorkoutLog> logs = workoutLogService.findByUserId(userId);


        logs.forEach(log -> {
            List<WorkoutLogComment> comments = workoutLogCommentService.getCommentsByLogId(log.getLogId());
            log.setComments(comments);
        });


        Map<String, List<WorkoutLog>> sessions = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getSessionDate().toString() + "_" + (log.getSessionName() != null && !log.getSessionName().isEmpty() ? log.getSessionName() : "Buổi tập chung"),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        model.addAttribute("client", client);
        model.addAttribute("sessions", sessions);
        model.addAttribute("currentPt", currentPt);


        PTAssignment relevantAssignment = ptAssignmentService.getAssignmentsByTrainer(currentPt.getUserId())
                .stream()
                .filter(a -> a.getUser().getUserId().equals(userId))
                .findFirst()
                .orElse(null);
        model.addAttribute("assignment", relevantAssignment);

        return "pt/client-logs";
    }


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

            java.sql.Date date = java.sql.Date.valueOf(sessionDate);
            java.time.LocalTime start = java.time.LocalTime.parse(startTime);
            java.time.LocalTime end = java.time.LocalTime.parse(endTime);


            TrainingSession newSession = new TrainingSession();
            newSession.setSessionDate(date);
            newSession.setStartTime(start);
            newSession.setEndTime(end);
            newSession.setStatus(TrainingSession.Status.valueOf(status));
            newSession.setNotes(notes);
            newSession.setIsDeleted(0);


            User user = userService.getUserById(userId).orElse(null);
            Trainer trainer = trainerService.getTrainerById(trainerId).orElse(null);

            if (user != null && trainer != null) {

                trainingSessionService.createSession(user, trainer, date, start, end, notes);


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


    @GetMapping("/stats")
    public String viewStats(@RequestParam Integer trainerId, Model model) {
        List<PTAssignment> activeAssignments = ptAssignmentService.getAssignmentsByTrainer(trainerId);


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
