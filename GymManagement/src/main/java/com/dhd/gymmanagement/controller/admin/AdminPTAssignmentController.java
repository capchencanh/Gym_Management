package com.dhd.gymmanagement.controller.admin;

import com.dhd.gymmanagement.entity.*;
import com.dhd.gymmanagement.service.*;
import com.dhd.gymmanagement.dto.TrainingSessionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/pt-assignments")
public class AdminPTAssignmentController {
    
    @Autowired
    private PTAssignmentService ptAssignmentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TrainerService trainerService;
    
    @Autowired
    private TrainingSessionService trainingSessionService;
    

    @GetMapping
    public String listAllAssignments(Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String sortBy,
                                    @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort;
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        } else {
            sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "createdAt");
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<PTAssignment> assignmentsPage;
        
        if (status != null && !status.trim().isEmpty()) {
            try {
                PTAssignment.Status assignmentStatus = PTAssignment.Status.valueOf(status.toUpperCase());
                assignmentsPage = ptAssignmentService.getAssignmentsByStatus(assignmentStatus, pageable);
            } catch (IllegalArgumentException e) {
                assignmentsPage = ptAssignmentService.getAllAssignments(pageable);
            }
        } else {
            assignmentsPage = ptAssignmentService.getAllAssignments(pageable);
        }
        
        List<Trainer> availableTrainers = trainerService.getActiveTrainers();
        
        List<PTAssignment> allAssignments = ptAssignmentService.getAllAssignments();
        List<PTAssignment> pendingAssignments = allAssignments.stream()
            .filter(a -> a.getStatus() == PTAssignment.Status.PENDING)
            .collect(java.util.stream.Collectors.toList());
            
        List<PTAssignment> assignedAssignments = allAssignments.stream()
            .filter(a -> a.getStatus() == PTAssignment.Status.ASSIGNED)
            .collect(java.util.stream.Collectors.toList());
            
        List<PTAssignment> activeAssignments = allAssignments.stream()
            .filter(a -> a.getStatus() == PTAssignment.Status.ACTIVE)
            .collect(java.util.stream.Collectors.toList());
            
        List<PTAssignment> completedAssignments = allAssignments.stream()
            .filter(a -> a.getStatus() == PTAssignment.Status.COMPLETED)
            .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("allAssignments", assignmentsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", assignmentsPage.getTotalPages());
        model.addAttribute("totalItems", assignmentsPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("pendingAssignments", pendingAssignments);
        model.addAttribute("assignedAssignments", assignedAssignments);
        model.addAttribute("activeAssignments", activeAssignments);
        model.addAttribute("completedAssignments", completedAssignments);
        model.addAttribute("availableTrainers", availableTrainers);
        
        return "admin/pt-assignment/list";
    }
    

    @GetMapping("/create")
    public String createAssignmentForm(Model model) {
        List<User> users = userService.getUsersByRole(User.Role.USER);
        List<Trainer> trainers = trainerService.getActiveTrainers();
        
        model.addAttribute("users", users);
        model.addAttribute("trainers", trainers);
        
        return "admin/pt-assignment/form";
    }
    

                @PostMapping("/create")
                public String createAssignment(@RequestParam Integer userId,
                                             @RequestParam Integer trainerId,
                                             RedirectAttributes redirectAttributes) {
                    try {
                        User user = userService.getUserById(userId).orElse(null);
                        Trainer trainer = trainerService.getTrainerById(trainerId).orElse(null);

                        if (user != null && trainer != null) {
                            PTAssignment assignment = ptAssignmentService.createAssignment(user, trainer);
                            redirectAttributes.addFlashAttribute("success", "Tạo assignment thành công! PT sẽ lên lịch tập sau.");
                        } else {
                            redirectAttributes.addFlashAttribute("error", "Không tìm thấy user hoặc trainer!");
                        }
                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo assignment: " + e.getMessage());
                    }

                    return "redirect:/admin/pt-assignments";
                }
    

                @GetMapping("/assign/{assignmentId}")
                public String showAssignForm(@PathVariable Integer assignmentId, Model model) {
                    PTAssignment assignment = ptAssignmentService.getAssignmentById(assignmentId);
                    List<Trainer> availableTrainers = trainerService.getActiveTrainers();
                    
                    if (assignment != null) {
                        model.addAttribute("assignment", assignment);
                        model.addAttribute("availableTrainers", availableTrainers);
                        return "admin/pt-assignment/assign-form";
                    }
                    
                    return "redirect:/admin/pt-assignments?error=assignment_not_found";
                }
                

                @PostMapping("/assign/{assignmentId}")
                public String assignPT(@PathVariable Integer assignmentId,
                                      @RequestParam Integer trainerId,
                                      RedirectAttributes redirectAttributes) {
                    try {
                        PTAssignment assignment = ptAssignmentService.getAssignmentById(assignmentId);
                        Trainer trainer = trainerService.getTrainerById(trainerId).orElse(null);

                        if (assignment != null && trainer != null) {
                            assignment.setTrainer(trainer);
                            assignment.setStatus(PTAssignment.Status.ASSIGNED);
                            ptAssignmentService.updateAssignment(assignment);

                            redirectAttributes.addFlashAttribute("success", "Phân công PT thành công! PT sẽ lên lịch tập sau.");
                        }
                    } catch (Exception e) {
                        redirectAttributes.addFlashAttribute("error", "Lỗi khi phân công PT: " + e.getMessage());
                    }

                    return "redirect:/admin/pt-assignments";
                }
    

                @GetMapping("/view/{assignmentId}")
                public String viewAssignment(@PathVariable Integer assignmentId, Model model) {
                    PTAssignment assignment = ptAssignmentService.getAssignmentById(assignmentId);
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

                    return "admin/pt-assignment/view";
                }
    

    @PostMapping("/status/{assignmentId}")
    public String changeStatus(@PathVariable Integer assignmentId,
                              @RequestParam PTAssignment.Status status,
                              RedirectAttributes redirectAttributes) {
        try {
            ptAssignmentService.changeStatus(assignmentId, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        
        return "redirect:/admin/pt-assignments";
    }
    

    @PostMapping("/delete/{assignmentId}")
    public String deleteAssignment(@PathVariable Integer assignmentId,
                                 RedirectAttributes redirectAttributes) {
        try {
            ptAssignmentService.deleteAssignment(assignmentId);
            redirectAttributes.addFlashAttribute("success", "Xóa assignment thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa assignment: " + e.getMessage());
        }
        
        return "redirect:/admin/pt-assignments";
    }
}
