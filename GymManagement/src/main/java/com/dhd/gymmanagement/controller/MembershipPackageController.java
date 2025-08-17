package com.dhd.gymmanagement.controller;

import com.dhd.gymmanagement.entity.MembershipPackage;
import com.dhd.gymmanagement.service.MembershipPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin/packages")
public class MembershipPackageController {

    @Autowired
    private MembershipPackageService packageService;


    @GetMapping
    public String listPackages(Model model) {
        try {
            List<MembershipPackage> packages = packageService.getAllActivePackages();
            long totalCount = packageService.countActivePackages();
            
            model.addAttribute("packages", packages);
            model.addAttribute("totalPackages", totalCount);
            
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi tải dữ liệu: " + e.getMessage());
            model.addAttribute("packages", new ArrayList<>());
            model.addAttribute("totalPackages", 0);
        }
        
        return "admin/packages/list";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("package", new MembershipPackage());
        return "admin/packages/form";
    }


    @PostMapping("/create")
    public String createPackage(@ModelAttribute MembershipPackage packageData, 
                               RedirectAttributes redirectAttributes) {
        try {
            packageService.createPackage(packageData);
            redirectAttributes.addFlashAttribute("success", "Tạo gói tập thành công!");
            return "redirect:/admin/packages";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi tạo gói tập: " + e.getMessage());
            redirectAttributes.addFlashAttribute("package", packageData);
            return "redirect:/admin/packages/create";
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, 
                              RedirectAttributes redirectAttributes) {
        try {
            var packageOpt = packageService.getPackageById(id);
            if (packageOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy gói tập!");
                return "redirect:/admin/packages";
            }
            
            model.addAttribute("package", packageOpt.get());
            return "admin/packages/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/packages";
        }
    }


    @PostMapping("/edit/{id}")
    public String updatePackage(@PathVariable Integer id, 
                               @ModelAttribute MembershipPackage packageData,
                               RedirectAttributes redirectAttributes) {
        try {
            packageService.updatePackage(id, packageData);
            redirectAttributes.addFlashAttribute("success", "Cập nhật gói tập thành công!");
            return "redirect:/admin/packages";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi cập nhật gói tập: " + e.getMessage());
            redirectAttributes.addFlashAttribute("package", packageData);
            return "redirect:/admin/packages/edit/" + id;
        }
    }


    @PostMapping("/delete/{id}")
    public String deletePackage(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = packageService.softDeletePackage(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Xóa gói tập thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy gói tập để xóa!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa gói tập: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }


    @PostMapping("/hard-delete/{id}")
    public String hardDeletePackage(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = packageService.hardDeletePackage(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Xóa hoàn toàn gói tập thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy gói tập để xóa!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xóa hoàn toàn gói tập: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }


    @GetMapping("/search/price")
    public String searchByPrice(@RequestParam Double minPrice, 
                               @RequestParam Double maxPrice, 
                               Model model) {
        List<MembershipPackage> packages = packageService.findPackagesByPriceRange(minPrice, maxPrice);
        model.addAttribute("packages", packages);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("totalPackages", packages.size());
        

        model.addAttribute("selectedDuration", null);
        
        return "admin/packages/list";
    }


    @GetMapping("/search/duration")
    public String searchByDuration(@RequestParam Integer duration, Model model) {
        List<MembershipPackage> packages = packageService.findPackagesByDuration(duration);
        
        model.addAttribute("packages", packages);
        model.addAttribute("selectedDuration", duration);
        model.addAttribute("totalPackages", packages.size());
        model.addAttribute("minPrice", null);
        model.addAttribute("maxPrice", null);
        
        return "admin/packages/list";
    }


    @GetMapping("/view/{id}")
    public String viewPackage(@PathVariable Integer id, Model model, 
                             RedirectAttributes redirectAttributes) {
        try {
            var packageOpt = packageService.getPackageById(id);
            if (packageOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy gói tập!");
                return "redirect:/admin/packages";
            }
            
            model.addAttribute("package", packageOpt.get());
            return "admin/packages/view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/packages";
        }
    }
}
