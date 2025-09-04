import React from "react";
import MySpinner from "./Layouts/MySpinner";

const LoginForm = ({ user, setState, login, loading, error }) => {
    const info = [
        {
            type: "email",
            title: "Email",
            field: "email",
            icon: "fas fa-envelope"
        },
        {
            type: "password",
            title: "Mật khẩu",
            field: "password",
            icon: "fas fa-lock"
        },
    ];

    return (
        <form onSubmit={login} className="login-form">
            {error && (
                <div className="alert alert-danger">
                    <i className="fas fa-exclamation-circle me-2"></i>
                    {error}
                </div>
            )}

            {info.map((i) => (
                <div className="mb-3" key={i.field}>
                    <label htmlFor={i.field} className="form-label">
                        <i className={`${i.icon} me-2`}></i>
                        {i.title}
                    </label>
                    <input
                        type={i.type}
                        className="form-control"
                        id={i.field}
                        placeholder={i.title}
                        value={user[i.field] || ""}
                        onChange={(e) => setState(e.target.value, i.field)}
                        required
                    />
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
                    <i className="fas fa-sign-in-alt me-2"></i>
                    Đăng nhập
                </button>
            )}
        </form>
    );
};

export default LoginForm;
