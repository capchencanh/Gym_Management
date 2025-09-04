package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.MoMoPayment;
import java.util.List;
import java.util.Optional;

public interface MoMoPaymentService {
    
    MoMoPayment createPayment(MoMoPayment payment);
    
    Optional<MoMoPayment> getPaymentByOrderId(String orderId);
    
    List<MoMoPayment> getPaymentsByUserId(Integer userId);
    
    List<MoMoPayment> getPaymentsByStatus(MoMoPayment.PaymentStatus status);
    List<MoMoPayment> getPaymentsByPackageId(Integer packageId);
    long countCompletedByPackageId(Integer packageId);
    
    void updatePaymentStatus(String orderId, MoMoPayment.PaymentStatus status, 
                           String transactionId, String resultCode, String message);
    
    void updatePaymentStatus(String orderId, MoMoPayment.PaymentStatus status, 
                           String transactionId, String resultCode, String message,
                           String requestId, String responseTime);
}
