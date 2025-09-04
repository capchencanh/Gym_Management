package com.dhd.gymmanagement.service.impl;

import com.dhd.gymmanagement.entity.MoMoPayment;
import com.dhd.gymmanagement.repository.MoMoPaymentRepository;
import com.dhd.gymmanagement.service.MoMoPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class MoMoPaymentServiceImpl implements MoMoPaymentService {

    @Autowired
    private MoMoPaymentRepository moMoPaymentRepository;

    @Override
    public MoMoPayment createPayment(MoMoPayment payment) {
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
        if (payment.getUpdatedAt() == null) {
            payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        }
        return moMoPaymentRepository.save(payment);
    }

    @Override
    public Optional<MoMoPayment> getPaymentByOrderId(String orderId) {
        return moMoPaymentRepository.findByOrderId(orderId);
    }

    @Override
    public List<MoMoPayment> getPaymentsByUserId(Integer userId) {
        return moMoPaymentRepository.findByUserId(userId);
    }

    @Override
    public List<MoMoPayment> getPaymentsByStatus(MoMoPayment.PaymentStatus status) {
        return moMoPaymentRepository.findByStatus(status);
    }

    @Override
    public List<MoMoPayment> getPaymentsByPackageId(Integer packageId) {
        return moMoPaymentRepository.findByPackageId(packageId);
    }

    @Override
    public long countCompletedByPackageId(Integer packageId) {
        List<MoMoPayment> list = moMoPaymentRepository.findByPackageId(packageId);
        return list.stream().filter(p -> p.getStatus() == MoMoPayment.PaymentStatus.COMPLETED).count();
    }

    @Override
    public void updatePaymentStatus(String orderId, MoMoPayment.PaymentStatus status, 
                                  String transactionId, String resultCode, String message) {
        Optional<MoMoPayment> paymentOpt = moMoPaymentRepository.findByOrderId(orderId);
        if (paymentOpt.isPresent()) {
            MoMoPayment payment = paymentOpt.get();
            payment.setStatus(status);
            payment.setMomoTransactionId(transactionId);
            payment.setMomoResultCode(resultCode);
            payment.setMomoMessage(message);
            payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            moMoPaymentRepository.save(payment);
        }
    }

    @Override
    public void updatePaymentStatus(String orderId, MoMoPayment.PaymentStatus status, 
                                  String transactionId, String resultCode, String message,
                                  String requestId, String responseTime) {
        Optional<MoMoPayment> paymentOpt = moMoPaymentRepository.findByOrderId(orderId);
        if (paymentOpt.isPresent()) {
            MoMoPayment payment = paymentOpt.get();
            payment.setStatus(status);
            payment.setMomoTransactionId(transactionId);
            payment.setMomoResultCode(resultCode);
            payment.setMomoMessage(message);
            payment.setMomoRequestId(requestId);
            payment.setMomoResponseTime(responseTime);
            payment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            moMoPaymentRepository.save(payment);
        }
    }
}
