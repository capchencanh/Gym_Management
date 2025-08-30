package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Integer paymentId);
    Payment createPayment(Payment payment);
    Payment updatePayment(Payment payment);
    boolean deletePayment(Integer paymentId);
    List<Payment> getPaymentsByMembershipId(Integer membershipId);
}
