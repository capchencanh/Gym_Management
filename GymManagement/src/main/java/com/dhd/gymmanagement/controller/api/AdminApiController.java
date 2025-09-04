package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.service.UserMembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminApiController {

    @Autowired
    private UserMembershipService userMembershipService;

    @GetMapping("/packages/{packageId}/expired-memberships")
    public ResponseEntity<List<Map<String, Object>>> getExpiredMembershipsByPackageId(@PathVariable Integer packageId) {
        try {
            List<Map<String, Object>> expiredMemberships = userMembershipService.getExpiredMembershipsByPackageId(packageId);
            return ResponseEntity.ok(expiredMemberships);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/memberships/{userId}/renew")
    public ResponseEntity<Map<String, Object>> renewMembership(@PathVariable Integer userId, 
                                                             @RequestBody Map<String, Object> request) {
        try {
            Object packageIdObj = request.get("packageId");
            Integer packageId;
            if (packageIdObj instanceof Integer) {
                packageId = (Integer) packageIdObj;
            } else if (packageIdObj instanceof String) {
                try {
                    packageId = Integer.parseInt((String) packageIdObj);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid packageId format"));
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid packageId"));
            }
            
            Object monthsObj = request.get("months");
            Integer months;
            if (monthsObj instanceof Integer) {
                months = (Integer) monthsObj;
            } else if (monthsObj instanceof String) {
                try {
                    months = Integer.parseInt((String) monthsObj);
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid months format"));
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing or invalid months"));
            }
            
            if (packageId == null || months == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing packageId or months"));
            }
            
            boolean success = userMembershipService.renewMembership(userId, packageId, months);
            
            if (success) {
                Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "Gia hạn gói tập thành công",
                    "userId", userId,
                    "packageId", packageId,
                    "months", months
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Không thể gia hạn gói tập"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Lỗi server: " + e.getMessage()
            ));
        }
    }
}
