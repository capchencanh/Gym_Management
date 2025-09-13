import React, { useState, useEffect } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import './PaymentStatus.css';

const PaymentStatus = ({ orderId, onClose }) => {
    const [paymentInfo, setPaymentInfo] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (orderId) {
            checkPaymentStatus();
        }
    }, [orderId]);

    const checkPaymentStatus = async () => {
        try {
            setLoading(true);
            const response = await Apis.get(`${endpoints['check-payment-status']}?orderId=${orderId}`);
            setPaymentInfo(response.data);
        } catch (err) {
            setError('Không thể kiểm tra trạng thái thanh toán');
        } finally {
            setLoading(false);
        }
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'COMPLETED':
                return '✅';
            case 'FAILED':
                return '❌';
            case 'PENDING':
                return '⏳';
            case 'EXPIRED':
                return '⏰';
            default:
                return '❓';
        }
    };

    const getStatusText = (status) => {
        switch (status) {
            case 'COMPLETED':
                return 'Thanh toán thành công';
            case 'FAILED':
                return 'Thanh toán thất bại';
            case 'PENDING':
                return 'Đang chờ thanh toán';
            case 'EXPIRED':
                return 'Đơn hàng đã hết hạn';
            default:
                return 'Trạng thái không xác định';
        }
    };

    const getStatusClass = (status) => {
        switch (status) {
            case 'COMPLETED':
                return 'success';
            case 'FAILED':
                return 'danger';
            case 'PENDING':
                return 'warning';
            case 'EXPIRED':
                return 'secondary';
            default:
                return 'info';
        }
    };

    if (loading) {
        return (
            <div className="payment-status-modal">
                <div className="payment-status-content">
                    <div className="text-center">
                        <div className="spinner-border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                        <p className="mt-2">Đang kiểm tra trạng thái thanh toán...</p>
                    </div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="payment-status-modal">
                <div className="payment-status-content">
                    <div className="alert alert-danger">
                        <h5>❌ Lỗi</h5>
                        <p>{error}</p>
                        <button className="btn btn-primary" onClick={checkPaymentStatus}>
                            Thử lại
                        </button>
                        <button className="btn btn-secondary ms-2" onClick={onClose}>
                            Đóng
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="payment-status-modal">
            <div className="payment-status-content">
                <div className="payment-status-header">
                    <h4>Thông tin thanh toán</h4>
                    <button className="btn-close" onClick={onClose}></button>
                </div>
                
                {paymentInfo && (
                    <div className="payment-status-body">
                        <div className={`status-badge status-${getStatusClass(paymentInfo.status)}`}>
                            <span className="status-icon">{getStatusIcon(paymentInfo.status)}</span>
                            <span className="status-text">{getStatusText(paymentInfo.status)}</span>
                        </div>
                        
                        <div className="payment-details">
                            <div className="detail-row">
                                <span className="detail-label">Mã đơn hàng:</span>
                                <span className="detail-value">{paymentInfo.orderId}</span>
                            </div>
                            
                            {paymentInfo.amount && (
                                <div className="detail-row">
                                    <span className="detail-label">Số tiền:</span>
                                    <span className="detail-value">
                                        {new Intl.NumberFormat('vi-VN', {
                                            style: 'currency',
                                            currency: 'VND'
                                        }).format(paymentInfo.amount)}
                                    </span>
                                </div>
                            )}
                            
                            {paymentInfo.transactionId && (
                                <div className="detail-row">
                                    <span className="detail-label">Mã giao dịch MoMo:</span>
                                    <span className="detail-value">{paymentInfo.transactionId}</span>
                                </div>
                            )}
                            
                            {paymentInfo.responseCode && (
                                <div className="detail-row">
                                    <span className="detail-label">Mã phản hồi:</span>
                                    <span className="detail-value">{paymentInfo.responseCode}</span>
                                </div>
                            )}
                            
                            {paymentInfo.message && (
                                <div className="detail-row">
                                    <span className="detail-label">Thông báo:</span>
                                    <span className="detail-value">{paymentInfo.message}</span>
                                </div>
                            )}
                        </div>
                        
                        <div className="payment-actions">
                            <button className="btn btn-primary" onClick={checkPaymentStatus}>
                                🔄 Cập nhật trạng thái
                            </button>
                            <button className="btn btn-secondary ms-2" onClick={onClose}>
                                Đóng
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default PaymentStatus;
