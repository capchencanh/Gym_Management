export const handleLoginError = (ex) => {
    if (ex.response) {
        const data = ex.response.data;
        const dataMessage = typeof data === 'string' ? data : (data?.message || null);
        
        switch (ex.response.status) {
            case 400:
                return dataMessage || "Dữ liệu không hợp lệ";
            case 401:
                return dataMessage || "Email hoặc mật khẩu không đúng";
            case 403:
                if (typeof data === 'string') {
                    if (data.includes("không thể đăng nhập vào ứng dụng di động")) {
                        return data;
                    } else if (data.includes("đã bị xóa")) {
                        return data;
                    } else {
                        return "Tài khoản của bạn đã bị khóa hoặc không có quyền truy cập";
                    }
                } else {
                    return "Tài khoản của bạn đã bị khóa hoặc không có quyền truy cập";
                }
            case 404:
                return "Tài khoản không tồn tại. Vui lòng kiểm tra lại email.";
            default:
                if (typeof data === 'string') {
                    return data;
                } else {
                    return "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.";
                }
        }
    } else {
        return "Có lỗi xảy ra. Vui lòng kiểm tra kết nối mạng và thử lại.";
    }
};
