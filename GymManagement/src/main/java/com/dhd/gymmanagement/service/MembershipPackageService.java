package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.MembershipPackage;
import java.util.List;
import java.util.Optional;

public interface MembershipPackageService {
    

    List<MembershipPackage> getAllActivePackages();
    

    Optional<MembershipPackage> getPackageById(Integer packageId);
    

    Optional<MembershipPackage> getPackageByName(String name);
    

    MembershipPackage createPackage(MembershipPackage packageData);

    MembershipPackage updatePackage(Integer packageId, MembershipPackage packageData);
    

    boolean softDeletePackage(Integer packageId);
    

    boolean hardDeletePackage(Integer packageId);
    

    List<MembershipPackage> findPackagesByPriceRange(Double minPrice, Double maxPrice);
    

    List<MembershipPackage> findPackagesByDuration(Integer duration);
    

    boolean isPackageNameExists(String name);
    

    long countActivePackages();
    

    long calculateTotalRevenue();
    

    long calculateMonthlyRevenue();
    

    long getMostPopularPackageCount();
}
