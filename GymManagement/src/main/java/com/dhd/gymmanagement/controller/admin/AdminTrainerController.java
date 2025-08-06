package com.dhd.gymmanagement.controller.admin;

import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/trainers")
public class AdminTrainerController {
    
    @Autowired
    private TrainerService trainerService;
    
    @GetMapping
    public String listTrainers(Model model, 
                              @RequestParam(required = false) String keyword) {
        
        List<Trainer> trainers;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            trainers = trainerService.searchTrainers(keyword);
        } else {
            trainers = trainerService.getAllTrainers();
        }
        
        long totalTrainers = trainers.size();
        long activeTrainers = totalTrainers; // All displayed trainers are active
        long inactiveTrainers = 0; // We don't display inactive trainers in the list
        
        model.addAttribute("trainers", trainers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalTrainers", totalTrainers);
        model.addAttribute("activeTrainers", activeTrainers);
        model.addAttribute("inactiveTrainers", inactiveTrainers);
        
        return "admin/trainer/list";
    }
    
    @GetMapping("/create")
    public String createTrainerForm(Model model) {
        Trainer trainer = new Trainer();
        User user = new User();
        user.setRole(User.Role.PT);
        trainer.setUser(user);
        model.addAttribute("trainer", trainer);
        return "admin/trainer/form";
    }
    
    @PostMapping("/create")
    public String createTrainer(@ModelAttribute Trainer trainer, RedirectAttributes redirectAttributes) {
        try {
            trainerService.createTrainer(trainer);
            redirectAttributes.addFlashAttribute("success", "Tạo trainer thành công!");
            return "redirect:/admin/trainers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo trainer: " + e.getMessage());
            return "redirect:/admin/trainers/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editTrainerForm(@PathVariable Integer id, Model model) {
        try {
            Trainer trainer = trainerService.getTrainerById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy trainer"));
            
            model.addAttribute("trainer", trainer);
            return "admin/trainer/form";
        } catch (Exception e) {
            return "redirect:/admin/trainers";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String editTrainer(@PathVariable Integer id, @ModelAttribute Trainer trainer, RedirectAttributes redirectAttributes) {
        try {
            trainerService.updateTrainer(id, trainer);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trainer thành công!");
            return "redirect:/admin/trainers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật trainer: " + e.getMessage());
            return "redirect:/admin/trainers/edit/" + id;
        }
    }
    
    @PostMapping("/delete/{id}")
    public String deleteTrainer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            trainerService.deleteTrainer(id);
            redirectAttributes.addFlashAttribute("success", "Xóa trainer thành công!");
            return "redirect:/admin/trainers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa trainer: " + e.getMessage());
            return "redirect:/admin/trainers";
        }
    }
    
    @GetMapping("/view/{id}")
    public String viewTrainer(@PathVariable Integer id, Model model) {
        try {
            Trainer trainer = trainerService.getTrainerById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy trainer"));
            
            model.addAttribute("trainer", trainer);
            return "admin/trainer/view";
        } catch (Exception e) {
            return "redirect:/admin/trainers";
        }
    }
}
