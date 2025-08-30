package com.dhd.gymmanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequestDTO {
    @JsonProperty("packageId")
    private Integer packageId;
    
    @JsonProperty("paymentMethod")
    private String paymentMethod;
    
    @JsonProperty("returnUrl")
    private String returnUrl;
    
    @JsonProperty("userId")
    private Integer userId;

    public PaymentRequestDTO() {}

    public PaymentRequestDTO(Integer packageId, String paymentMethod, String returnUrl, Integer userId) {
        this.packageId = packageId;
        this.paymentMethod = paymentMethod;
        this.returnUrl = returnUrl;
        this.userId = userId;
    }

    public Integer getPackageId() { return packageId; }
    public void setPackageId(Integer packageId) { this.packageId = packageId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReturnUrl() { return returnUrl; }
    public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "PaymentRequestDTO{" +
                "packageId=" + packageId +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                ", userId=" + userId +
                '}';
    }
}
