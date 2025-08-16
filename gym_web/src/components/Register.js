import React, { useState } from "react";
import { Button, Col, Form, Alert, Container, Row, Card } from "react-bootstrap";
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

    const renderField = (field) => {
        if (field.type === "select") {
            return (
                <Form.Select
                    required={field.required}
                    value={user[field.field] || ""}
                    onChange={(e) => setState(e.target.value, field.field)}
                    size="lg"
                >
                    {field.options.map((option, index) => (
                        <option key={index} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </Form.Select>
            );
        }

        return (
            <Form.Control
                required={field.required}
                type={field.type}
                placeholder={field.title}
                value={user[field.field] || ""}
                onChange={(e) => setState(e.target.value, field.field)}
                size="lg"
            />
        );
    };

    return (
        <Container className="mt-5">
            <Row className="justify-content-center">
                <Col md={6} lg={4}>
                    <Card className="shadow">
                        <Card.Body className="p-4">
                            <h1 className="text-center text-primary mb-4">
                                <i className="fas fa-user-plus me-2"></i>
                                ĐĂNG KÝ TÀI KHOẢN
                            </h1>
                            
                            <Form onSubmit={register}>
                                {error && <Alert variant="danger">{error}</Alert>}
                                {success && <Alert variant="success">{success}</Alert>}

                                {info.map((i) => (
                                    <Form.Group className="mb-3" controlId={i.field} key={i.field}>
                                        <Form.Label>
                                            {i.title}
                                            {i.required && <span className="text-danger"> *</span>}
                                        </Form.Label>
                                        {renderField(i)}
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
                                        <i className="fas fa-user-plus me-2"></i>
                                        Đăng ký
                                    </Button>
                                )}
                            </Form>
                            
                            <div className="text-center">
                                <small className="text-muted">
                                    Đã có tài khoản?{" "}
                                    <a href="/login" className="text-decoration-none">
                                        Đăng nhập ngay
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

export default Register;
