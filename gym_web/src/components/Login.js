import React, { useState } from "react";
import { Col, Container, Row, Card } from "react-bootstrap";
import LoginForm from "./LoginForm";
import { useAuth } from "../hooks/useAuth";

/**
 * Login component
 */
const Login = () => {
    const [user, setUser] = useState({});
    const { login, loading, error, reset } = useAuth();

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        reset();
        
        try {
            await login(user.email, user.password);
        } catch (ex) {
        }
    };

    return (
        <div className="page-container">
            <div className="page-header">
                <h1 className="page-title">
                    <i className="fas fa-sign-in-alt me-2"></i>
                    ĐĂNG NHẬP
                </h1>
                <p className="page-subtitle">
                    Đăng nhập vào tài khoản của bạn để tiếp tục
                </p>
            </div>
            
            <div className="row justify-content-center">
                <div className="col-md-6 col-lg-4">
                    <div className="card">
                        <div className="card-body">
                            <LoginForm 
                                user={user}
                                setState={setState}
                                login={handleLogin}
                                loading={loading}
                                error={error}
                            />
                            
                            <div className="text-center mt-4">
                                <small className="text-muted">
                                    Chưa có tài khoản?{" "}
                                    <a href="/register" className="text-decoration-none fw-medium">
                                        Đăng ký ngay
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

export default Login;
