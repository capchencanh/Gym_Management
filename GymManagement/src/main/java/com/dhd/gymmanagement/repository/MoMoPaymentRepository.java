package com.dhd.gymmanagement.repository;

import com.dhd.gymmanagement.entity.MoMoPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoMoPaymentRepository extends JpaRepository<MoMoPayment, Integer> {
    
    Optional<MoMoPayment> findByOrderId(String orderId);
    
    List<MoMoPayment> findByUserId(Integer userId);
    
    List<MoMoPayment> findByStatus(MoMoPayment.PaymentStatus status);
    
    List<MoMoPayment> findByUserIdAndStatus(Integer userId, MoMoPayment.PaymentStatus status);
}
