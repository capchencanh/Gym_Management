package com.dhd.gymmanagement.controller;

import com.dhd.gymmanagement.service.CategoryService;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.service.DeviceService;
import com.dhd.gymmanagement.service.MembershipPackageService;
import com.dhd.gymmanagement.service.PTAssignmentService;
import com.dhd.gymmanagement.entity.Device;
import com.dhd.gymmanagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DeviceService deviceService;
    
    @Autowired
    private MembershipPackageService packageService;
    
    @Autowired
    private PTAssignmentService ptAssignmentService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "index";
    }
    
    @GetMapping("/admin")
    public String admin(Model model, @RequestParam(required = false) Integer categoryId) {
        if (categoryId != null) {
            switch (categoryId) {
                case 7:
                    return "redirect:/admin/users";
                case 8:
                    return "redirect:/admin/trainers";
                case 9:
                    return "redirect:/admin/classes";
                case 10:
                    return "redirect:/admin/packages";
                case 11:
                    return "redirect:/admin/payments";
                case 12:
                    return "redirect:/admin/reports";
                case 13:
                    return "redirect:/admin/devices";
                default:
                    break;
            }
        }
        

        long totalUsers = userService.countUsers();
        long ptCount = userService.getUsersByRole(User.Role.PT).size();
        long userCount = userService.getUsersByRole(User.Role.USER).size();
        long adminCount = userService.getUsersByRole(User.Role.ADMIN).size();
        long totalPackages = packageService.countActivePackages();
        

        long activePackages = packageService.countActivePackages();
        Double totalPackageRevenue = packageService.calculateTotalRevenue();
        Double monthlyPackageRevenue = packageService.calculateMonthlyRevenue();
        long popularPackageCount = packageService.getMostPopularPackageCount();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalTrainers", ptCount);
        model.addAttribute("totalClasses", 0);
        model.addAttribute("totalDevices", deviceService.countTotalDevices());
        model.addAttribute("totalPackages", totalPackages);
        

        model.addAttribute("activePackages", activePackages);
        model.addAttribute("totalPackageRevenue", totalPackageRevenue);
        model.addAttribute("monthlyPackageRevenue", monthlyPackageRevenue);
        model.addAttribute("popularPackageCount", popularPackageCount);
        
        model.addAttribute("userCount", userCount);
        model.addAttribute("ptCount", ptCount);
        model.addAttribute("activeClasses", 0);
        model.addAttribute("activeDevices", deviceService.countDevicesByStatus(Device.DeviceStatus.IN_USE));
        

        model.addAttribute("totalAssignments", ptAssignmentService.countTotalAssignments());
        model.addAttribute("pendingAssignments", ptAssignmentService.countPendingAssignments());
        model.addAttribute("activeAssignments", ptAssignmentService.countActiveAssignments());
        model.addAttribute("completedAssignments", ptAssignmentService.countCompletedAssignments());
        
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/dashboard";
    }
} 
