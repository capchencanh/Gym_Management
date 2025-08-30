package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.MembershipPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface MembershipPackageService {
    

    List<MembershipPackage> getAllActivePackages();
    
    Page<MembershipPackage> getAllPackages(Pageable pageable);
    

    Optional<MembershipPackage> getPackageById(Integer packageId);
    

    Optional<MembershipPackage> getPackageByName(String name);
    

    MembershipPackage createPackage(MembershipPackage packageData);

    MembershipPackage updatePackage(Integer packageId, MembershipPackage packageData);
    
    MembershipPackage updatePackage(MembershipPackage packageData);
    

    boolean softDeletePackage(Integer packageId);
    
    boolean deletePackage(Integer packageId);
    

    boolean hardDeletePackage(Integer packageId);
    

    List<MembershipPackage> findPackagesByPriceRange(Double minPrice, Double maxPrice);
    
    Page<MembershipPackage> findPackagesByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);
    

    List<MembershipPackage> findPackagesByDuration(Integer duration);
    
    Page<MembershipPackage> findPackagesByDuration(Integer duration, Pageable pageable);
    
    Page<MembershipPackage> searchPackages(String keyword, Pageable pageable);
    

    boolean isPackageNameExists(String name);
    

    long countActivePackages();
    

    Double calculateTotalRevenue();
    

    Double calculateMonthlyRevenue();
    

    long getMostPopularPackageCount();
}
