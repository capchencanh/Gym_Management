package com.dhd.gymmanagement.controller.admin;

import com.dhd.gymmanagement.entity.Device;
import com.dhd.gymmanagement.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/devices")
public class AdminDeviceController {
    
    @Autowired
    private DeviceService deviceService;
    
    @GetMapping
    public String listDevices(Model model, 
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String type,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String location) {
        
        List<Device> devices;
        
        Device.DeviceStatus deviceStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                deviceStatus = Device.DeviceStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
            }
        }
        
        if (name != null && !name.trim().isEmpty() || 
            type != null && !type.trim().isEmpty() || 
            deviceStatus != null || 
            location != null && !location.trim().isEmpty()) {
            devices = deviceService.searchDevices(name, type, deviceStatus, location);
        } else {
            devices = deviceService.getAllDevices();
        }
        
        System.out.println("Total devices found: " + devices.size());
        devices.forEach(device -> {
            System.out.println("Device: ID=" + device.getDeviceId() + 
                             ", Name=" + device.getName() + 
                             ", Status=" + device.getStatus() + 
                             ", IsDeleted=" + device.getIsDeleted());
        });
        
        List<Device> allDevicesDebug = deviceService.getAllDevicesForDebug();
        System.out.println("All devices in DB: " + allDevicesDebug.size());
        allDevicesDebug.forEach(device -> {
            System.out.println("All Device: ID=" + device.getDeviceId() + 
                             ", Name=" + device.getName() + 
                             ", Status=" + device.getStatus() + 
                             ", IsDeleted=" + device.getIsDeleted());
        });
        
        List<Device> allDevices = deviceService.getAllDevicesForStats();
        long totalDevices = allDevices.size();
        long availableCount = allDevices.stream().filter(d -> d.getStatus() == Device.DeviceStatus.AVAILABLE).count();
        long inUseCount = allDevices.stream().filter(d -> d.getStatus() == Device.DeviceStatus.IN_USE).count();
        long brokenCount = allDevices.stream().filter(d -> d.getStatus() == Device.DeviceStatus.BROKEN).count();
        long maintenanceCount = allDevices.stream().filter(d -> d.getStatus() == Device.DeviceStatus.MAINTENANCE).count();
        
        model.addAttribute("devices", devices);
        model.addAttribute("name", name);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("location", location);
        model.addAttribute("statuses", Device.DeviceStatus.values());
        model.addAttribute("totalDevices", totalDevices);
        model.addAttribute("availableCount", availableCount);
        model.addAttribute("inUseCount", inUseCount);
        model.addAttribute("brokenCount", brokenCount);
        model.addAttribute("maintenanceCount", maintenanceCount);
        
        return "admin/device/list";
    }
    
    @GetMapping("/create")
    public String createDeviceForm(Model model) {
        model.addAttribute("device", new Device());
        model.addAttribute("statuses", Device.DeviceStatus.values());
        return "admin/device/form";
    }
    
    @PostMapping("/create")
    public String createDevice(@ModelAttribute Device device, RedirectAttributes redirectAttributes) {
        try {
            deviceService.createDevice(device);
            redirectAttributes.addFlashAttribute("success", "Thêm thiết bị thành công!");
            return "redirect:/admin/devices";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm thiết bị: " + e.getMessage());
            return "redirect:/admin/devices/create";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editDeviceForm(@PathVariable Integer id, Model model) {
        try {
            Device device = deviceService.getDeviceById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
            model.addAttribute("device", device);
            model.addAttribute("statuses", Device.DeviceStatus.values());
            return "admin/device/form";
        } catch (Exception e) {
            return "redirect:/admin/devices";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String editDevice(@PathVariable Integer id, @ModelAttribute Device device, RedirectAttributes redirectAttributes) {
        try {
            deviceService.updateDevice(id, device);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thiết bị thành công!");
            return "redirect:/admin/devices";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật thiết bị: " + e.getMessage());
            return "redirect:/admin/devices/edit/" + id;
        }
    }
    
    @PostMapping("/delete/{id}")
    public String deleteDevice(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            deviceService.deleteDevice(id);
            redirectAttributes.addFlashAttribute("success", "Xóa thiết bị thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa thiết bị: " + e.getMessage());
        }
        return "redirect:/admin/devices";
    }
    
    @GetMapping("/view/{id}")
    public String viewDevice(@PathVariable Integer id, Model model) {
        try {
            Device device = deviceService.getDeviceById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));
            
            long availableCount = deviceService.countDevicesByStatus(Device.DeviceStatus.AVAILABLE);
            long inUseCount = deviceService.countDevicesByStatus(Device.DeviceStatus.IN_USE);
            long brokenCount = deviceService.countDevicesByStatus(Device.DeviceStatus.BROKEN);
            long maintenanceCount = deviceService.countDevicesByStatus(Device.DeviceStatus.MAINTENANCE);
            
            model.addAttribute("device", device);
            model.addAttribute("availableCount", availableCount);
            model.addAttribute("inUseCount", inUseCount);
            model.addAttribute("brokenCount", brokenCount);
            model.addAttribute("maintenanceCount", maintenanceCount);
            
            return "admin/device/view";
        } catch (Exception e) {
            return "redirect:/admin/devices";
        }
    }
    
    @PostMapping("/update-status/{id}")
    public String updateDeviceStatus(@PathVariable Integer id, 
                                   @RequestParam Device.DeviceStatus status,
                                   RedirectAttributes redirectAttributes) {
        try {
            deviceService.updateDeviceStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thiết bị thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/devices/view/" + id;
    }
    
    @PostMapping("/add-note/{id}")
    public String addNote(@PathVariable Integer id, 
                         @RequestParam String note,
                         RedirectAttributes redirectAttributes) {
        try {
            deviceService.addMaintenanceNote(id, note);
            redirectAttributes.addFlashAttribute("success", "Thêm ghi chú thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/devices/view/" + id;
    }
} 