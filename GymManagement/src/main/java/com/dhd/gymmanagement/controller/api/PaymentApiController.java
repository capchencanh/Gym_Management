package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.dto.PaymentRequestDTO;
import com.dhd.gymmanagement.entity.MembershipPackage;
import com.dhd.gymmanagement.entity.Payment;
import com.dhd.gymmanagement.entity.UserMembership;
import com.dhd.gymmanagement.service.MembershipPackageService;
import com.dhd.gymmanagement.service.PaymentService;
import com.dhd.gymmanagement.service.UserMembershipService;
import com.dhd.gymmanagement.service.MoMoService;
import com.dhd.gymmanagement.service.MoMoPaymentService;
import com.dhd.gymmanagement.entity.MoMoPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentApiController {

    @Autowired
    private MembershipPackageService membershipPackageService;

    @Autowired
    private UserMembershipService userMembershipService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MoMoService moMoService;

    @Autowired
    private MoMoPaymentService moMoPaymentService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody PaymentRequestDTO request) {
        try {
            if (request.getPackageId() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Package ID không được để trống"));
            }
            
            Optional<MembershipPackage> packageOpt = membershipPackageService.getPackageById(request.getPackageId());
            if (!packageOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Gói tập không tồn tại"));
            }
            
            MembershipPackage membershipPackage = packageOpt.get();
            
            if (membershipPackage.getIsDeleted() == 1) {
                return ResponseEntity.badRequest().body(Map.of("error", "Gói tập đã bị xóa"));
            }
            
            String orderId = "MOMO_" + System.currentTimeMillis();
            
            String orderInfo = "Thanh toan goi tap: " + membershipPackage.getName();
            
            MoMoPayment moMoPayment = new MoMoPayment(orderId, membershipPackage.getPrice(), membershipPackage.getPackageId(), request.getUserId());
            moMoPaymentService.createPayment(moMoPayment);
            
            String requestId = String.valueOf(System.currentTimeMillis());
            
            String rawSignature = "accessKey=F8BBA842ECF85" +
                    "&amount=" + membershipPackage.getPrice().longValue() +
                    "&extraData=" +
                    "&ipnUrl=http://localhost:8080/api/payment/momo-ipn" +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=MOMO" +
                    "&redirectUrl=http://localhost:8080/api/payment/momo-callback" +
                    "&requestId=" + requestId +
                    "&requestType=captureWallet";
            

            
            String signature = moMoService.hmacSHA256("K951B6PE1waDMi640xX08PD3vg6EkVlz", rawSignature);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", "MOMO");
            requestBody.put("partnerName", "Test");
            requestBody.put("storeId", "MomoTestStore");
            requestBody.put("requestId", requestId);
            requestBody.put("amount", membershipPackage.getPrice().longValue());
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", "http://localhost:8080/api/payment/momo-callback");
            requestBody.put("ipnUrl", "http://localhost:8080/api/payment/momo-ipn");
            requestBody.put("lang", "vi");
            requestBody.put("extraData", "");
            requestBody.put("requestType", "captureWallet");
            requestBody.put("signature", signature);
            
            String momoPayUrl = null;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                
                ResponseEntity<Map<String, Object>> momoResponse = restTemplate.exchange(
                    "https://test-payment.momo.vn/v2/gateway/api/create",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
                );
                
                if (momoResponse.getStatusCode().is2xxSuccessful() && momoResponse.getBody() != null) {
                    Map<String, Object> momoData = momoResponse.getBody();
                    
                    if (momoData.containsKey("payUrl")) {
                        momoPayUrl = (String) momoData.get("payUrl");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tạo thanh toán MoMo thành công");
            response.put("orderId", orderId);
            response.put("amount", membershipPackage.getPrice());
            response.put("packageName", membershipPackage.getName());
            response.put("packageId", request.getPackageId());
            response.put("payUrl", momoPayUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi tạo thanh toán: " + e.getMessage()));
        }
    }

    @GetMapping("/momo-callback")
    public ResponseEntity<String> momoCallback(@RequestParam Map<String, String> queryParams) {
        try {
            boolean isValidSignature = moMoService.verifyPaymentResponse(queryParams);
            if (!isValidSignature) {
            }
            
            String orderId = queryParams.get("orderId");
            String resultCode = queryParams.get("resultCode");
            String transactionId = queryParams.get("transId");
            /* removed unused: amount */
            String message = queryParams.get("message");
            String requestId = queryParams.get("requestId");
            String responseTime = queryParams.get("responseTime");
            

            
            MoMoPayment.PaymentStatus status;
            String redirectUrl;
            
            if ("0".equals(resultCode)) {
                status = MoMoPayment.PaymentStatus.COMPLETED;
                redirectUrl = "http://localhost:3000/package?status=success&orderId=" + orderId;
                
                Optional<MoMoPayment> paymentOpt = moMoPaymentService.getPaymentByOrderId(orderId);
                if (paymentOpt.isPresent()) {
                    MoMoPayment payment = paymentOpt.get();
                    
                    Optional<MembershipPackage> packageOpt = membershipPackageService.getPackageById(payment.getPackageId());
                    if (packageOpt.isPresent()) {
                        MembershipPackage membershipPackage = packageOpt.get();
                        
                        UserMembership userMembership = new UserMembership();
                        userMembership.setUserId(payment.getUserId());
                        userMembership.setPackageId(payment.getPackageId());
                        
                        Timestamp startDate = new Timestamp(System.currentTimeMillis());
                        userMembership.setStartDate(startDate);
                        
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(startDate);
                        calendar.add(Calendar.MONTH, membershipPackage.getDurationMonths());
                        Timestamp endDate = new Timestamp(calendar.getTimeInMillis());
                        userMembership.setEndDate(endDate);
                        
                        userMembership.setStatus(UserMembership.MembershipStatus.ACTIVE);
                        userMembership.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                        userMembership.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                        
                        UserMembership savedMembership = userMembershipService.createUserMembership(userMembership);
                        
                        Payment paymentRecord = new Payment();
                        paymentRecord.setMembershipId(savedMembership.getMembershipId());
                        paymentRecord.setAmount(payment.getAmount());
                        paymentRecord.setPaymentMethod(Payment.PaymentMethod.CARD);
                        paymentRecord.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
                        paymentRecord.setPaymentDate(new Timestamp(System.currentTimeMillis()));
                        paymentRecord.setNotes("Thanh toán MoMo - Order ID: " + orderId + ", Transaction ID: " + transactionId);
                        paymentRecord.setIsDeleted(0);
                        
                                        paymentService.createPayment(paymentRecord);
                    }
                }
            } else {
                status = MoMoPayment.PaymentStatus.FAILED;
                redirectUrl = "http://localhost:3000/package?status=failed&orderId=" + orderId + "&message=" + message;
            }
            
            moMoPaymentService.updatePaymentStatus(orderId, status, transactionId, resultCode, message, requestId, responseTime);
            
            String htmlResponse = "<html><head><title>MoMo Callback</title></head>" +
                    "<body><script>window.location.href='" + redirectUrl + "';</script>" +
                    "<p>Đang chuyển hướng...</p></body></html>";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlResponse);
                    
        } catch (Exception e) {
            e.printStackTrace();
            String errorUrl = "http://localhost:3000/package?status=error&message=Lỗi xử lý callback MoMo";
            String htmlResponse = "<html><head><title>Lỗi</title></head>" +
                    "<body><script>window.location.href='" + errorUrl + "';</script>" +
                    "<p>Đang chuyển hướng...</p></body></html>";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlResponse);
        }
    }

    @GetMapping("/test-momo-callback")
    public ResponseEntity<String> testMomoCallback(@RequestParam String orderId) {
        try {
            
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("orderId", orderId);
            queryParams.put("resultCode", "0");
            queryParams.put("transId", "TEST_" + System.currentTimeMillis());
            queryParams.put("amount", "10000");
            queryParams.put("message", "Giao dich thanh cong");
            queryParams.put("requestId", "REQ_" + System.currentTimeMillis());
            queryParams.put("responseTime", String.valueOf(System.currentTimeMillis()));
            
            return momoCallback(queryParams);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test-payment-success-direct")
    public ResponseEntity<String> testPaymentSuccessDirect(@RequestParam String orderId) {
        try {
            
            // Tìm payment record
            Optional<MoMoPayment> paymentOpt = moMoPaymentService.getPaymentByOrderId(orderId);
            if (!paymentOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Order not found: " + orderId);
            }
            
            MoMoPayment payment = paymentOpt.get();
            
            moMoPaymentService.updatePaymentStatus(orderId, MoMoPayment.PaymentStatus.COMPLETED,
                "DIRECT_TEST_" + System.currentTimeMillis(), "0", "Test thanh toán thành công",
                "REQ_" + System.currentTimeMillis(), String.valueOf(System.currentTimeMillis()));
            
            Optional<MembershipPackage> packageOpt = membershipPackageService.getPackageById(payment.getPackageId());
            if (packageOpt.isPresent()) {
                MembershipPackage membershipPackage = packageOpt.get();
                
                UserMembership userMembership = new UserMembership();
                userMembership.setUserId(payment.getUserId());
                userMembership.setPackageId(payment.getPackageId());
                
                Timestamp startDate = new Timestamp(System.currentTimeMillis());
                userMembership.setStartDate(startDate);
                
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.MONTH, membershipPackage.getDurationMonths());
                Timestamp endDate = new Timestamp(calendar.getTimeInMillis());
                userMembership.setEndDate(endDate);
                
                userMembership.setStatus(UserMembership.MembershipStatus.ACTIVE);
                userMembership.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                userMembership.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                
                UserMembership savedMembership = userMembershipService.createUserMembership(userMembership);
                
                Payment paymentRecord = new Payment();
                paymentRecord.setMembershipId(savedMembership.getMembershipId());
                paymentRecord.setAmount(payment.getAmount());
                paymentRecord.setPaymentMethod(Payment.PaymentMethod.CARD);
                paymentRecord.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
                paymentRecord.setPaymentDate(new Timestamp(System.currentTimeMillis()));
                paymentRecord.setNotes("Test thanh toán trực tiếp - Order ID: " + orderId);
                paymentRecord.setIsDeleted(0);
                
                                        paymentService.createPayment(paymentRecord);
            }
            
            String successUrl = "http://localhost:3000/package?status=success&orderId=" + orderId;
            String htmlResponse = "<html><head><title>Test Payment Success</title></head>" +
                    "<body><script>window.location.href='" + successUrl + "';</script>" +
                    "<p>Đang chuyển hướng về trang thành công...</p></body></html>";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlResponse);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/check-status")
    public ResponseEntity<Map<String, Object>> checkOrderStatus(@RequestParam String orderId) {
        try {
            
            Optional<MoMoPayment> paymentOpt = moMoPaymentService.getPaymentByOrderId(orderId);
            if (!paymentOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "NOT_FOUND");
                response.put("message", "Không tìm thấy đơn hàng");
                response.put("orderId", orderId);
                return ResponseEntity.ok(response);
            }
            
            MoMoPayment payment = paymentOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("status", payment.getStatus().toString());
            response.put("message", payment.getMomoMessage() != null ? payment.getMomoMessage() : "Trạng thái: " + payment.getStatus());
            response.put("orderId", orderId);
            response.put("amount", payment.getAmount());
            response.put("transactionId", payment.getMomoTransactionId());
            response.put("resultCode", payment.getMomoResultCode());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "ERROR");
            response.put("message", "Lỗi: " + e.getMessage());
            response.put("orderId", orderId);
            return ResponseEntity.ok(response);
        }
    }
}
