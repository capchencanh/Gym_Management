package com.dhd.gymmanagement.controller.admin;

import com.dhd.gymmanagement.entity.ClassEnrollment;
import com.dhd.gymmanagement.entity.TrainingClass;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.repository.TrainerRepository;
import com.dhd.gymmanagement.service.ClassEnrollmentService;
import com.dhd.gymmanagement.service.TrainingClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        model.addAttribute("classes", classes);
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
        if (trainerId != null) {
            trainerRepository.findById(trainerId).ifPresent(trainingClass::setTrainer);
        }
        
        TrainingClass savedClass = trainingClassService.createClass(trainingClass);
        if (savedClass != null) {
            redirectAttributes.addFlashAttribute("success", "Tạo lớp tập thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi tạo lớp tập!");
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
        if (trainerId != null) {
            trainerRepository.findById(trainerId).ifPresent(trainingClass::setTrainer);
        }
        
        TrainingClass updatedClass = trainingClassService.updateClass(id, trainingClass);
        if (updatedClass != null) {
            redirectAttributes.addFlashAttribute("success", "Cập nhật lớp tập thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật lớp tập!");
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
    public String viewClass(@PathVariable Integer id, Model model) {
        TrainingClass trainingClass = trainingClassService.getClassById(id);
        if (trainingClass == null) {
            return "redirect:/admin/classes";
        }
        
        List<ClassEnrollment> enrollments = classEnrollmentService.getEnrolledUsersByClass(id);
        Long enrolledCount = trainingClassService.getEnrolledCount(id);
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
    public String searchClasses(@RequestParam String name, Model model) {
        List<TrainingClass> classes = trainingClassService.searchClassesByName(name);
        model.addAttribute("classes", classes);
        model.addAttribute("searchTerm", name);
        return "admin/class/list";
    }


    @GetMapping("/without-trainer")
    public String classesWithoutTrainer(Model model) {
        List<TrainingClass> classes = trainingClassService.getClassesWithoutTrainer();
        model.addAttribute("classes", classes);
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
