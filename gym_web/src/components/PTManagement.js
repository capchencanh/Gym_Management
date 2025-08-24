import React, { useState, useEffect } from 'react';
import PTRequestForm from './PTRequestForm';
import PTStatusDisplay from './PTStatusDisplay';
import Chat from './Chat';
import './PTManagement.css';
import Apis, { endpoints } from '../configs/Apis'; 

const PTManagement = ({ userId }) => {
    const [activeTab, setActiveTab] = useState('status'); 
    const [hasPTRequest, setHasPTRequest] = useState(false);
    const [ptAssignment, setPtAssignment] = useState(null);
    const [loading, setLoading] = useState(true); 

    useEffect(() => {
        if (userId) {
            checkPTAssignment();
        } else {
            setLoading(false);
        }
    }, [userId]);

    const checkPTAssignment = async () => {
        setLoading(true);
        try {
          
           const response = await Apis.get(`${endpoints['pt-status']}/${userId}`);
            
            if (response.data && response.data.length > 0) {
              
                const assignment = response.data.find(a => a.status === 'ACTIVE' || a.status === 'PENDING');
                if (assignment) {
                    setPtAssignment(assignment);
                    setHasPTRequest(true);
                } else {
                    setHasPTRequest(false); 
                }
            } else {
                setHasPTRequest(false);
            }
        } catch (error) {
            console.error('Error checking PT assignment:', error);
            setHasPTRequest(false);
        } finally {
            setLoading(false);
        }
    };

    const handleTabChange = (tab) => {
        setActiveTab(tab);
    };

    const handlePTRequestSubmitted = () => {
        checkPTAssignment(); 
    };

    if (loading) {
        return <div className="pt-management-container"><p>Đang tải dữ liệu...</p></div>;
    }

    if (!userId) {
        return (
            <div className="pt-management-container">
                <div className="no-user-message">
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
                    <i className="fas fa-plus-circle"></i> Yêu cầu PT
                </button>
                <button
                    className={`tab-button ${activeTab === 'status' ? 'active' : ''}`}
                    onClick={() => handleTabChange('status')}
                    disabled={!hasPTRequest}
                >
                    <i className="fas fa-info-circle"></i> Trạng thái
                </button>
                {ptAssignment && ptAssignment.status === 'ACTIVE' && (
                    <button
                        className={`tab-button ${activeTab === 'chat' ? 'active' : ''}`}
                        onClick={() => handleTabChange('chat')}
                    >
                        <i className="fas fa-comments"></i> Liên hệ PT
                    </button>
                )}
            </div>

            <div className="pt-content">
                {activeTab === 'request' && !hasPTRequest && (
                    <PTRequestForm 
                        userId={userId} 
                        onRequestSubmitted={handlePTRequestSubmitted}
                    />
                )}

                {activeTab === 'status' && hasPTRequest && (
                  
                    <PTStatusDisplay ptAssignment={ptAssignment} onRefresh={checkPTAssignment} />
                )}

                {activeTab === 'chat' && ptAssignment && (
                    <Chat 
                        isOpen={true}
                        onClose={() => handleTabChange('status')}
                        currentUserId={userId}
                        receiverId={ptAssignment.trainer_id}
                        receiverName={ptAssignment.trainer_name}
                    />
                )}

                 {}
                 {(activeTab === 'request' || !hasPTRequest) && hasPTRequest && (
                     <div className="pt-already-requested">
                        <div className="info-card">
                             <h3>Đã gửi yêu cầu PT</h3>
                             <p>Hãy chuyển sang tab "Trạng thái" để theo dõi tiến trình.</p>
                             <button 
                                 className="btn btn-primary"
                                onClick={() => handleTabChange('status')}
                            >
                                Xem trạng thái
                            </button>
                        </div>
                    </div>
                )}
                 
                 {activeTab === 'status' && !hasPTRequest && (
                     <div className="pt-already-requested">
                        <div className="info-card">
                             <h3>Chưa có yêu cầu PT</h3>
                             <p>Hãy chuyển sang tab "Yêu cầu PT" để bắt đầu.</p>
                             <button 
                                 className="btn btn-primary"
                                onClick={() => handleTabChange('request')}
                            >
                                Gửi yêu cầu
                            </button>
                        </div>
                    </div>
                 )}
            </div>
        </div>
    );
};

export default PTManagement;