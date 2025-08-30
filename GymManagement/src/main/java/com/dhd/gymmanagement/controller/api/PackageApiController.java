package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.entity.MembershipPackage;
import com.dhd.gymmanagement.service.MembershipPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/packages")
@CrossOrigin(origins = "http://localhost:3000")
public class PackageApiController {
    
    @Autowired
    private MembershipPackageService membershipPackageService;
    
    @GetMapping
    public ResponseEntity<List<MembershipPackage>> getAllActivePackages() {
        try {
            List<MembershipPackage> packages = membershipPackageService.getAllActivePackages();
            return ResponseEntity.ok(packages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MembershipPackage> getPackageById(@PathVariable Integer id) {
        try {
            Optional<MembershipPackage> packageOpt = membershipPackageService.getPackageById(id);
            if (packageOpt.isPresent()) {
                return ResponseEntity.ok(packageOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<MembershipPackage>> searchPackages(@RequestParam String keyword) {
        try {
            List<MembershipPackage> packages = membershipPackageService.getAllActivePackages();
            List<MembershipPackage> filteredPackages = packages.stream()
                .filter(pkg -> pkg.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                              pkg.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
            return ResponseEntity.ok(filteredPackages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
