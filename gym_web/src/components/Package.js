import React, { useState, useEffect, useContext } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import MySpinner from './Layouts/MySpinner';
import { MyUserContext } from '../configs/Contexts';
import { useLocation } from 'react-router-dom';
import PaymentStatus from './PaymentStatus';
import './Package.css';

const Package = () => {
    const [packages, setPackages] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [processingPayment, setProcessingPayment] = useState(false);
    const [paymentStatus, setPaymentStatus] = useState(null);
    const [showPaymentStatus, setShowPaymentStatus] = useState(false);
    const [currentOrderId, setCurrentOrderId] = useState(null);
    const user = useContext(MyUserContext);
    const location = useLocation();

    useEffect(() => {
        loadPackages();
        
        const urlParams = new URLSearchParams(location.search);
        const status = urlParams.get('status');
        const orderId = urlParams.get('orderId');
        const message = urlParams.get('message');
        
        if (status) {
            setPaymentStatus({
                status: status,
                orderId: orderId,
                message: message
            });
            
            window.history.replaceState({}, document.title, window.location.pathname);
        } else {
            const pendingOrderId = localStorage.getItem('currentOrderId');
            if (pendingOrderId) {
    
                startPaymentPolling(pendingOrderId);
            }
        }
    }, [location]);

    const loadPackages = async () => {
        try {
            setLoading(true);
            const response = await Apis.get(endpoints['packages']);

            setPackages(response.data);
        } catch (error) {
            console.error('Lỗi khi tải danh sách gói tập:', error);
        } finally {
            setLoading(false);
        }
    };

    const formatPrice = (price) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    };

    const formatDuration = (months) => {
        if (!months || months === 0) return 'Không xác định';
        if (months === 1) return '1 tháng';
        return `${months} tháng`;
    };

    const filteredPackages = packages.filter(pkg => 
        pkg.name.toLowerCase().includes(searchKeyword.toLowerCase()) ||
        (pkg.description && pkg.description.toLowerCase().includes(searchKeyword.toLowerCase()))
    );

    

    const handlePayment = async (packageId) => {
        if (!user) {
            alert('Vui lòng đăng nhập để thanh toán!');
            return;
        }

        if (!packageId) {
            alert('Lỗi: Không tìm thấy ID gói tập!');
            return;
        }

        try {
            setProcessingPayment(true);

            
            const paymentData = {
                packageId: parseInt(packageId), 
                paymentMethod: 'CARD',
                returnUrl: window.location.origin + '/package',
                userId: user.id 
            };
            

            
            const response = await Apis.post(endpoints['create-payment'], paymentData);


            if (response.data.payUrl) {
                localStorage.setItem('currentOrderId', response.data.orderId);
                localStorage.setItem('currentPackageId', packageId);
                

                
                window.location.href = response.data.payUrl;
            } else {
                console.error('No payUrl in response:', response.data);
                alert('Có lỗi xảy ra khi tạo thanh toán MoMo!');
            }
        } catch (error) {
            console.error('Lỗi thanh toán:', error);
            alert('Có lỗi xảy ra khi thanh toán!');
        } finally {
            setProcessingPayment(false);
        }
    };

    const closePaymentStatus = () => {
        setPaymentStatus(null);
    };

    const showPaymentStatusModal = (orderId) => {
        setCurrentOrderId(orderId);
        setShowPaymentStatus(true);
    };

    const closePaymentStatusModal = () => {
        setShowPaymentStatus(false);
        setCurrentOrderId(null);
    };

    const startPaymentPolling = (orderId) => {
        const pollInterval = setInterval(async () => {
            try {
                const response = await Apis.get(`${endpoints['check-payment-status']}?orderId=${orderId}`);
                
                if (response.data.status === 'COMPLETED') {
                    clearInterval(pollInterval);
                    localStorage.removeItem('currentOrderId');
                    localStorage.removeItem('currentPackageId');
                    
                    window.location.href = '/package?status=success&orderId=' + orderId;
                } else if (response.data.status === 'FAILED' || response.data.status === 'EXPIRED') {
                    clearInterval(pollInterval);
                    localStorage.removeItem('currentOrderId');
                    localStorage.removeItem('currentPackageId');
                    
                    window.location.href = '/package?status=failed&orderId=' + orderId + '&message=' + response.data.message;
                }
            } catch (error) {
                console.error('Lỗi khi polling trạng thái thanh toán:', error);
            }
        }, 30000); 
        
        setTimeout(() => {
            clearInterval(pollInterval);
            localStorage.removeItem('currentOrderId');
            localStorage.removeItem('currentPackageId');
        }, 15 * 60 * 1000);
    };

    if (loading) {
        return <MySpinner />;
    }

    return (
        <div className="package-container">
            <div className="container">
                <h2 className="text-center mb-4">Gói Tập Luyện</h2>

                {paymentStatus && (
                    <div className={`alert alert-${paymentStatus.status === 'success' ? 'success' : paymentStatus.status === 'failed' ? 'danger' : 'warning'} alert-dismissible fade show`} role="alert">
                        <strong>
                            {paymentStatus.status === 'success' ? '✅ Thanh toán thành công!' : 
                             paymentStatus.status === 'failed' ? '❌ Thanh toán thất bại!' : 
                             '⚠️ Có lỗi xảy ra!'}
                        </strong>
                        <br />
                        {paymentStatus.message || 
                         (paymentStatus.status === 'success' ? 'Gói tập của bạn đã được kích hoạt thành công!' :
                          paymentStatus.status === 'failed' ? 'Thanh toán không thành công. Vui lòng thử lại.' :
                          'Đã xảy ra lỗi trong quá trình xử lý thanh toán.')}
                        {paymentStatus.orderId && (
                            <div className="mt-2">
                                <small className="text-muted">Mã giao dịch: {paymentStatus.orderId}</small>
                                <button 
                                    className="btn btn-sm btn-outline-primary ms-2"
                                    onClick={() => showPaymentStatusModal(paymentStatus.orderId)}
                                >
                                    Xem chi tiết
                                </button>
                            </div>
                        )}
                        <button type="button" className="btn-close" onClick={closePaymentStatus}></button>
                    </div>
                )}
                
                <div className="search-box">
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Tìm kiếm gói tập..."
                        value={searchKeyword}
                        onChange={(e) => setSearchKeyword(e.target.value)}
                    />
                </div>

                {filteredPackages.length === 0 ? (
                    <div className="text-center">
                        <p className="text-muted">Không tìm thấy gói tập nào.</p>
                    </div>
                ) : (
                    <div className="row">
                                                 {filteredPackages.map((pkg) => (
                             <div key={pkg.package_id} className="col-md-4 mb-3">
                                <div className="card package-card h-100">
                                    <div className="card-body">
                                        <h5 className="card-title package-title">{pkg.name}</h5>
                                        <div className="mb-2">
                                            <span className="package-duration me-2">
                                                {formatDuration(pkg.duration_months)}
                                            </span>
                                            <span className="package-price">
                                                {formatPrice(pkg.price)}
                                            </span>
                                        </div>
                                        <p className="card-text text-muted">
                                            {pkg.description || 'Không có mô tả'}
                                        </p>
                                                                                                                         <button 
                                            className="btn btn-primary w-100"
                                            onClick={() => {
                                                
                                                handlePayment(pkg.package_id);
                                            }}
                                            disabled={processingPayment}
                                        >
                                            {processingPayment ? 'Đang xử lý...' : 'Thanh Toán MoMo'}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
            
            {showPaymentStatus && (
                <PaymentStatus 
                    orderId={currentOrderId} 
                    onClose={closePaymentStatusModal} 
                />
            )}
        </div>
    );
};

export default Package;
