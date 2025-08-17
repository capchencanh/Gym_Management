import React, { useState, useEffect } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import './PTStatusDisplay.css';
import TrainingSchedule from './TrainingSchedule';

const PTStatusDisplay = ({ userId }) => {
    const [ptAssignment, setPtAssignment] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showSchedule, setShowSchedule] = useState(false);

    useEffect(() => {
        if (userId) {
            loadPTStatus();
        }
    }, [userId]);



                    const loadPTStatus = async () => {
                    try {
                        setLoading(true);
                        const response = await Apis.get(`${endpoints['pt-status']}/${userId}`);
                        
                        if (response.data && response.data.length > 0) {
                            const assignment = response.data[0];
                            setPtAssignment(assignment);
                        }
                    } catch (error) {
                        console.error('Lỗi khi load trạng thái PT:', error);
                        setError('Không thể tải thông tin PT. Vui lòng thử lại sau.');
                    } finally {
                        setLoading(false);
                    }
                };

    const refreshStatus = () => {
        loadPTStatus();
    };

    if (loading) {
        return (
            <div className="pt-status-loading">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Đang tải...</span>
                </div>
                <p>Đang tải thông tin PT...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="pt-status-error">
                <div className="error-card">
                    <i className="fas fa-exclamation-triangle text-warning"></i>
                    <h4>Lỗi khi tải thông tin</h4>
                    <p>{error}</p>
                    <button onClick={refreshStatus} className="btn btn-outline-primary">
                        <i className="fas fa-redo"></i> Thử lại
                    </button>
                </div>
            </div>
        );
    }

    if (!ptAssignment) {
        return (
            <div className="pt-status-empty">
                <div className="empty-card">
                    <i className="fas fa-user-plus text-muted"></i>
                    <h4>Chưa có yêu cầu PT</h4>
                    <p>Bạn chưa gửi yêu cầu thuê Personal Trainer nào.</p>
                    <p>Hãy sử dụng form yêu cầu PT để bắt đầu!</p>
                </div>
            </div>
        );
    }

    const getStatusInfo = (status) => {
        const statusConfig = {
            'PENDING': {
                label: 'Đang chờ phân công',
                color: 'warning',
                icon: 'clock',
                description: 'Yêu cầu của bạn đã được gửi và đang chờ admin phân công PT.'
            },
            'ASSIGNED': {
                label: 'Đã được phân công',
                color: 'info',
                icon: 'user-check',
                description: 'Admin đã phân công PT cho bạn. PT sẽ liên hệ sớm nhất.'
            },
            'ACTIVE': {
                label: 'Đang hoạt động',
                color: 'success',
                icon: 'play-circle',
                description: 'Bạn đang tập luyện với PT. Hãy tuân thủ lịch tập!'
            },
            'COMPLETED': {
                label: 'Hoàn thành',
                color: 'primary',
                icon: 'check-circle',
                description: 'Khóa tập luyện với PT đã hoàn thành. Chúc mừng bạn!'
            },
            'CANCELLED': {
                label: 'Hủy bỏ',
                color: 'danger',
                icon: 'times-circle',
                description: 'Yêu cầu PT đã bị hủy bỏ.'
            }
        };

        return statusConfig[status] || statusConfig['PENDING'];
    };

    const statusInfo = getStatusInfo(ptAssignment.status);

    return (
        <div className="pt-status-display">
            <div className="status-header">
                <h3>Trạng thái yêu cầu Personal Trainer</h3>
                <button onClick={refreshStatus} className="btn btn-outline-secondary btn-sm">
                    <i className="fas fa-sync-alt"></i> Làm mới
                </button>
            </div>

            <div className="status-main-card">
                <div className="status-badge-large">
                    <i className={`fas fa-${statusInfo.icon} text-${statusInfo.color}`}></i>
                    <span className={`badge bg-${statusInfo.color}`}>
                        {statusInfo.label}
                    </span>
                </div>

                <div className="status-description">
                    <p>{statusInfo.description}</p>
                </div>

                <div className="request-details">
                    <h4>Chi tiết yêu cầu</h4>
                    <div className="detail-grid">
                        <div className="detail-item">
                            <span className="detail-label">Ngày yêu cầu:</span>
                            <span className="detail-value">
                                {ptAssignment.createdAt ? 
                                    new Date(ptAssignment.createdAt).toLocaleDateString('vi-VN', {
                                        year: 'numeric',
                                        month: 'long',
                                        day: 'numeric',
                                        hour: '2-digit',
                                        minute: '2-digit'
                                    }) : 'Không có thông tin'
                                }
                            </span>
                        </div>

                        <div className="detail-item">
                            <span className="detail-label">Ghi chú:</span>
                            <span className="detail-value">{ptAssignment.requestNotes}</span>
                        </div>

                        {ptAssignment.updatedAt && (
                            <div className="detail-item">
                                <span className="detail-label">Cập nhật lần cuối:</span>
                                <span className="detail-value">
                                    {new Date(ptAssignment.updatedAt).toLocaleDateString('vi-VN', {
                                        year: 'numeric',
                                        month: 'long',
                                        day: 'numeric',
                                        hour: '2-digit',
                                        minute: '2-digit'
                                    })}
                                </span>
                            </div>
                        )}
                    </div>
                </div>

                                 {ptAssignment.trainerId && (
                     <div className="trainer-assignment">
                         <h4>PT được phân công</h4>
                         <div className="trainer-card">
                             <div className="trainer-avatar">
                                 <i className="fas fa-user-tie"></i>
                             </div>
                             <div className="trainer-info">
                                 <h5>{ptAssignment.trainerName}</h5>
                                 <p className="trainer-specialization">
                                     <i className="fas fa-star text-warning"></i>
                                     {ptAssignment.trainerSpecialization}
                                 </p>
                                 <p className="trainer-email">
                                     <i className="fas fa-envelope text-muted"></i>
                                     {ptAssignment.trainerEmail}
                                 </p>
                             </div>
                         </div>
                         
                         <div className="trainer-actions">
                             <button 
                                 className="btn btn-outline-primary btn-sm"
                                 onClick={() => setShowSchedule(true)}
                             >
                                 <i className="fas fa-calendar-alt"></i> Xem lịch tập
                             </button>
                             <button className="btn btn-outline-success btn-sm">
                                 <i className="fas fa-comments"></i> Liên hệ PT
                             </button>
                         </div>
                     </div>
                 )}

                {ptAssignment.status === 'PENDING' && (
                    <div className="pending-info">
                        <div className="info-alert">
                            <i className="fas fa-info-circle text-info"></i>
                            <div>
                                <strong>Yêu cầu của bạn đang được xử lý</strong>
                                <p>Admin sẽ xem xét và phân công PT phù hợp trong thời gian sớm nhất.</p>
                            </div>
                        </div>
                    </div>
                )}

                {ptAssignment.status === 'ASSIGNED' && (
                    <div className="assigned-info">
                        <div className="info-alert">
                            <i className="fas fa-check-circle text-success"></i>
                            <div>
                                <strong>PT đã được phân công!</strong>
                                <p>PT sẽ liên hệ với bạn để lên lịch tập cụ thể dựa trên thời gian rảnh bạn đã cung cấp.</p>
                            </div>
                        </div>
                    </div>
                )}

                {ptAssignment.status === 'ACTIVE' && (
                    <div className="active-info">
                        <div className="info-alert">
                            <i className="fas fa-play-circle text-success"></i>
                            <div>
                                <strong>Đang tập luyện với PT</strong>
                                <p>Hãy tuân thủ lịch tập và liên hệ với PT nếu có vấn đề gì.</p>
                            </div>
                        </div>
                    </div>
                                 )}
             </div>
             
             {showSchedule && (
                 <TrainingSchedule
                     userId={userId}
                     trainerId={ptAssignment.trainerId}
                     onClose={() => setShowSchedule(false)}
                 />
             )}
         </div>
     );
};

export default PTStatusDisplay;
