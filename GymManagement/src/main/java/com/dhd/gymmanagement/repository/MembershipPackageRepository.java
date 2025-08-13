package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.MembershipPackage;
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
    

    @Query("SELECT p FROM MembershipPackage p WHERE p.name = :name AND p.isDeleted = 0")
    Optional<MembershipPackage> findByNameAndNotDeleted(@Param("name") String name);
    

    @Query("SELECT p FROM MembershipPackage p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isDeleted = 0")
    List<MembershipPackage> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    

    @Query("SELECT p FROM MembershipPackage p WHERE p.durationMonths = :duration AND p.isDeleted = 0")
    List<MembershipPackage> findByDuration(@Param("duration") Integer duration);
    

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
}
