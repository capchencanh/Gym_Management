import React, { useContext, useState } from "react";
import { Button, Col, Form, Alert, Container, Row, Card } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import MySpinner from "./Layouts/MySpinner";
import Apis, { endpoints } from "../configs/Apis";
import { MyDispatchContext } from "../configs/Contexts";

const Login = () => {
    const info = [
        {
            type: "email",
            title: "Email",
            field: "email",
        },
        {
            type: "password",
            title: "Mật khẩu",
            field: "password",
        },
    ];

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

            let res = await Apis.post(endpoints["login"], { 
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
            if (ex.response) {
                const data = ex.response.data;
                const dataMessage = typeof data === 'string' ? data : (data?.message || null);
                switch (ex.response.status) {
                    case 400:
                        setError(dataMessage || "Dữ liệu không hợp lệ");
                        break;
                    case 401:
                        setError(dataMessage || "Email hoặc mật khẩu không đúng");
                        break;
                    case 403:
                        if (typeof data === 'string') {
                            if (data.includes("không thể đăng nhập vào ứng dụng di động")) {
                                setError(data);
                            } else if (ex.response.data.includes("đã bị xóa")) {
                                setError(data);
                            } else {
                                setError("Tài khoản của bạn đã bị khóa hoặc không có quyền truy cập");
                            }
                        } else {
                            setError("Tài khoản của bạn đã bị khóa hoặc không có quyền truy cập");
                        }
                        break;
                    case 404:
                        setError("Tài khoản không tồn tại. Vui lòng kiểm tra lại email.");
                        break;
                    default:
                        if (typeof data === 'string') {
                            setError(data);
                        } else {
                            setError("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
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
                            

                            
                            <Form onSubmit={login}>
                                {error && <Alert variant="danger">{error}</Alert>}

                                {info.map((i) => (
                                    <Form.Group className="mb-3" controlId={i.field} key={i.field}>
                                        <Form.Label>{i.title}</Form.Label>
                                        <Form.Control
                                            required
                                            type={i.type}
                                            placeholder={i.title}
                                            value={user[i.field] || ""}
                                            onChange={(e) => setState(e.target.value, i.field)}
                                            size="lg"
                                        />
                                    </Form.Group>
                                ))}

                                {loading ? (
                                    <MySpinner />
                                ) : (
                                    <Button 
                                        type="submit" 
                                        variant="primary" 
                                        size="lg"
                                        className="w-100 mb-3"
                                    >
                                        <i className="fas fa-sign-in-alt me-2"></i>
                                        Đăng nhập
                                    </Button>
                                )}
                            </Form>
                            
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
