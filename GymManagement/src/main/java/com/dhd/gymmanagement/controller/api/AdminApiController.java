package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.service.UserMembershipService;
import com.dhd.gymmanagement.service.MembershipPackageService;
import com.dhd.gymmanagement.repository.UserRepository;
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
    
    @Autowired
    private MembershipPackageService membershipPackageService;
    
    @Autowired
    private UserRepository userRepository;

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
                Map<String, Object> response = new java.util.HashMap<>();
                response.put("success", true);
                response.put("message", "Gia hạn gói tập thành công");
                response.put("userId", userId);
                response.put("packageId", packageId);
                response.put("months", months);
                
                // Thêm thông tin user và package
                try {
                    com.dhd.gymmanagement.entity.User user = userRepository.findById(userId).orElse(null);
                    if (user != null) {
                        response.put("userName", user.getName() != null ? user.getName() : "N/A");
                    }
                    
                    com.dhd.gymmanagement.entity.MembershipPackage pkg = membershipPackageService.getPackageById(packageId).orElse(null);
                    if (pkg != null) {
                        response.put("packageName", pkg.getName() != null ? pkg.getName() : "N/A");
                    }
                    
                    // Lấy thông tin membership mới
                    java.util.List<com.dhd.gymmanagement.entity.UserMembership> memberships = userMembershipService.getUserMembershipsByUserId(userId);
                    com.dhd.gymmanagement.entity.UserMembership latestMembership = memberships.stream()
                            .filter(m -> m.getPackageId().equals(packageId) && m.getIsDeleted() == 0)
                            .sorted((m1, m2) -> m2.getUpdatedAt().compareTo(m1.getUpdatedAt()))
                            .findFirst().orElse(null);
                    
                    if (latestMembership != null && latestMembership.getEndDate() != null) {
                        response.put("newEndDate", latestMembership.getEndDate().toString());
                    }
                } catch (Exception e) {
                    // Không làm gì, chỉ thiếu thông tin thêm
                }
                
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
    
    @GetMapping("/users/{userId}/memberships")
    public ResponseEntity<List<Map<String, Object>>> getUserMemberships(@PathVariable Integer userId) {
        try {
            List<com.dhd.gymmanagement.entity.UserMembership> memberships = userMembershipService.getUserMembershipsByUserId(userId);
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            
            for (com.dhd.gymmanagement.entity.UserMembership membership : memberships) {
                Map<String, Object> membershipData = new java.util.HashMap<>();
                membershipData.put("membershipId", membership.getMembershipId());
                membershipData.put("packageId", membership.getPackageId());
                membershipData.put("startDate", membership.getStartDate());
                membershipData.put("endDate", membership.getEndDate());
                membershipData.put("status", membership.getStatus().toString());
                
                // Thêm thông tin package
                try {
                    com.dhd.gymmanagement.entity.MembershipPackage pkg = membershipPackageService.getPackageById(membership.getPackageId()).orElse(null);
                    if (pkg != null) {
                        membershipData.put("packageName", pkg.getName());
                    }
                } catch (Exception e) {
                    membershipData.put("packageName", "N/A");
                }
                
                result.add(membershipData);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/packages/{packageId}/active-users")
    public ResponseEntity<List<Map<String, Object>>> getActiveUsersByPackageId(@PathVariable Integer packageId) {
        try {
            List<com.dhd.gymmanagement.entity.UserMembership> memberships = userMembershipService.getUserMembershipsByUserId(packageId);
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            
            // Lấy tất cả memberships của package này
            List<com.dhd.gymmanagement.entity.UserMembership> allMemberships = userMembershipService.getAllUserMemberships();
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
            
            for (com.dhd.gymmanagement.entity.UserMembership membership : allMemberships) {
                if (membership.getPackageId().equals(packageId) && membership.getIsDeleted() == 0) {
                    Map<String, Object> userData = new java.util.HashMap<>();
                    userData.put("membershipId", membership.getMembershipId());
                    userData.put("userId", membership.getUserId());
                    userData.put("packageId", membership.getPackageId());
                    userData.put("startDate", membership.getStartDate());
                    userData.put("endDate", membership.getEndDate());
                    userData.put("status", membership.getStatus().toString());
                    
                    // Thêm thông tin user
                    try {
                        com.dhd.gymmanagement.entity.User user = userRepository.findById(membership.getUserId()).orElse(null);
                        if (user != null) {
                            userData.put("userName", user.getName() != null ? user.getName() : "N/A");
                            userData.put("userEmail", user.getEmail() != null ? user.getEmail() : "N/A");
                            userData.put("userPhone", user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
                        }
                    } catch (Exception e) {
                        userData.put("userName", "N/A");
                        userData.put("userEmail", "N/A");
                        userData.put("userPhone", "N/A");
                    }
                    
                    result.add(userData);
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
