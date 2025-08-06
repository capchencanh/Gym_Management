package com.dhd.gymmanagement.controller;

import com.dhd.gymmanagement.service.CategoryService;
import com.dhd.gymmanagement.service.UserService;
import com.dhd.gymmanagement.service.TrainerService;
import com.dhd.gymmanagement.service.DeviceService;
import com.dhd.gymmanagement.entity.Device;
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
    private TrainerService trainerService;
    
    @Autowired
    private DeviceService deviceService;

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
        

        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalTrainers", trainerService.getAllTrainers().size());
        model.addAttribute("totalClasses", 0);
        model.addAttribute("totalDevices", deviceService.countTotalDevices());
        

        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("ptCount", trainerService.getAllTrainers().size());
        model.addAttribute("activeClasses", 0);
        model.addAttribute("activeDevices", deviceService.countDevicesByStatus(Device.DeviceStatus.IN_USE));
        
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/dashboard";
    }
} 
