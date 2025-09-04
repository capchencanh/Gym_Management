package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    List<Payment> findByIsDeleted(Integer isDeleted);
    
    Optional<Payment> findByPaymentIdAndIsDeleted(Integer paymentId, Integer isDeleted);
    
    List<Payment> findByMembershipIdAndIsDeleted(Integer membershipId, Integer isDeleted);
    @Query("SELECT p FROM Payment p JOIN UserMembership um ON p.membershipId = um.membershipId WHERE um.packageId = :packageId AND p.isDeleted = :isDeleted")
    List<Payment> findByPackageIdAndIsDeleted(@Param("packageId") Integer packageId, @Param("isDeleted") Integer isDeleted);
}
