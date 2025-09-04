package com.dhd.gymmanagement.service.impl;

import com.dhd.gymmanagement.entity.MembershipPackage;
import com.dhd.gymmanagement.repository.MembershipPackageRepository;
import com.dhd.gymmanagement.service.MembershipPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        
        try {
            return packageRepository.save(packageData);
        } catch (Exception e) {
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
    public Page<MembershipPackage> getAllPackages(Pageable pageable) {
        return packageRepository.findAllActive(pageable);
    }
    
    @Override
    public MembershipPackage updatePackage(MembershipPackage packageData) {
        return updatePackage(packageData.getPackageId(), packageData);
    }
    
    @Override
    public boolean deletePackage(Integer packageId) {
        return softDeletePackage(packageId);
    }
    
    @Override
    public Page<MembershipPackage> findPackagesByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return packageRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }
    
    @Override
    public Page<MembershipPackage> findPackagesByDuration(Integer duration, Pageable pageable) {
        return packageRepository.findByDuration(duration, pageable);
    }
    
    @Override
    public Page<MembershipPackage> searchPackages(String keyword, Pageable pageable) {
        return packageRepository.findAllActive(pageable);
    }
    
    @Override
    public Double calculateTotalRevenue() {
        return packageRepository.calculateTotalRevenue();
    }
    
    @Override
    public Double calculateMonthlyRevenue() {
        return packageRepository.calculateMonthlyRevenue();
    }
    
    @Override
    public long getMostPopularPackageCount() {
        List<MembershipPackage> popularPackages = packageRepository.findMostExpensivePackages();
        return popularPackages.isEmpty() ? 0 : popularPackages.size();
    }
    
    @Override
    public List<java.util.Map<String, Object>> getPackageDistributionData() {
        List<java.util.Map<String, Object>> distributionData = new java.util.ArrayList<>();
        
        List<MembershipPackage> packages = getAllActivePackages();
        
        for (MembershipPackage pkg : packages) {
            java.util.Map<String, Object> packageData = new java.util.HashMap<>();
            packageData.put("name", pkg.getName());
            
            long momoCount = packageRepository.countMoMoPaymentsByPackageId(pkg.getPackageId());
            
            long cashCount = packageRepository.countCashPaymentsByPackageId(pkg.getPackageId());
            
            long totalCount = momoCount + cashCount;
            packageData.put("count", totalCount);
            
            if (totalCount > 0) {
                distributionData.add(packageData);
            }
        }
        
        return distributionData;
    }
    
    @Override
    public List<java.util.Map<String, Object>> getRevenueMonthlyData() {
        List<java.util.Map<String, Object>> revenueData = new java.util.ArrayList<>();
        
        //  6 tháng gần nhất
        for (int i = 5; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusMonths(i);
            String monthName = date.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.forLanguageTag("vi"));
            
            java.util.Map<String, Object> monthData = new java.util.HashMap<>();
            monthData.put("month", monthName);
            
            double momoRevenue = packageRepository.calculateMoMoRevenueByMonth(date.getYear(), date.getMonthValue());
            
            double cashRevenue = packageRepository.calculateCashRevenueByMonth(date.getYear(), date.getMonthValue());
            
            double totalRevenue = momoRevenue + cashRevenue;
            monthData.put("amount", totalRevenue);
            
            revenueData.add(monthData);
        }
        
        return revenueData;
    }
    
    @Override
    public List<java.util.Map<String, Object>> getPaymentMethodData() {
        List<java.util.Map<String, Object>> paymentData = new java.util.ArrayList<>();
        
        java.util.Map<String, Object> momoData = new java.util.HashMap<>();
        momoData.put("method", "MoMo");
        long momoCount = packageRepository.countTotalMoMoPayments();
        momoData.put("count", momoCount);
        paymentData.add(momoData);
        
        java.util.Map<String, Object> cashData = new java.util.HashMap<>();
        cashData.put("method", "Tiền mặt");
        long cashCount = packageRepository.countTotalCashPayments();
        cashData.put("count", cashCount);
        paymentData.add(cashData);
        
        java.util.Map<String, Object> transferData = new java.util.HashMap<>();
        transferData.put("method", "Chuyển khoản");
        long transferCount = packageRepository.countTotalTransferPayments();
        transferData.put("count", transferCount);
        paymentData.add(transferData);
        
        return paymentData;
    }
}
