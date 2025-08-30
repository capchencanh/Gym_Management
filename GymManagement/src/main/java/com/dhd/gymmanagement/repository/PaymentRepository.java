package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    List<Payment> findByIsDeleted(Integer isDeleted);
    
    Optional<Payment> findByPaymentIdAndIsDeleted(Integer paymentId, Integer isDeleted);
    
    List<Payment> findByMembershipIdAndIsDeleted(Integer membershipId, Integer isDeleted);
}
