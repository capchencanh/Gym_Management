package com.dhd.gymmanagement.controller.admin;

import com.dhd.gymmanagement.dto.AdminClassDTO;
import com.dhd.gymmanagement.entity.ClassEnrollment;
import com.dhd.gymmanagement.entity.TrainingClass;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.repository.TrainerRepository;
import com.dhd.gymmanagement.service.ClassEnrollmentService;
import com.dhd.gymmanagement.service.TrainingClassService;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/classes")
public class AdminClassController {

    @Autowired
    private TrainingClassService trainingClassService;

    @Autowired
    private ClassEnrollmentService classEnrollmentService;

    @Autowired
    private TrainerRepository trainerRepository;


    @GetMapping
    public String listClasses(Model model) {
        List<TrainingClass> classes = trainingClassService.getAllClasses();
        

        List<AdminClassDTO> classDTOs = classes.stream().map(trainingClass -> {
            AdminClassDTO dto = new AdminClassDTO(trainingClass);
            Long enrolledCount = classEnrollmentService.getEnrolledCountByClass(trainingClass.getClassId());
            dto.setCurrentEnrollmentCount(enrolledCount != null ? enrolledCount.intValue() : 0);
            return dto;
        }).collect(Collectors.toList());
        
        model.addAttribute("classes", classDTOs);
        return "admin/class/list";
    }


    @GetMapping("/create")
    public String createClassForm(Model model) {
        List<Trainer> trainers = trainerRepository.findAllActive();
        model.addAttribute("trainers", trainers);
        model.addAttribute("trainingClass", new TrainingClass());
        return "admin/class/form";
    }


