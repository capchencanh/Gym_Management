package com.dhd.gymmanagement.controller.admin;

import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.TrainerService;
import com.dhd.gymmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/trainers")
public class AdminTrainerController {
    
    @Autowired
    private TrainerService trainerService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public String listTrainers(Model model, 
                              @RequestParam(required = false) String keyword) {
        
        List<User> ptUsers;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            ptUsers = userService.searchUsersByRole(User.Role.PT, keyword);
        } else {
            ptUsers = userService.getUsersByRole(User.Role.PT);
        }
        
        List<Trainer> trainers = new ArrayList<>();
        for (User user : ptUsers) {
            Trainer trainer = new Trainer();
            trainer.setTrainerId(user.getUserId());
            trainer.setUser(user);
            try {
                Trainer existingTrainer = trainerService.getTrainerById(user.getUserId()).orElse(null);
                if (existingTrainer != null) {
                    trainer.setSpecialization(existingTrainer.getSpecialization());
                    trainer.setSchedule(existingTrainer.getSchedule());
                }
            } catch (Exception e) {
            }
            trainers.add(trainer);
        }
        
        long totalTrainers = ptUsers.size();
        long activeTrainers = totalTrainers;
        long inactiveTrainers = 0;
        
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
    public String createTrainer(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String phoneNumber,
                               @RequestParam String passwordHash,
                               @RequestParam(required = false) String gender,
                               @RequestParam(required = false) String birthdate,
                               @RequestParam(required = false) String height,
                               @RequestParam(required = false) String weight,
                               @RequestParam(required = false) String fitnessGoal,
                               @RequestParam(required = false) String specialization,
                               @RequestParam(required = false) String schedule,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setPasswordHash(passwordHash);
            user.setGender(gender);
            user.setFitnessGoal(fitnessGoal);
            user.setRole(User.Role.PT);
            
            if (birthdate != null && !birthdate.trim().isEmpty()) {
                try {
                    user.setBirthdate(java.sql.Date.valueOf(birthdate));
                } catch (IllegalArgumentException e) {
                    user.setBirthdate(null);
                }
            }
            
            if (height != null && !height.trim().isEmpty()) {
                try {
                    user.setHeight(Double.parseDouble(height));
                } catch (NumberFormatException e) {
                    user.setHeight(null);
                }
            }
            
            if (weight != null && !weight.trim().isEmpty()) {
                try {
                    user.setWeight(Double.parseDouble(weight));
                } catch (NumberFormatException e) {
                    user.setWeight(null);
                }
            }
            
            User savedUser = userService.createUser(user);
            
            Trainer trainer = new Trainer();
            trainer.setUser(savedUser);
            trainer.setSpecialization(specialization);
            trainer.setSchedule(schedule);
            
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
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            
            if (user.getRole() != User.Role.PT) {
                throw new RuntimeException("User này không phải là trainer");
            }
            
            Trainer trainer = new Trainer();
            trainer.setTrainerId(user.getUserId());
            trainer.setUser(user);
            
            try {
                Trainer existingTrainer = trainerService.getTrainerById(user.getUserId()).orElse(null);
                if (existingTrainer != null) {
                    trainer.setSpecialization(existingTrainer.getSpecialization());
                    trainer.setSchedule(existingTrainer.getSchedule());
                }
            } catch (Exception e) {
            }
            
            model.addAttribute("trainer", trainer);
            return "admin/trainer/form";
        } catch (Exception e) {
            return "redirect:/admin/trainers";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String editTrainer(@PathVariable Integer id,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phoneNumber,
                             @RequestParam(required = false) String gender,
                             @RequestParam(required = false) String birthdate,
                             @RequestParam(required = false) String height,
                             @RequestParam(required = false) String weight,
                             @RequestParam(required = false) String fitnessGoal,
                             @RequestParam(required = false) String specialization,
                             @RequestParam(required = false) String schedule,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            
            if (user.getRole() != User.Role.PT) {
                throw new RuntimeException("User này không phải là trainer");
            }
            
            user.setName(name);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            user.setGender(gender);
            user.setFitnessGoal(fitnessGoal);
            
            if (birthdate != null && !birthdate.trim().isEmpty()) {
                try {
                    user.setBirthdate(java.sql.Date.valueOf(birthdate));
                } catch (IllegalArgumentException e) {
                    user.setBirthdate(null);
                }
            }
            
            if (height != null && !height.trim().isEmpty()) {
                try {
                    user.setHeight(Double.parseDouble(height));
                } catch (NumberFormatException e) {
                    user.setHeight(null);
                }
            }
            
            if (weight != null && !weight.trim().isEmpty()) {
                try {
                    user.setWeight(Double.parseDouble(weight));
                } catch (NumberFormatException e) {
                    user.setWeight(null);
                }
            }
            
            User updatedUser = userService.updateUser(id, user);
            
            Trainer trainer;
            try {
                trainer = trainerService.getTrainerById(id).orElse(null);
                if (trainer == null) {
                    trainer = new Trainer();
                    trainer.setTrainerId(updatedUser.getUserId());
                    trainer.setUser(updatedUser);
                }
            } catch (Exception e) {
                trainer = new Trainer();
                trainer.setTrainerId(updatedUser.getUserId());
                trainer.setUser(updatedUser);
            }
            
            trainer.setSpecialization(specialization);
            trainer.setSchedule(schedule);
            
            trainerService.createTrainer(trainer);
            
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
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            
            if (user.getRole() != User.Role.PT) {
                throw new RuntimeException("User này không phải là trainer");
            }
            
            try {
                trainerService.deleteTrainer(id);
            } catch (Exception e) {
            }
            
            userService.deleteUser(id);
            
            redirectAttributes.addFlashAttribute("success", "Xóa trainer thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa trainer: " + e.getMessage());
        }
        return "redirect:/admin/trainers";
    }
    
    @GetMapping("/view/{id}")
    public String viewTrainer(@PathVariable Integer id, Model model) {
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            
            if (user.getRole() != User.Role.PT) {
                throw new RuntimeException("User này không phải là trainer");
            }
            
            Trainer trainer = new Trainer();
            trainer.setTrainerId(user.getUserId());
            trainer.setUser(user);
            
            try {
                Trainer existingTrainer = trainerService.getTrainerById(user.getUserId()).orElse(null);
                if (existingTrainer != null) {
                    trainer.setSpecialization(existingTrainer.getSpecialization());
                    trainer.setSchedule(existingTrainer.getSchedule());
                }
            } catch (Exception e) {
            }
            
            model.addAttribute("trainer", trainer);
            return "admin/trainer/view";
        } catch (Exception e) {
            return "redirect:/admin/trainers";
        }
    }

    @GetMapping("/sync-trainers")
    public String syncTrainers(RedirectAttributes redirectAttributes) {
        try {
            List<User> ptUsers = userService.getUsersByRole(User.Role.PT);
            int createdCount = 0;
            
            for (User user : ptUsers) {
                try {
                    trainerService.getTrainerById(user.getUserId());
                } catch (Exception e) {
                    Trainer trainer = new Trainer();
                    trainer.setUser(user);
                    trainerService.createTrainer(trainer);
                    createdCount++;
                }
            }
            
            redirectAttributes.addFlashAttribute("success", 
                "Đồng bộ thành công! Đã tạo " + createdCount + " trainer records cho " + ptUsers.size() + " PT users.");
            return "redirect:/admin/trainers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đồng bộ: " + e.getMessage());
            return "redirect:/admin/trainers";
        }
    }
}
