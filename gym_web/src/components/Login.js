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
        <Container className="mt-5">
            <Row className="justify-content-center">
                <Col md={6} lg={4}>
                    <Card className="shadow">
                        <Card.Body className="p-4">
                            <h1 className="text-center text-primary mb-4">
                                <i className="fas fa-sign-in-alt me-2"></i>
                                ĐĂNG NHẬP
                            </h1>
                            
                            <LoginForm 
                                user={user}
                                setState={setState}
                                login={login}
                                loading={loading}
                                error={error}
                            />
                            
                            <div className="text-center">
                                <small className="text-muted">
                                    Chưa có tài khoản?{" "}
                                    <a href="/register" className="text-decoration-none">
                                        Đăng ký ngay
                                    </a>
                                </small>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Login;
