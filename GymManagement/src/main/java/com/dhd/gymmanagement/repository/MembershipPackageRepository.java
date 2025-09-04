package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.MembershipPackage;
import com.dhd.gymmanagement.entity.MoMoPayment;
import com.dhd.gymmanagement.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPackageRepository extends JpaRepository<MembershipPackage, Integer> {
    

    @Query("SELECT p FROM MembershipPackage p WHERE p.isDeleted = 0")
    List<MembershipPackage> findAllActive();
    @Query("SELECT p FROM MembershipPackage p WHERE p.isDeleted = 0")
    Page<MembershipPackage> findAllActive(Pageable pageable);
    

    @Query("SELECT p FROM MembershipPackage p WHERE p.name = :name AND p.isDeleted = 0")
    Optional<MembershipPackage> findByNameAndNotDeleted(@Param("name") String name);
    

    @Query("SELECT p FROM MembershipPackage p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isDeleted = 0")
    List<MembershipPackage> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    @Query("SELECT p FROM MembershipPackage p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isDeleted = 0")
    Page<MembershipPackage> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);
    

    @Query("SELECT p FROM MembershipPackage p WHERE p.durationMonths = :duration AND p.isDeleted = 0")
    List<MembershipPackage> findByDuration(@Param("duration") Integer duration);
    @Query("SELECT p FROM MembershipPackage p WHERE p.durationMonths = :duration AND p.isDeleted = 0")
    Page<MembershipPackage> findByDuration(@Param("duration") Integer duration, Pageable pageable);
    

    @Query("SELECT COUNT(p) > 0 FROM MembershipPackage p WHERE p.name = :name AND p.isDeleted = 0")
    boolean existsByNameAndNotDeleted(@Param("name") String name);
    

    @Query("SELECT COUNT(p) FROM MembershipPackage p WHERE p.isDeleted = 0")
    long countActivePackages();
    

    @Query("SELECT COALESCE(SUM(p.price), 0) FROM MembershipPackage p WHERE p.isDeleted = 0")
    Double calculateTotalRevenue();
    

    @Query("SELECT COALESCE(SUM(p.price), 0) FROM MembershipPackage p WHERE p.isDeleted = 0 AND MONTH(p.createdAt) = MONTH(CURRENT_DATE()) AND YEAR(p.createdAt) = YEAR(CURRENT_DATE())")
    Double calculateMonthlyRevenue();
    

    @Query("SELECT p FROM MembershipPackage p WHERE p.isDeleted = 0 ORDER BY p.price DESC")
    List<MembershipPackage> findMostExpensivePackages();
    
    @Query("SELECT COUNT(mp) FROM MoMoPayment mp WHERE mp.packageId = :packageId AND mp.status = 'COMPLETED' AND mp.isDeleted = 0")
    long countMoMoPaymentsByPackageId(@Param("packageId") Integer packageId);
    
    @Query("SELECT COUNT(p) FROM Payment p JOIN UserMembership um ON p.membershipId = um.membershipId WHERE um.packageId = :packageId AND p.paymentMethod = 'CASH' AND p.paymentStatus = 'COMPLETED' AND p.isDeleted = 0")
    long countCashPaymentsByPackageId(@Param("packageId") Integer packageId);
    
    @Query("SELECT COALESCE(SUM(mp.amount), 0) FROM MoMoPayment mp WHERE mp.status = 'COMPLETED' AND mp.isDeleted = 0 AND YEAR(mp.createdAt) = :year AND MONTH(mp.createdAt) = :month")
    Double calculateMoMoRevenueByMonth(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p JOIN UserMembership um ON p.membershipId = um.membershipId WHERE p.paymentMethod = 'CASH' AND p.paymentStatus = 'COMPLETED' AND p.isDeleted = 0 AND YEAR(p.paymentDate) = :year AND MONTH(p.paymentDate) = :month")
    Double calculateCashRevenueByMonth(@Param("year") int year, @Param("month") int month);
    
    @Query("SELECT COUNT(mp) FROM MoMoPayment mp WHERE mp.status = 'COMPLETED' AND mp.isDeleted = 0")
    long countTotalMoMoPayments();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = 'CASH' AND p.paymentStatus = 'COMPLETED' AND p.isDeleted = 0")
    long countTotalCashPayments();
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentMethod = 'TRANSFER' AND p.paymentStatus = 'COMPLETED' AND p.isDeleted = 0")
    long countTotalTransferPayments();
}
