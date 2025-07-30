package com.dhd.gymmanagement.controller;

import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                case USER:
                    return "redirect:/user/dashboard";
                default:
                    return "redirect:/";
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
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          @RequestParam String gender,
                          @RequestParam(required = false) String birthdate,
                          @RequestParam(required = false) Double height,
                          @RequestParam(required = false) Double weight,
                          @RequestParam(required = false) String fitnessGoal,
                          RedirectAttributes redirectAttributes) {
        try {

            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp");
                return "redirect:/register";
            }


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
            newUser.setPasswordHash(password);
            newUser.setGender(gender);
            newUser.setRole(User.Role.USER);
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
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

            redirectAttributes.addFlashAttribute("success", 
                "Hướng dẫn đặt lại mật khẩu đã được gửi đến email: " + email);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
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


}
