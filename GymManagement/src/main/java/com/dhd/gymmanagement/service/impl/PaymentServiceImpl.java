package com.dhd.gymmanagement.service.impl;

import com.dhd.gymmanagement.entity.Payment;
import com.dhd.gymmanagement.repository.PaymentRepository;
import com.dhd.gymmanagement.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findByIsDeleted(0);
    }

    @Override
    public Optional<Payment> getPaymentById(Integer paymentId) {
        return paymentRepository.findByPaymentIdAndIsDeleted(paymentId, 0);
    }

    @Override
    public Payment createPayment(Payment payment) {
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        payment.setIsDeleted(0);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public boolean deletePayment(Integer paymentId) {
        Optional<Payment> paymentOpt = getPaymentById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setIsDeleted(1);
            paymentRepository.save(payment);
            return true;
        }
        return false;
    }

    @Override
    public List<Payment> getPaymentsByMembershipId(Integer membershipId) {
        return paymentRepository.findByMembershipIdAndIsDeleted(membershipId, 0);
    }
}
