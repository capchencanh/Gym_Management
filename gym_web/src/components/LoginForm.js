import React from "react";
import { Button, Form, Alert } from "react-bootstrap";
import MySpinner from "./Layouts/MySpinner";

const LoginForm = ({ user, setState, login, loading, error }) => {
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

    return (
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
    );
};

export default LoginForm;
