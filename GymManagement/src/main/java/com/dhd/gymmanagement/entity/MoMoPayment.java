package com.dhd.gymmanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "momo_payments")
public class MoMoPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "package_id", nullable = false)
    private Integer packageId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "momo_transaction_id")
    private String momoTransactionId;

    @Column(name = "momo_result_code")
    private String momoResultCode;

    @Column(name = "momo_message")
    private String momoMessage;

    @Column(name = "momo_request_id")
    private String momoRequestId;

    @Column(name = "momo_response_time")
    private String momoResponseTime;

    @Column(name = "created_at", columnDefinition = "datetime(6) DEFAULT CURRENT_TIMESTAMP(6)")
    private Timestamp createdAt;

    @Column(name = "updated_at", columnDefinition = "datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
    private Timestamp updatedAt;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Integer isDeleted = 0;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, EXPIRED
    }

    public MoMoPayment() {}

    public MoMoPayment(String orderId, Double amount, Integer packageId, Integer userId) {
        this.orderId = orderId;
        this.amount = amount;
        this.packageId = packageId;
        this.userId = userId;
        this.status = PaymentStatus.PENDING;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getMomoTransactionId() { return momoTransactionId; }
    public void setMomoTransactionId(String momoTransactionId) { this.momoTransactionId = momoTransactionId; }

    public String getMomoResultCode() { return momoResultCode; }
    public void setMomoResultCode(String momoResultCode) { this.momoResultCode = momoResultCode; }

    public String getMomoMessage() { return momoMessage; }
    public void setMomoMessage(String momoMessage) { this.momoMessage = momoMessage; }

    public String getMomoRequestId() { return momoRequestId; }
    public void setMomoRequestId(String momoRequestId) { this.momoRequestId = momoRequestId; }

    public String getMomoResponseTime() { return momoResponseTime; }
    public void setMomoResponseTime(String momoResponseTime) { this.momoResponseTime = momoResponseTime; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "MoMoPayment{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", packageId=" + packageId +
                ", userId=" + userId +
                ", status=" + status +
                ", momoTransactionId='" + momoTransactionId + '\'' +
                ", momoResultCode='" + momoResultCode + '\'' +
                ", momoMessage='" + momoMessage + '\'' +
                ", momoRequestId='" + momoRequestId + '\'' +
                ", momoResponseTime='" + momoResponseTime + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
