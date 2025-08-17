import React, { useState, useEffect } from 'react';
import PTRequestForm from './PTRequestForm';
import PTStatusDisplay from './PTStatusDisplay';
import './PTManagement.css';

const PTManagement = ({ userId }) => {
    const [activeTab, setActiveTab] = useState('request');
    const [hasPTRequest, setHasPTRequest] = useState(false);

    useEffect(() => {
        // Kiểm tra xem user đã có yêu cầu PT chưa
        checkExistingPTRequest();
    }, [userId]);

    const checkExistingPTRequest = async () => {
        // Logic kiểm tra sẽ được implement sau
        // Tạm thời để false
        setHasPTRequest(false);
    };

    const handleTabChange = (tab) => {
        setActiveTab(tab);
    };

    const handlePTRequestSubmitted = () => {
        setHasPTRequest(true);
        setActiveTab('status');
    };

    if (!userId) {
        return (
            <div className="pt-management-container">
                <div className="no-user-message">
                    <i className="fas fa-user-lock text-muted"></i>
                    <h3>Vui lòng đăng nhập</h3>
                    <p>Bạn cần đăng nhập để sử dụng tính năng Personal Trainer</p>
                </div>
            </div>
        );
    }

    return (
        <div className="pt-management-container">
            <div className="pt-header">
                <h1>Quản lý Personal Trainer</h1>
                <p>Yêu cầu và theo dõi trạng thái PT của bạn</p>
            </div>

            <div className="pt-tabs">
                <button
                    className={`tab-button ${activeTab === 'request' ? 'active' : ''}`}
                    onClick={() => handleTabChange('request')}
                    disabled={hasPTRequest}
                >
                    <i className="fas fa-plus-circle"></i>
                    Yêu cầu PT
                </button>
                <button
                    className={`tab-button ${activeTab === 'status' ? 'active' : ''}`}
                    onClick={() => handleTabChange('status')}
                >
                    <i className="fas fa-info-circle"></i>
                    Trạng thái
                </button>
            </div>

            <div className="pt-content">
                {activeTab === 'request' && !hasPTRequest && (
                    <PTRequestForm 
                        userId={userId} 
                        onRequestSubmitted={handlePTRequestSubmitted}
                    />
                )}

                {activeTab === 'status' && (
                    <PTStatusDisplay userId={userId} />
                )}

                {activeTab === 'request' && hasPTRequest && (
                    <div className="pt-already-requested">
                        <div className="info-card">
                            <i className="fas fa-check-circle text-success"></i>
                            <h3>Đã gửi yêu cầu PT</h3>
                            <p>Bạn đã gửi yêu cầu thuê Personal Trainer.</p>
                            <p>Hãy chuyển sang tab "Trạng thái" để theo dõi tiến trình.</p>
                            <button 
                                className="btn btn-primary"
                                onClick={() => handleTabChange('status')}
                            >
                                <i className="fas fa-eye"></i> Xem trạng thái
                            </button>
                        </div>
                    </div>
                )}
            </div>

            <div className="pt-info-section">
                <div className="info-grid">
                    <div className="info-card">
                        <div className="info-icon">
                            <i className="fas fa-clock text-primary"></i>
                        </div>
                        <h4>Quy trình yêu cầu</h4>
                        <ol>
                            <li>Chọn thời gian rảnh trong tuần</li>
                            <li>Nhập ghi chú yêu cầu</li>
                            <li>Gửi yêu cầu</li>
                            <li>Admin xem xét và phân công</li>
                            <li>PT liên hệ để lên lịch tập</li>
                        </ol>
                    </div>

                    <div className="info-card">
                        <div className="info-icon">
                            <i className="fas fa-calendar-alt text-success"></i>
                        </div>
                        <h4>Lợi ích của PT</h4>
                        <ul>
                            <li>Lịch tập cá nhân hóa</li>
                            <li>Hướng dẫn chuyên nghiệp</li>
                            <li>Theo dõi tiến độ tập luyện</li>
                            <li>Động viên và hỗ trợ</li>
                            <li>Đạt mục tiêu nhanh chóng</li>
                        </ul>
                    </div>

                    <div className="info-card">
                        <div className="info-icon">
                            <i className="fas fa-question-circle text-info"></i>
                        </div>
                        <h4>Hỗ trợ</h4>
                        <p>Nếu bạn có câu hỏi về dịch vụ PT, vui lòng liên hệ:</p>
                        <div className="contact-info">
                            <p><i className="fas fa-phone"></i> Hotline: 1900-xxxx</p>
                            <p><i className="fas fa-envelope"></i> Email: support@gym.com</p>
                            <p><i className="fas fa-comments"></i> Chat trực tuyến</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PTManagement;
