package com.dhd.gymmanagement.controller;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.service.PasswordResetService;
import com.dhd.gymmanagement.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;




@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private PasswordResetService passwordResetService;


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";
    }


    @PostMapping("/login")
    public String login(@RequestParam String email,
                       @RequestParam String password,
                       @RequestParam(required = false) String redirect,
                       HttpSession session,
                       HttpServletResponse response,
                       RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu không đúng");
                return "redirect:/login";
            }

            String jwtToken = JwtUtils.generateToken(
                user.getUserId().longValue(), 
                user.getEmail(), 
                user.getRole().name()
            );
            
            Cookie jwtCookie = new Cookie("jwt_token", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(86400);
            response.addCookie(jwtCookie);
            
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole().name());
            session.setAttribute("jwtToken", jwtToken);

            redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công! Chào mừng " + user.getName());
            

            if (redirect != null && !redirect.isEmpty()) {
                return "redirect:" + redirect;
            }
            


            switch (user.getRole()) {
                case ADMIN:
                    return "redirect:/admin";
                case PT:
                    return "redirect:/pt/dashboard";
                default:
                    return "redirect:/"; // User thường
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }


    @PostMapping("/register")
    public String register(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String phoneNumber,
                          @RequestParam String passwordHash,
                          @RequestParam(required = false) String confirmPassword,
                          @RequestParam String gender,
                          @RequestParam String role,
                          @RequestParam(required = false) String birthdate,
                          @RequestParam(required = false) Double height,
                          @RequestParam(required = false) Double weight,
                          @RequestParam(required = false) String fitnessGoal,
                          RedirectAttributes redirectAttributes) {
        try {

            if (confirmPassword != null && !passwordHash.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
                return "redirect:/register";
            }


            User.Role selectedRole = User.Role.valueOf(role);

            if (userService.getUserByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng");
                return "redirect:/register";
            }

            if (userService.getUserByPhoneNumber(phoneNumber).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại đã được sử dụng");
                return "redirect:/register";
            }

            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setPasswordHash(passwordHash);
            newUser.setGender(gender);
            newUser.setRole(selectedRole);
            newUser.setFitnessGoal(fitnessGoal);

            if (birthdate != null && !birthdate.trim().isEmpty()) {
                try {
                    newUser.setBirthdate(java.sql.Date.valueOf(birthdate));
                } catch (Exception e) {

                }
            }

            if (height != null) newUser.setHeight(height);
            if (weight != null) newUser.setWeight(weight);

            userService.createUser(newUser);

            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        
        Cookie jwtCookie = new Cookie("jwt_token", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        jwtCookie.setDomain(null);
        response.addCookie(jwtCookie);
        
        Cookie clearCookie = new Cookie("jwt_token", null);
        clearCookie.setHttpOnly(true);
        clearCookie.setSecure(false);
        clearCookie.setPath("/");
        clearCookie.setMaxAge(0);
        response.addCookie(clearCookie);
        
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Đăng xuất thành công!");
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            boolean emailSent = passwordResetService.sendPasswordResetEmail(email);
            
            if (emailSent) {
                redirectAttributes.addFlashAttribute("message", 
                    "Link đặt lại mật khẩu đã được gửi đến email: " + email);
                redirectAttributes.addFlashAttribute("messageType", "success");
            } else {
                redirectAttributes.addFlashAttribute("message", 
                    "Email không tồn tại trong hệ thống");
                redirectAttributes.addFlashAttribute("messageType", "error");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        
        return "redirect:/forgot-password";
    }

    @GetMapping("/create-admin")
    public String createDefaultAdmin(RedirectAttributes redirectAttributes) {
        try {
            if (userService.getUserByEmail("admin@gym.com").isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Admin đã tồn tại!");
                return "redirect:/login";
            }
            
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail("admin@gym.com");
            admin.setPhoneNumber("0123456789");
            admin.setRole(User.Role.ADMIN);
            admin.setGender("Nam");
            
            userService.createUser(admin);
            
            redirectAttributes.addFlashAttribute("success", "Tạo admin thành công! Email: admin@gym.com, Password: 123456");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        if (passwordResetService.isValidToken(token)) {
            model.addAttribute("token", token);
            return "reset_password";
        } else {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn");
            return "forgot_password";
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("message", "Mật khẩu xác nhận không khớp");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/reset-password?token=" + token;
        }
        
        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("message", "Mật khẩu phải có ít nhất 6 ký tự");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/reset-password?token=" + token;
        }
        
        try {
            boolean passwordReset = passwordResetService.resetPassword(token, password);
            if (passwordReset) {
                redirectAttributes.addFlashAttribute("message", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới");
                redirectAttributes.addFlashAttribute("messageType", "success");
                return "redirect:/login";
            } else {
                redirectAttributes.addFlashAttribute("message", "Token không hợp lệ hoặc đã hết hạn");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/forgot-password";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/reset-password?token=" + token;
        }
    }
}
