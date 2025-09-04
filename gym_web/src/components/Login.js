import React, { useContext, useState } from "react";
import { Col, Container, Row, Card } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../configs/Apis";
import { MyDispatchContext } from "../configs/Contexts";
import LoginForm from "./LoginForm";
import { handleLoginError } from "./LoginErrorHandler";

const Login = () => {
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const nav = useNavigate();
    const dispatch = useContext(MyDispatchContext);

    const setState = (value, field) => {
        setUser({ ...user, [field]: value });
    };

    const login = async (e) => {
        e.preventDefault();

        try {
            setLoading(true);
            setError(null);

            await Apis.post(endpoints["login"], { 
                email: user.email, 
                password: user.password 
            });

            let u = await Apis.get(endpoints["profile"]);

            dispatch({
                type: "login",
                payload: {
                    id: u.data.user_id,
                    email: u.data.email,
                    name: u.data.name,
                    role: u.data.role,
                    phone_number: u.data.phone_number,
                    gender: u.data.gender,
                    birthdate: u.data.birthdate,
                    height: u.data.height,
                    weight: u.data.weight,
                    fitness_goal: u.data.fitness_goal
                },
            });

            nav("/");
        } catch (ex) {
            setError(handleLoginError(ex));
        } finally {
            setLoading(false);
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
                                login={login}
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
