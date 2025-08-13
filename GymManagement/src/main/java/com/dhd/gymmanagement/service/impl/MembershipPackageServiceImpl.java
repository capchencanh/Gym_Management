package com.dhd.gymmanagement.service.impl;

import com.dhd.gymmanagement.entity.MembershipPackage;
import com.dhd.gymmanagement.repository.MembershipPackageRepository;
import com.dhd.gymmanagement.service.MembershipPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class MembershipPackageServiceImpl implements MembershipPackageService {

    @Autowired
    private MembershipPackageRepository packageRepository;

    @Override
    public List<MembershipPackage> getAllActivePackages() {
        return packageRepository.findAllActive();
    }

    @Override
    public Optional<MembershipPackage> getPackageById(Integer packageId) {
        return packageRepository.findById(packageId);
    }

    @Override
    public Optional<MembershipPackage> getPackageByName(String name) {
        return packageRepository.findByNameAndNotDeleted(name);
    }

    @Override
    public MembershipPackage createPackage(MembershipPackage packageData) {
        System.out.println("DEBUG: Service - Creating package: " + packageData.getName());
        System.out.println("DEBUG: Service - Package isDeleted before: " + packageData.getIsDeleted());
        
        // Kiểm tra tên gói tập đã tồn tại
        if (isPackageNameExists(packageData.getName())) {
            throw new RuntimeException("Tên gói tập đã tồn tại: " + packageData.getName());
        }
        
        // Thiết lập thời gian tạo và cập nhật
        LocalDateTime localNow = LocalDateTime.now();
        // Làm tròn xuống giây, loại bỏ microsecond
        localNow = localNow.withNano(0);
        Timestamp now = Timestamp.valueOf(localNow);
        packageData.setCreatedAt(now);
        packageData.setUpdatedAt(now);
        packageData.setIsDeleted(0);
        
        System.out.println("DEBUG: Service - Package isDeleted after: " + packageData.getIsDeleted());
        
        try {
            MembershipPackage savedPackage = packageRepository.save(packageData);
            System.out.println("DEBUG: Service - Package saved successfully: " + savedPackage.getPackageId());
            return savedPackage;
        } catch (Exception e) {
            System.err.println("DEBUG: Service - Error saving package: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public MembershipPackage updatePackage(Integer packageId, MembershipPackage packageData) {
        Optional<MembershipPackage> existingPackage = packageRepository.findById(packageId);
        if (existingPackage.isEmpty()) {
            throw new RuntimeException("Không tìm thấy gói tập với ID: " + packageId);
        }
        
        MembershipPackage packageToUpdate = existingPackage.get();
        
        // Kiểm tra nếu thay đổi tên thì tên mới không được trùng
        if (!packageToUpdate.getName().equals(packageData.getName()) && 
            isPackageNameExists(packageData.getName())) {
            throw new RuntimeException("Tên gói tập đã tồn tại: " + packageData.getName());
        }
        
        // Cập nhật thông tin
        packageToUpdate.setName(packageData.getName());
        packageToUpdate.setDurationMonths(packageData.getDurationMonths());
        packageToUpdate.setPrice(packageData.getPrice());
        packageToUpdate.setDescription(packageData.getDescription());
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        Timestamp now = Timestamp.valueOf(localNow);
        packageToUpdate.setUpdatedAt(now);
        
        return packageRepository.save(packageToUpdate);
    }

    @Override
    public boolean softDeletePackage(Integer packageId) {
        Optional<MembershipPackage> packageOpt = packageRepository.findById(packageId);
        if (packageOpt.isEmpty()) {
            return false;
        }
        
        MembershipPackage packageToDelete = packageOpt.get();
        packageToDelete.setIsDeleted(1);
        
        LocalDateTime localNow = LocalDateTime.now();
        localNow = localNow.withNano(0);
        Timestamp now = Timestamp.valueOf(localNow);
        packageToDelete.setUpdatedAt(now);
        
        packageRepository.save(packageToDelete);
        return true;
    }

    @Override
    public boolean hardDeletePackage(Integer packageId) {
        if (packageRepository.existsById(packageId)) {
            packageRepository.deleteById(packageId);
            return true;
        }
        return false;
    }

    @Override
    public List<MembershipPackage> findPackagesByPriceRange(Double minPrice, Double maxPrice) {
        return packageRepository.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    public List<MembershipPackage> findPackagesByDuration(Integer duration) {
        return packageRepository.findByDuration(duration);
    }

    @Override
    public boolean isPackageNameExists(String name) {
        return packageRepository.existsByNameAndNotDeleted(name);
    }

    @Override
    public long countActivePackages() {
        return packageRepository.countActivePackages();
    }
    
    @Override
    public long calculateTotalRevenue() {
        Double revenue = packageRepository.calculateTotalRevenue();
        return revenue != null ? revenue.longValue() : 0L;
    }
    
    @Override
    public long calculateMonthlyRevenue() {
        Double revenue = packageRepository.calculateMonthlyRevenue();
        return revenue != null ? revenue.longValue() : 0L;
    }
    
    @Override
    public long getMostPopularPackageCount() {
        List<MembershipPackage> popularPackages = packageRepository.findMostExpensivePackages();
        return popularPackages.isEmpty() ? 0 : popularPackages.size();
    }
}
