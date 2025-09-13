import React, { useState, useContext, useEffect } from 'react';
import { MyUserContext, MyDispatchContext } from '../configs/Contexts';
import Apis, { endpoints } from '../configs/Apis';
import './Profile.css';

const Profile = () => {
    const user = useContext(MyUserContext);
    const dispatch = useContext(MyDispatchContext);
    
    const [formData, setFormData] = useState({
        name: '',
        phone_number: '',
        gender: '',
        birthdate: '',
        height: '',
        weight: '',
        fitness_goal: ''
    });
    
    const [passwordData, setPasswordData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    
    const [avatarFile, setAvatarFile] = useState(null);
    const [avatarPreview, setAvatarPreview] = useState(null);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: '', text: '' });

    useEffect(() => {
        if (user) {
            setFormData({
                name: user.name || '',
                phone_number: user.phone_number || '',
                gender: user.gender || '',
                birthdate: user.birthdate ? user.birthdate.split('T')[0] : '',
                height: user.height || '',
                weight: user.weight || '',
                fitness_goal: user.fitness_goal || ''
            });
            setAvatarPreview(user.avatar_url);
        }
    }, [user]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handlePasswordChange = (e) => {
        const { name, value } = e.target;
        setPasswordData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleAvatarChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setAvatarFile(file);
            const reader = new FileReader();
            reader.onload = (e) => {
                setAvatarPreview(e.target.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            const payload = {
                ...formData,
                height: formData.height !== '' && formData.height !== null ? parseFloat(formData.height) : null,
                weight: formData.weight !== '' && formData.weight !== null ? parseFloat(formData.weight) : null
            };
            const response = await Apis.put(endpoints['update-profile'], payload);
            
            const updatedUser = { ...user, ...payload };
            dispatch({
                type: "update",
                payload: updatedUser
            });

            setMessage({ type: 'success', text: 'Cập nhật thông tin thành công!' });
        } catch (error) {
            setMessage({ 
                type: 'error', 
                text: error.response?.data || 'Có lỗi xảy ra khi cập nhật thông tin' 
            });
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateAvatar = async (e) => {
        e.preventDefault();
        if (!avatarFile) {
            setMessage({ type: 'error', text: 'Vui lòng chọn ảnh để tải lên' });
            return;
        }

        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            const formData = new FormData();
            formData.append('avatarFile', avatarFile);

            const response = await Apis.post(endpoints['update-avatar'], formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });

            const updatedUser = { ...user, avatar_url: response.data.avatar_url };
            dispatch({
                type: "update",
                payload: updatedUser
            });

            setMessage({ type: 'success', text: 'Cập nhật ảnh đại diện thành công!' });
            setAvatarFile(null);
        } catch (error) {
            setMessage({ 
                type: 'error', 
                text: error.response?.data || 'Có lỗi xảy ra khi cập nhật ảnh đại diện' 
            });
        } finally {
            setLoading(false);
        }
    };

    const handleChangePassword = async (e) => {
        e.preventDefault();
        
        if (passwordData.newPassword !== passwordData.confirmPassword) {
            setMessage({ type: 'error', text: 'Mật khẩu mới không khớp' });
            return;
        }

        if (passwordData.newPassword.length < 6) {
            setMessage({ type: 'error', text: 'Mật khẩu mới phải có ít nhất 6 ký tự' });
            return;
        }

        setLoading(true);
        setMessage({ type: '', text: '' });

        try {
            await Apis.post(endpoints['change-password'], {
                currentPassword: passwordData.currentPassword,
                newPassword: passwordData.newPassword
            });

            setMessage({ type: 'success', text: 'Đổi mật khẩu thành công!' });
            setPasswordData({
                currentPassword: '',
                newPassword: '',
                confirmPassword: ''
            });
        } catch (error) {
            setMessage({ 
                type: 'error', 
                text: error.response?.data || 'Có lỗi xảy ra khi đổi mật khẩu' 
            });
        } finally {
            setLoading(false);
        }
    };

    if (!user) {
        return <div className="container mt-5">Vui lòng đăng nhập để xem profile</div>;
    }

    return (
        <div className="page-container">
                    <div className="page-header">
                        <h1 className="page-title">
                            <i className="fas fa-user me-2"></i>
                            Profile
                        </h1>
                        <p className="page-subtitle">
                            Quản lý thông tin cá nhân và tài khoản
                        </p>
                    </div>

                    {message.text && (
                        <div className={`alert alert-${message.type === 'success' ? 'success' : 'danger'}`}>
                            <i className={`fas fa-${message.type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2`}></i>
                            {message.text}
                            <button type="button" className="btn-close" onClick={() => setMessage({ type: '', text: '' })}></button>
                        </div>
                    )}

                    <div className="section">
                        <div className="card">
                            <div className="card-body">
                                <div className="profile-info">
                                    <div className="profile-avatar">
                                        {avatarPreview ? (
                                            <img src={avatarPreview} alt="User Avatar" />
                                        ) : (
                                            <i className="fas fa-user"></i>
                                        )}
                                    </div>
                                    <div className="profile-details">
                                        <h2>{user.name}</h2>
                                        <div className="detail-row">
                                            <div>
                                                <p><i className="fas fa-envelope"></i>{user.email}</p>
                                                <p><i className="fas fa-phone"></i>{user.phone_number || 'Chưa cập nhật'}</p>
                                            </div>
                                            <div>
                                                <p><i className="fas fa-calendar"></i>{user.birthdate ? new Date(user.birthdate).toLocaleDateString('vi-VN') : 'N/A'}</p>
                                                <p><i className="fas fa-venus-mars"></i>{user.gender}</p>
                                                <p><i className="fas fa-bullseye"></i>{user.fitness_goal || 'Chưa thiết lập mục tiêu'}</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="section">
                        <div className="card">
                            <div className="card-body">
                                <h3 className="section-title">
                                    <i className="fas fa-image me-2"></i>
                                    Cập nhật ảnh đại diện
                                </h3>
                                <form onSubmit={handleUpdateAvatar}>
                                    <div className="mb-3">
                                        <label htmlFor="avatar-input" className="form-label">Chọn ảnh mới để thay thế:</label>
                                        <input 
                                            type="file" 
                                            className="form-control" 
                                            id="avatar-input" 
                                            accept="image/*"
                                            onChange={handleAvatarChange}
                                            required
                                        />
                                    </div>
                                    <div className="text-center">
                                        <button type="submit" className="btn btn-primary" disabled={loading}>
                                            <i className="fas fa-upload me-2"></i>
                                            {loading ? 'Đang tải...' : 'Tải lên'}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <div className="section">
                        <div className="card">
                            <div className="card-body">
                                <h3 className="section-title">
                                    <i className="fas fa-edit me-2"></i>
                                    Cập nhật thông tin
                                </h3>
                                <form onSubmit={handleUpdateProfile}>
                                    <div className="row">
                                        <div className="col-md-6 mb-3">
                                            <label className="form-label">Họ và tên</label>
                                            <input 
                                                type="text" 
                                                className="form-control" 
                                                name="name"
                                                value={formData.name}
                                                onChange={handleInputChange}
                                                required
                                            />
                                        </div>
                                        <div className="col-md-6 mb-3">
                                            <label className="form-label">Số điện thoại</label>
                                            <input 
                                                type="tel" 
                                                className="form-control" 
                                                name="phone_number"
                                                value={formData.phone_number}
                                                onChange={handleInputChange}
                                                required
                                            />
                                        </div>
                                    </div>

                                    <div className="row">
                                        <div className="col-md-4 mb-3">
                                            <label className="form-label">Ngày sinh</label>
                                            <input 
                                                type="date" 
                                                className="form-control" 
                                                name="birthdate"
                                                value={formData.birthdate}
                                                onChange={handleInputChange}
                                                required
                                            />
                                        </div>
                                        <div className="col-md-4 mb-3">
                                            <label className="form-label">Giới tính</label>
                                            <select 
                                                className="form-select" 
                                                name="gender"
                                                value={formData.gender}
                                                onChange={handleInputChange}
                                                required
                                            >
                                                <option value="">Chọn giới tính</option>
                                                <option value="Nam">Nam</option>
                                                <option value="Nữ">Nữ</option>
                                                <option value="Khác">Khác</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div className="row">
                                        <div className="col-md-6 mb-3">
                                            <label className="form-label">Chiều cao (cm)</label>
                                            <input 
                                                type="number" 
                                                className="form-control" 
                                                name="height"
                                                value={formData.height}
                                                onChange={handleInputChange}
                                                min="100" 
                                                max="250"
                                            />
                                        </div>
                                        <div className="col-md-6 mb-3">
                                            <label className="form-label">Cân nặng (kg)</label>
                                            <input 
                                                type="number" 
                                                className="form-control" 
                                                name="weight"
                                                value={formData.weight}
                                                onChange={handleInputChange}
                                                min="30" 
                                                max="200" 
                                                step="0.1"
                                            />
                                        </div>
                                    </div>

                                    <div className="row">
                                        <div className="col-md-12 mb-3">
                                            <label className="form-label">Mục tiêu tập luyện/dinh dưỡng</label>
                                            <input
                                                type="text"
                                                className="form-control"
                                                name="fitness_goal"
                                                placeholder="Ví dụ: Giảm mỡ 5kg trong 3 tháng, tăng cơ phần thân trên..."
                                                value={formData.fitness_goal}
                                                onChange={handleInputChange}
                                            />
                                        </div>
                                    </div>

                                    <div className="text-center">
                                        <button type="submit" className="btn btn-primary" disabled={loading}>
                                            <i className="fas fa-save me-2"></i>
                                            {loading ? 'Đang cập nhật...' : 'Cập nhật thông tin'}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <div className="section">
                        <div className="card">
                            <div className="card-body">
                                <h3 className="section-title">
                                    <i className="fas fa-lock me-2"></i>
                                    Đổi mật khẩu
                                </h3>
                                <form onSubmit={handleChangePassword}>
                                    <div className="row">
                                        <div className="col-md-4 mb-3">
                                            <label className="form-label">Mật khẩu hiện tại</label>
                                            <input 
                                                type="password" 
                                                className="form-control" 
                                                name="currentPassword"
                                                value={passwordData.currentPassword}
                                                onChange={handlePasswordChange}
                                                required
                                            />
                                        </div>
                                        <div className="col-md-4 mb-3">
                                            <label className="form-label">Mật khẩu mới</label>
                                            <input 
                                                type="password" 
                                                className="form-control" 
                                                name="newPassword"
                                                value={passwordData.newPassword}
                                                onChange={handlePasswordChange}
                                                required 
                                                minLength="6"
                                            />
                                        </div>
                                        <div className="col-md-4 mb-3">
                                            <label className="form-label">Xác nhận mật khẩu mới</label>
                                            <input 
                                                type="password" 
                                                className="form-control" 
                                                name="confirmPassword"
                                                value={passwordData.confirmPassword}
                                                onChange={handlePasswordChange}
                                                required 
                                                minLength="6"
                                            />
                                        </div>
                                    </div>

                                    <div className="text-center">
                                        <button type="submit" className="btn btn-primary" disabled={loading}>
                                            <i className="fas fa-key me-2"></i>
                                            {loading ? 'Đang đổi...' : 'Đổi mật khẩu'}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
        </div>
    );
};

export default Profile;
