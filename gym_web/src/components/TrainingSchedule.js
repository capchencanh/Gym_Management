import React, { useState, useEffect } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import './TrainingSchedule.css';

const TrainingSchedule = ({ userId, trainerId, onClose }) => {
    const [sessions, setSessions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (userId && trainerId) {
            loadTrainingSessions();
        }
    }, [userId, trainerId]);

    const loadTrainingSessions = async () => {
        try {
            setLoading(true);
            const response = await Apis.get(`${endpoints['pt-sessions-by-user-trainer']}/${userId}/trainer/${trainerId}`);
            setSessions(response.data || []);
        } catch (error) {
            console.error('Lỗi khi load lịch tập:', error);
            setError('Không thể tải lịch tập. Vui lòng thử lại sau.');
        } finally {
            setLoading(false);
        }
    };

    const getStatusInfo = (status) => {
        const statusConfig = {
            'SCHEDULED': {
                label: 'Đã lên lịch',
                color: 'info',
                icon: 'calendar-check'
            },
            'IN_PROGRESS': {
                label: 'Đang diễn ra',
                color: 'warning',
                icon: 'play-circle'
            },
            'COMPLETED': {
                label: 'Hoàn thành',
                color: 'success',
                icon: 'check-circle'
            },
            'CANCELLED': {
                label: 'Đã hủy',
                color: 'danger',
                icon: 'times-circle'
            },
            'NO_SHOW': {
                label: 'Không tham gia',
                color: 'secondary',
                icon: 'user-times'
            }
        };

        return statusConfig[status] || statusConfig['SCHEDULED'];
    };

    const formatTime = (timeStr) => {
        if (!timeStr) return '';
        return timeStr.substring(0, 5);
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleDateString('vi-VN', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    };

    if (loading) {
        return (
            <div className="training-schedule-modal">
                <div className="modal-content">
                    <div className="modal-header">
                        <h3>Lịch tập với PT</h3>
                        <button onClick={onClose} className="close-btn">&times;</button>
                    </div>
                    <div className="modal-body">
                        <div className="loading-spinner">
                            <div className="spinner-border text-primary" role="status">
                                <span className="visually-hidden">Đang tải...</span>
                            </div>
                            <p>Đang tải lịch tập...</p>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="training-schedule-modal">
                <div className="modal-content">
                    <div className="modal-header">
                        <h3>Lịch tập với PT</h3>
                        <button onClick={onClose} className="close-btn">&times;</button>
                    </div>
                    <div className="modal-body">
                        <div className="error-message">
                            <i className="fas fa-exclamation-triangle text-warning"></i>
                            <p>{error}</p>
                            <button onClick={loadTrainingSessions} className="btn btn-outline-primary">
                                <i className="fas fa-redo"></i> Thử lại
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="training-schedule-modal">
            <div className="modal-content">
                <div className="modal-header">
                    <h3>Lịch tập với PT</h3>
                    <button onClick={onClose} className="close-btn">&times;</button>
                </div>
                <div className="modal-body">
                    {sessions.length === 0 ? (
                        <div className="no-sessions">
                            <i className="fas fa-calendar-times text-muted"></i>
                            <h4>Chưa có lịch tập</h4>
                            <p>PT chưa lên lịch tập cho bạn. Hãy liên hệ với PT để được sắp xếp lịch tập phù hợp.</p>
                        </div>
                                         ) : (
                         <div className="sessions-list">
                             <h4>Danh sách buổi tập</h4>
                             {sessions.map((session) => {
                                 const statusInfo = getStatusInfo(session.status);
                                 return (
                                     <div key={session.sessionId} className="session-card">
                                                                                 <div className="session-header">
                                             <div className="session-date">
                                                 <i className="fas fa-calendar-day text-primary"></i>
                                                 <span>{formatDate(session.session_date || session.sessionDate)}</span>
                                             </div>
                                             <div className="session-time">
                                                 <i className="fas fa-clock text-info"></i>
                                                 <span>{formatTime(session.start_time || session.startTime)} - {formatTime(session.end_time || session.endTime)}</span>
                                             </div>
                                         </div>
                                        <div className="session-status">
                                            <span className={`badge bg-${statusInfo.color}`}>
                                                <i className={`fas fa-${statusInfo.icon}`}></i>
                                                {statusInfo.label}
                                            </span>
                                        </div>
                                        {session.notes && (
                                            <div className="session-notes">
                                                <i className="fas fa-sticky-note text-muted"></i>
                                                <span>{session.notes}</span>
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
                <div className="modal-footer">
                    <button onClick={onClose} className="btn btn-secondary">
                        Đóng
                    </button>
                </div>
            </div>
        </div>
    );
};

export default TrainingSchedule;
