import React, { useState, useEffect } from 'react';
import Apis, { endpoints } from '../configs/Apis';
import './PTRequestForm.css';

const PTRequestForm = ({ userId }) => {
    const [requestNotes, setRequestNotes] = useState('');
    const [availabilities, setAvailabilities] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [ptRequestStatus, setPtRequestStatus] = useState(null);


    const daysOfWeek = [
        { value: 'MONDAY', label: 'Thứ 2' },
        { value: 'TUESDAY', label: 'Thứ 3' },
        { value: 'WEDNESDAY', label: 'Thứ 4' },
        { value: 'THURSDAY', label: 'Thứ 5' },
        { value: 'FRIDAY', label: 'Thứ 6' },
        { value: 'SATURDAY', label: 'Thứ 7' },
        { value: 'SUNDAY', label: 'Chủ nhật' }
    ];

    const timeSlots = [
        '06:00', '07:00', '08:00', '09:00', '10:00', '11:00',
        '12:00', '13:00', '14:00', '15:00', '16:00', '17:00',
        '18:00', '19:00', '20:00', '21:00', '22:00'
    ];

    useEffect(() => {
        if (userId) {
            checkPTRequestStatus();
            loadUserAvailabilities();
        }
    }, [userId]);

                   
                const checkPTRequestStatus = async () => {
                    try {
                        const response = await Apis.get(`${endpoints['pt-status']}/${userId}`);
                        if (response.data && response.data.length > 0) {
                            setPtRequestStatus(response.data[0]);
                        }
                    } catch (error) {
                        console.error('Lỗi khi kiểm tra trạng thái PT:', error);
                    }
                };

                  
                const loadUserAvailabilities = async () => {
                    try {
                        const response = await Apis.get(`${endpoints['pt-availability']}/${userId}`);
                        setAvailabilities(response.data);
                    } catch (error) {
                        console.error('Lỗi khi load thời gian rảnh:', error);
                    }
                };

   
    const addAvailability = () => {
        setAvailabilities([...availabilities, {
            dayOfWeek: 'MONDAY',
            startTime: '18:00',
            endTime: '19:00',
            isAvailable: true
        }]);
    };

  
    const updateAvailability = (index, field, value) => {
        const newAvailabilities = [...availabilities];
        newAvailabilities[index][field] = value;
        setAvailabilities(newAvailabilities);
    };

  
    const removeAvailability = (index) => {
        const newAvailabilities = availabilities.filter((_, i) => i !== index);
        setAvailabilities(newAvailabilities);
    };

                   
                const saveAvailabilities = async () => {
                    setLoading(true);
                    try {
                        for (const availability of availabilities) {
                            await Apis.post(endpoints['pt-availability'], {
                                userId: userId,
                                dayOfWeek: availability.dayOfWeek,
                                startTime: availability.startTime,
                                endTime: availability.endTime
                            });
                        }
                        setMessage('Đã lưu thời gian rảnh thành công!');
                        setTimeout(() => setMessage(''), 3000);
                    } catch (error) {
                        setMessage('Lỗi khi lưu thời gian rảnh: ' + error.response?.data || error.message);
                        setTimeout(() => setMessage(''), 5000);
                    } finally {
                        setLoading(false);
                    }
                };

                   
                const submitPTRequest = async () => {
                    if (!requestNotes.trim()) {
                        setMessage('Vui lòng nhập ghi chú yêu cầu!');
                        return;
                    }

                    setLoading(true);
                    try {
                        const response = await Apis.post(endpoints['pt-request'], {
                            userId: userId,
                            requestNotes: requestNotes
                        });
                        
                        setMessage('Đã gửi yêu cầu PT thành công! Admin sẽ xem xét và phân công PT cho bạn.');
                        setPtRequestStatus(response.data);
                        setTimeout(() => setMessage(''), 5000);
                    } catch (error) {
                        setMessage('Lỗi khi gửi yêu cầu PT: ' + error.response?.data || error.message);
                        setTimeout(() => setMessage(''), 5000);
                    } finally {
                        setLoading(false);
                    }
                };

    
    if (ptRequestStatus) {
        return (
            <div className="pt-request-status">
                <div className="status-card">
                    <h3>Trạng thái yêu cầu PT</h3>
                    <div className="status-info">
                        <p><strong>Trạng thái:</strong> 
                            <span className={`status-badge status-${ptRequestStatus.status?.toLowerCase() || 'pending'}`}>
                                {getStatusLabel(ptRequestStatus.status)}
                            </span>
                        </p>
                        <p><strong>Ghi chú:</strong> {ptRequestStatus.requestNotes || 'Không có'}</p>
                        <p><strong>Ngày yêu cầu:</strong> {ptRequestStatus.createdAt ? new Date(ptRequestStatus.createdAt).toLocaleDateString('vi-VN') : 'Không xác định'}</p>
                        
                        {ptRequestStatus.trainer && ptRequestStatus.trainer.user && (
                            <div className="trainer-info">
                                <h4>PT được phân công:</h4>
                                <p><strong>Tên:</strong> {ptRequestStatus.trainer.user.name || 'Không xác định'}</p>
                                <p><strong>Chuyên môn:</strong> {ptRequestStatus.trainer.specialization || 'Không xác định'}</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="pt-request-form">
            <div className="form-header">
                <h2>Yêu cầu Personal Trainer</h2>
                <p>Chọn thời gian rảnh trong tuần để admin có thể phân công PT phù hợp</p>
            </div>

            {message && (
                <div className={`alert ${message.includes('Lỗi') ? 'alert-danger' : 'alert-success'}`}>
                    {message}
                </div>
            )}

            <div className="form-section">
                <h3>Thông tin yêu cầu</h3>
                <div className="form-group">
                    <label htmlFor="requestNotes">Ghi chú yêu cầu:</label>
                    <textarea
                        id="requestNotes"
                        value={requestNotes}
                        onChange={(e) => setRequestNotes(e.target.value)}
                        placeholder="Mô tả mục tiêu tập luyện, mức độ tập luyện hiện tại..."
                        rows="4"
                        className="form-control"
                    />
                </div>
            </div>

            <div className="form-section">
                <div className="section-header">
                    <h3>Thời gian rảnh trong tuần</h3>
                    <button 
                        type="button" 
                        onClick={addAvailability}
                        className="btn btn-outline-primary btn-sm"
                    >
                        <i className="fas fa-plus"></i> Thêm thời gian
                    </button>
                </div>

                {availabilities.length === 0 ? (
                    <div className="no-availability">
                        <p>Chưa có thời gian rảnh nào. Hãy thêm thời gian rảnh của bạn!</p>
                        <button 
                            type="button" 
                            onClick={addAvailability}
                            className="btn btn-primary"
                        >
                            Thêm thời gian đầu tiên
                        </button>
                    </div>
                ) : (
                    <div className="availabilities-list">
                        {availabilities.map((availability, index) => (
                            <div key={index} className="availability-item">
                                <div className="availability-row">
                                    <div className="form-group">
                                        <label>Ngày:</label>
                                        <select
                                            value={availability.dayOfWeek}
                                            onChange={(e) => updateAvailability(index, 'dayOfWeek', e.target.value)}
                                            className="form-select"
                                        >
                                            {daysOfWeek.map(day => (
                                                <option key={day.value} value={day.value}>
                                                    {day.label}
                                                </option>
                                            ))}
                                        </select>
                                    </div>

                                    <div className="form-group">
                                        <label>Giờ bắt đầu:</label>
                                        <select
                                            value={availability.startTime}
                                            onChange={(e) => updateAvailability(index, 'startTime', e.target.value)}
                                            className="form-select"
                                        >
                                            {timeSlots.map(time => (
                                                <option key={time} value={time}>{time}</option>
                                            ))}
                                        </select>
                                    </div>

                                    <div className="form-group">
                                        <label>Giờ kết thúc:</label>
                                        <select
                                            value={availability.endTime}
                                            onChange={(e) => updateAvailability(index, 'endTime', e.target.value)}
                                            className="form-select"
                                        >
                                            {timeSlots.map(time => (
                                                <option key={time} value={time}>{time}</option>
                                            ))}
                                        </select>
                                    </div>

                                    <button
                                        type="button"
                                        onClick={() => removeAvailability(index)}
                                        className="btn btn-outline-danger btn-sm"
                                        title="Xóa thời gian này"
                                    >
                                        <i className="fas fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            <div className="form-actions">
                <button
                    type="button"
                    onClick={saveAvailabilities}
                    disabled={loading || availabilities.length === 0}
                    className="btn btn-secondary me-2"
                >
                    {loading ? 'Đang lưu...' : 'Lưu thời gian rảnh'}
                </button>

                <button
                    type="button"
                    onClick={submitPTRequest}
                    disabled={loading || !requestNotes.trim() || availabilities.length === 0}
                    className="btn btn-primary"
                >
                    {loading ? 'Đang gửi...' : 'Gửi yêu cầu PT'}
                </button>
            </div>

            <div className="form-info">
                <div className="info-card">
                    <h4>Quy trình yêu cầu PT:</h4>
                    <ol>
                        <li>Chọn thời gian rảnh trong tuần</li>
                        <li>Nhập ghi chú yêu cầu</li>
                        <li>Gửi yêu cầu</li>
                        <li>Admin sẽ xem xét và phân công PT</li>
                        <li>PT sẽ liên hệ để lên lịch tập cụ thể</li>
                    </ol>
                </div>
            </div>
        </div>
    );
};

// Helper function
const getStatusLabel = (status) => {
    if (!status) return 'Không xác định';
    
    const statusLabels = {
        'PENDING': 'Đang chờ phân công',
        'ASSIGNED': 'Đã được phân công',
        'ACTIVE': 'Đang hoạt động',
        'COMPLETED': 'Hoàn thành',
        'CANCELLED': 'Hủy bỏ'
    };
    return statusLabels[status] || status;
};

export default PTRequestForm;
