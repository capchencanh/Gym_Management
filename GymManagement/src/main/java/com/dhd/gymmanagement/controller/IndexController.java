package com.dhd.gymmanagement.controller;

import com.dhd.gymmanagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;

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
        
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/dashboard";
    }
} 