    @PostMapping("/create")
    public String createClass(@ModelAttribute TrainingClass trainingClass, 
                           @RequestParam(required = false) Integer trainerId,
                           RedirectAttributes redirectAttributes) {
        try {
            if (trainerId != null) {
                trainerRepository.findById(trainerId).ifPresent(trainingClass::setTrainer);
            }
            

            if (trainingClass.getStartTime() == null || trainingClass.getStartTime().trim().isEmpty()) {
                trainingClass.setStartTime("00:00");
            }
            
            TrainingClass savedClass = trainingClassService.createClass(trainingClass);
            if (savedClass != null) {
                redirectAttributes.addFlashAttribute("success", "Tạo lớp tập thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo lớp tập!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/classes";
    }


    @GetMapping("/edit/{id}")
    public String editClassForm(@PathVariable Integer id, Model model) {
        TrainingClass trainingClass = trainingClassService.getClassById(id);
        if (trainingClass == null) {
            return "redirect:/admin/classes";
        }
        
        List<Trainer> trainers = trainerRepository.findAllActive();
        model.addAttribute("trainers", trainers);
        model.addAttribute("trainingClass", trainingClass);
        return "admin/class/form";
    }


    @PostMapping("/edit/{id}")
    public String updateClass(@PathVariable Integer id, 
                           @ModelAttribute TrainingClass trainingClass,
                           @RequestParam(required = false) Integer trainerId,
                           RedirectAttributes redirectAttributes) {
        try {
            if (trainerId != null) {
                trainerRepository.findById(trainerId).ifPresent(trainingClass::setTrainer);
            }
            

            if (trainingClass.getStartTime() == null || trainingClass.getStartTime().trim().isEmpty()) {
                trainingClass.setStartTime("00:00");
            }
            
            TrainingClass updatedClass = trainingClassService.updateClass(id, trainingClass);
            if (updatedClass != null) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật lớp tập thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật lớp tập!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/admin/classes";
    }


    @PostMapping("/delete/{id}")
    public String deleteClass(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean deleted = trainingClassService.deleteClass(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Xóa lớp tập thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa lớp tập!");
        }
        return "redirect:/admin/classes";
    }


    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String viewClass(@PathVariable Integer id, Model model) {
        TrainingClass trainingClass = trainingClassService.getClassById(id);
        if (trainingClass == null) {
            return "redirect:/admin/classes";
        }
        
        List<ClassEnrollment> enrollments = classEnrollmentService.getAllEnrollmentsByClass(id);
        Long enrolledCount = classEnrollmentService.getEnrolledCountByClass(id);
        List<Trainer> trainers = trainerRepository.findAllActive();
        
        model.addAttribute("trainingClass", trainingClass);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("enrolledCount", enrolledCount);
        model.addAttribute("trainers", trainers);
        return "admin/class/view";
    }


    @PostMapping("/{id}/assign-trainer")
    public String assignTrainer(@PathVariable Integer id, 
                              @RequestParam Integer trainerId,
                              RedirectAttributes redirectAttributes) {
        boolean assigned = trainingClassService.assignTrainerToClass(id, trainerId);
        if (assigned) {
            redirectAttributes.addFlashAttribute("success", "Gán PT thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi gán PT!");
        }
        return "redirect:/admin/classes/" + id;
    }


    @GetMapping("/search")
    public String searchClasses(@RequestParam(required = false) String name, 
                              @RequestParam(required = false) String trainer,
                              Model model) {
        try {

            if ((name == null || name.trim().isEmpty()) && (trainer == null || trainer.trim().isEmpty())) {
                return "redirect:/admin/classes";
            }
            
            List<TrainingClass> classes;
            
            if (name != null && !name.trim().isEmpty()) {

                classes = trainingClassService.searchClassesByName(name);
                

                if ("assigned".equals(trainer)) {
                    classes = classes.stream()
                        .filter(tc -> tc.getTrainer() != null)
                        .collect(Collectors.toList());
                } else if ("unassigned".equals(trainer)) {
                    classes = classes.stream()
                        .filter(tc -> tc.getTrainer() == null)
                        .collect(Collectors.toList());
                }
            } else if ("assigned".equals(trainer)) {

                classes = trainingClassService.getClassesWithTrainer();
            } else if ("unassigned".equals(trainer)) {

                classes = trainingClassService.getClassesWithoutTrainer();
            } else {

                classes = trainingClassService.getAllClasses();
            }
            

            List<AdminClassDTO> classDTOs = classes.stream().map(trainingClass -> {
                AdminClassDTO dto = new AdminClassDTO(trainingClass);
                Long enrolledCount = classEnrollmentService.getEnrolledCountByClass(trainingClass.getClassId());
                dto.setCurrentEnrollmentCount(enrolledCount != null ? enrolledCount.intValue() : 0);
                return dto;
            }).collect(Collectors.toList());
            
            model.addAttribute("classes", classDTOs);
            model.addAttribute("searchTerm", name != null ? name : "");
            model.addAttribute("trainer", trainer);
            
        } catch (Exception e) {

            model.addAttribute("error", "Có lỗi xảy ra trong quá trình tìm kiếm: " + e.getMessage());
            model.addAttribute("classes", new ArrayList<>());
            model.addAttribute("searchTerm", name != null ? name : "");
            model.addAttribute("trainer", trainer);
        }
        
        return "admin/class/list";
    }


    @GetMapping("/without-trainer")
    public String classesWithoutTrainer(Model model) {
        List<TrainingClass> classes = trainingClassService.getClassesWithoutTrainer();
        

        List<AdminClassDTO> classDTOs = classes.stream().map(trainingClass -> {
            AdminClassDTO dto = new AdminClassDTO(trainingClass);
            Long enrolledCount = classEnrollmentService.getEnrolledCountByClass(trainingClass.getClassId());
            dto.setCurrentEnrollmentCount(enrolledCount != null ? enrolledCount.intValue() : 0);
            return dto;
        }).collect(Collectors.toList());
        
        model.addAttribute("classes", classDTOs);
        return "admin/class/list";
    }


    @PostMapping("/enrollment/{enrollmentId}/attendance")
    public String markAttendance(@PathVariable Integer enrollmentId,
                               @RequestParam Boolean attendance,
                               @RequestParam Integer classId,
                               RedirectAttributes redirectAttributes) {
        boolean marked = classEnrollmentService.markAttendance(enrollmentId, attendance);
        if (marked) {
            redirectAttributes.addFlashAttribute("success", "Cập nhật điểm danh thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật điểm danh!");
        }
        return "redirect:/admin/classes/" + classId;
    }


    @PostMapping("/enrollment/{enrollmentId}/cancel")
    public String cancelEnrollment(@PathVariable Integer enrollmentId,
                                 @RequestParam Integer classId,
                                 RedirectAttributes redirectAttributes) {
        boolean cancelled = classEnrollmentService.cancelEnrollment(enrollmentId);
        if (cancelled) {
            redirectAttributes.addFlashAttribute("success", "Hủy đăng ký thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi hủy đăng ký!");
        }
        return "redirect:/admin/classes/" + classId;
    }
}
