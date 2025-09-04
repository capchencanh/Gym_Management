import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import MySpinner from "./Layouts/MySpinner";
import Apis, { endpoints } from "../configs/Apis";

const Register = () => {
    const info = [
        {
            type: "text",
            title: "Họ và tên",
            field: "name",
            required: true
        },
        {
            type: "email",
            title: "Email",
            field: "email",
            required: true
        },
        {
            type: "password",
            title: "Mật khẩu",
            field: "password",
            required: true
        },
        {
            type: "password",
            title: "Xác nhận mật khẩu",
            field: "confirmPassword",
            required: true
        },
        {
            type: "tel",
            title: "Số điện thoại",
            field: "phoneNumber",
            required: false
        }
    ];

    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
    const nav = useNavigate();

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const validateForm = () => {
        if (!user.name || !user.email || !user.password || !user.confirmPassword) {
            setError("Vui lòng điền đầy đủ thông tin bắt buộc");
            return false;
        }

        if (user.password !== user.confirmPassword) {
            setError("Mật khẩu xác nhận không khớp");
            return false;
        }

        if (user.password.length < 6) {
            setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }



        return true;
    };

    const register = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        try {
            setLoading(true);
            setError(null);
            setSuccess(null);

            
            const registerData = {
                name: user.name,
                email: user.email,
                password: user.password,
                phoneNumber: user.phoneNumber || null
            };

           
            await Apis.post(endpoints["register"], registerData);
            
            setSuccess("Đăng ký thành công! Vui lòng đăng nhập để tiếp tục.");
            
          
            setTimeout(() => {
                nav("/login");
            }, 2000);

        } catch (ex) {
            if (ex.response) {
                switch (ex.response.status) {
                    case 400:
                        setError(ex.response.data || "Dữ liệu không hợp lệ");
                        break;
                    case 409:
                        setError("Email đã tồn tại trong hệ thống");
                        break;
                    default:
                        if (ex.response.data && typeof ex.response.data === 'string') {
                            setError(ex.response.data);
                        } else {
                            setError("Đăng ký thất bại. Vui lòng kiểm tra lại thông tin.");
                        }
                        break;
                }
            } else {
                setError("Có lỗi xảy ra. Vui lòng kiểm tra kết nối mạng và thử lại.");
            }
        } finally {
            setLoading(false);
        }
    };

    const icons = {
        name: "fas fa-user",
        email: "fas fa-envelope",
        password: "fas fa-lock",
        confirmPassword: "fas fa-lock",
        phoneNumber: "fas fa-phone"
    };

    const renderField = (field) => {
        if (field.type === "select") {
            return (
                <select
                    className="form-control"
                    required={field.required}
                    value={user[field.field] || ""}
                    onChange={(e) => setState(e.target.value, field.field)}
                >
                    {field.options.map((option, index) => (
                        <option key={index} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            );
        }

        return (
            <input
                type={field.type}
                className="form-control"
                placeholder={field.title}
                value={user[field.field] || ""}
                onChange={(e) => setState(e.target.value, field.field)}
                required={field.required}
            />
        );
    };

    return (
        <div className="page-container">
            <div className="page-header">
                <h1 className="page-title">
                    <i className="fas fa-user-plus me-2"></i>
                    ĐĂNG KÝ TÀI KHOẢN
                </h1>
                <p className="page-subtitle">
                    Tạo tài khoản mới để bắt đầu hành trình fitness của bạn
                </p>
            </div>
            
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="card">
                        <div className="card-body">
                            <form onSubmit={register} className="register-form">
                                {error && (
                                    <div className="alert alert-danger">
                                        <i className="fas fa-exclamation-circle me-2"></i>
                                        {error}
                                    </div>
                                )}
                                {success && (
                                    <div className="alert alert-success">
                                        <i className="fas fa-check-circle me-2"></i>
                                        {success}
                                    </div>
                                )}

                                {info.map((i) => (
                                    <div className="mb-3" key={i.field}>
                                        <label htmlFor={i.field} className="form-label">
                                            <i className={`${icons[i.field] || 'fas fa-edit'} me-2`}></i>
                                            {i.title}
                                            {i.required && <span className="text-danger"> *</span>}
                                        </label>
                                        {renderField(i)}
                                    </div>
                                ))}

                                {loading ? (
                                    <div className="loading-container">
                                        <MySpinner />
                                    </div>
                                ) : (
                                    <button 
                                        type="submit" 
                                        className="btn btn-primary w-100"
                                    >
                                        <i className="fas fa-user-plus me-2"></i>
                                        Đăng ký
                                    </button>
                                )}
                            </form>
                            
                            <div className="text-center mt-4">
                                <small className="text-muted">
                                    Đã có tài khoản?{" "}
                                    <a href="/login" className="text-decoration-none fw-medium">
                                        Đăng nhập ngay
                                    </a>
                                </small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Register;
