package com.dhd.gymmanagement.controller;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;

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
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
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

            User updatedUser = userService.save(currentUser);


            session.setAttribute("user", updatedUser);

            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
            return "redirect:/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @PostMapping("/update-avatar")
    public String updateAvatar(@RequestParam("avatarFile") MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes,
                               HttpSession session) { // << THÊM HttpSession
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            userService.updateAvatar(email, avatarFile);


            User updatedUser = userService.findByEmail(email);
            session.setAttribute("user", updatedUser);

            redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh đại diện thành công!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải ảnh lên: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
        }

        return "redirect:/profile";
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
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng");
                return "redirect:/profile";
            }

            if (!userService.checkPassword(user, currentPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không chính xác");
                return "redirect:/profile";
            }

            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp");
                return "redirect:/profile";
            }

            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự");
                return "redirect:/profile";
            }

            userService.updatePassword(user, newPassword);

            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
            return "redirect:/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi đổi mật khẩu: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}