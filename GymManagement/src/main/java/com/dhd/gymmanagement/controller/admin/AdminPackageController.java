package com.dhd.gymmanagement.controller.admin;

import com.dhd.gymmanagement.entity.MembershipPackage;
import com.dhd.gymmanagement.service.MembershipPackageService;
import com.dhd.gymmanagement.service.MoMoPaymentService;
import com.dhd.gymmanagement.service.UserMembershipService;
import com.dhd.gymmanagement.service.PaymentService;
import com.dhd.gymmanagement.entity.UserMembership;
import com.dhd.gymmanagement.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/packages")
public class AdminPackageController {
    
    @Autowired
    private MembershipPackageService membershipPackageService;
    @Autowired
    private MoMoPaymentService moMoPaymentService;
    @Autowired
    private UserMembershipService userMembershipService;
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping
    public String listPackages(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort;
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        } else {
            sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "name");
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<MembershipPackage> packagesPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            packagesPage = membershipPackageService.searchPackages(keyword, pageable);
        } else {
            packagesPage = membershipPackageService.getAllPackages(pageable);
        }
        
        long totalPackages = membershipPackageService.countActivePackages();
        Double totalRevenue = membershipPackageService.calculateTotalRevenue();
        Double monthlyRevenue = membershipPackageService.calculateMonthlyRevenue();
        
        model.addAttribute("packages", packagesPage.getContent());
        java.util.Map<Integer, Long> totalPaymentCounts = new java.util.HashMap<>();
        java.util.Map<Integer, java.util.List<java.util.Map<String, Object>>> expiredMemberships = new java.util.HashMap<>();
        
        for (MembershipPackage mp : packagesPage.getContent()) {
            Integer packageId = mp.getPackageId();
            long momoCount = moMoPaymentService.countCompletedByPackageId(packageId);
            long cashCount = paymentService.countCompletedByPackageId(packageId);
            totalPaymentCounts.put(packageId, momoCount + cashCount);
            
            java.util.List<java.util.Map<String, Object>> expiredList = userMembershipService.getExpiredMembershipsByPackageId(packageId);
            if (!expiredList.isEmpty()) {
                expiredMemberships.put(packageId, expiredList);
            }
        }
        
        model.addAttribute("totalPaymentCounts", totalPaymentCounts);
        model.addAttribute("expiredMemberships", expiredMemberships);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", packagesPage.getTotalPages());
        model.addAttribute("totalItems", packagesPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("totalPackages", totalPackages);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        
        return "admin/packages/list";
    }
    
    @GetMapping("/create")
    public String createPackageForm(Model model) {
        model.addAttribute("package", new MembershipPackage());
        return "admin/packages/form";
    }
    
    @GetMapping("/new")
    public String newPackageForm(Model model) {
        return createPackageForm(model);
    }
    
    @PostMapping("/create")
    public String createPackage(@ModelAttribute MembershipPackage membershipPackage,
                               RedirectAttributes redirectAttributes) {
        try {
            membershipPackageService.createPackage(membershipPackage);
            redirectAttributes.addFlashAttribute("success", "Tạo gói thành viên thành công!");
            return "redirect:/admin/packages";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo gói thành viên: " + e.getMessage());
            return "redirect:/admin/packages/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editPackageForm(@PathVariable Integer id, Model model) {
        try {
            Optional<MembershipPackage> packageOpt = membershipPackageService.getPackageById(id);
            if (packageOpt.isPresent()) {
                model.addAttribute("package", packageOpt.get());
                return "admin/packages/form";
            } else {
                return "redirect:/admin/packages";
            }
        } catch (Exception e) {
            return "redirect:/admin/packages";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String editPackage(@PathVariable Integer id,
                             @ModelAttribute MembershipPackage membershipPackage,
                             RedirectAttributes redirectAttributes) {
        try {
            membershipPackage.setPackageId(id);
            membershipPackageService.updatePackage(membershipPackage);
            redirectAttributes.addFlashAttribute("success", "Cập nhật gói thành viên thành công!");
            return "redirect:/admin/packages";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật gói thành viên: " + e.getMessage());
            return "redirect:/admin/packages/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deletePackage(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            membershipPackageService.deletePackage(id);
            redirectAttributes.addFlashAttribute("success", "Xóa gói thành viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa gói thành viên: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }
    
    @GetMapping("/view/{id}")
    public String viewPackage(@PathVariable Integer id, Model model) {
        try {
            Optional<MembershipPackage> packageOpt = membershipPackageService.getPackageById(id);
            if (packageOpt.isPresent()) {
                model.addAttribute("package", packageOpt.get());
                model.addAttribute("momoPayments", moMoPaymentService.getPaymentsByPackageId(id));
                model.addAttribute("cashPayments", paymentService.getPaymentsByPackageId(id));
                return "admin/packages/view";
            } else {
                return "redirect:/admin/packages";
            }
        } catch (Exception e) {
            return "redirect:/admin/packages";
        }
    }

    @PostMapping("/cash-pay/{id}")
    public String cashPay(@PathVariable Integer id,
                          @RequestParam Integer userId,
                          @RequestParam(required = false) String notes,
                          RedirectAttributes redirectAttributes) {
        try {
            java.util.Optional<MembershipPackage> pkgOpt = membershipPackageService.getPackageById(id);
            if (pkgOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy gói");
                return "redirect:/admin/packages";
            }
            MembershipPackage pkg = pkgOpt.get();

            java.sql.Timestamp start = new java.sql.Timestamp(System.currentTimeMillis());
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(start);
            cal.add(java.util.Calendar.MONTH, pkg.getDurationMonths());
            java.sql.Timestamp end = new java.sql.Timestamp(cal.getTimeInMillis());

            UserMembership um = new UserMembership();
            um.setUserId(userId);
            um.setPackageId(id);
            um.setStartDate(start);
            um.setEndDate(end);
            um.setStatus(UserMembership.MembershipStatus.ACTIVE);
            UserMembership saved = userMembershipService.createUserMembership(um);

            Payment p = new Payment();
            p.setMembershipId(saved.getMembershipId());
            p.setAmount(pkg.getPrice());
            p.setPaymentMethod(Payment.PaymentMethod.CASH);
            p.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
            p.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));
            p.setNotes(notes != null ? notes : ("Thanh toán tiền mặt cho gói " + pkg.getName()));
            p.setIsDeleted(0);
            paymentService.createPayment(p);

            redirectAttributes.addFlashAttribute("success", "Ghi nhận thanh toán tiền mặt thành công cho User ID " + userId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi ghi nhận tiền mặt: " + e.getMessage());
        }
        return "redirect:/admin/packages";
    }
}
