import React, { useState } from 'react';
import './PTStatusDisplay.css';
import TrainingSchedule from './TrainingSchedule';

const PTStatusDisplay = ({ ptAssignment, onRefresh }) => {
    const [showSchedule, setShowSchedule] = useState(false);

    if (!ptAssignment) {
        return (
            <div className="pt-status-empty">
                <div className="empty-card">
                    <i className="fas fa-user-plus text-muted"></i>
                    <h4>Chưa có yêu cầu PT</h4>
                    <p>Bạn chưa gửi yêu cầu thuê Personal Trainer nào.</p>
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
                <button onClick={onRefresh} className="btn btn-outline-secondary btn-sm">
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
                                {ptAssignment.created_at ? 
                                    new Date(ptAssignment.created_at).toLocaleString('vi-VN') 
                                    : 'Không có thông tin'
                                }
                            </span>
                        </div>
                        <div className="detail-item">
                            <span className="detail-label">Ghi chú:</span>
                            <span className="detail-value">{ptAssignment.request_notes}</span>
                        </div>
                        {ptAssignment.updated_at && (
                            <div className="detail-item">
                                <span className="detail-label">Cập nhật lần cuối:</span>
                                <span className="detail-value">
                                    {new Date(ptAssignment.updated_at).toLocaleString('vi-VN')}
                                </span>
                            </div>
                        )}
                    </div>
                </div>

                {ptAssignment.trainer_id && (
                    <div className="trainer-assignment">
                        <h4>PT được phân công</h4>
                        <div className="trainer-card">
                            <div className="trainer-avatar">
                                <i className="fas fa-user-tie"></i>
                            </div>
                            <div className="trainer-info">
                                <h5>{ptAssignment.trainer_name}</h5>
                                <p className="trainer-specialization">
                                    <i className="fas fa-star text-warning"></i>
                                    {ptAssignment.trainer_specialization}
                                </p>
                                <p className="trainer-email">
                                    <i className="fas fa-envelope text-muted"></i>
                                    {ptAssignment.trainer_email}
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
                        </div>
                    </div>
                )}
            </div>
            
            {showSchedule && (
                <TrainingSchedule
                    userId={ptAssignment.user_id}
                    trainerId={ptAssignment.trainer_id}
                    onClose={() => setShowSchedule(false)}
                />
            )}
        </div>
    );
};

export default PTStatusDisplay;