package com.dhd.gymmanagement.controller;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userService.findByEmail(email);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute User user, 
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User currentUser = userService.findByEmail(email);
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/profile";
            }
            

            currentUser.setName(user.getName());
            currentUser.setPhoneNumber(user.getPhoneNumber());
            currentUser.setBirthdate(user.getBirthdate());
            currentUser.setGender(user.getGender());
            currentUser.setHeight(user.getHeight());
            currentUser.setWeight(user.getWeight());
            currentUser.setFitnessGoal(user.getFitnessGoal());
            
            userService.save(currentUser);
            
            redirectAttributes.addFlashAttribute("success", "Cap nhat thanh cong!");
            return "redirect:/profile";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Loi cap nhat: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User user = userService.findByEmail(email);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Not found ");
                return "redirect:/profile";
            }
            

            if (!userService.checkPassword(user, currentPassword)) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
                return "redirect:/profile";
            }
            

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match");
                return "redirect:/profile";
            }
            
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters");
                return "redirect:/profile";
            }
            

            userService.updatePassword(user, newPassword);
            
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/profile";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error changing password: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}
