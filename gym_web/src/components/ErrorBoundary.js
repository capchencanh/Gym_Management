import React from 'react';

class ErrorBoundary extends React.Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false, error: null, errorInfo: null };
    }

    static getDerivedStateFromError(error) {
        return { hasError: true };
    }

    componentDidCatch(error, errorInfo) {
        this.setState({
            error: error,
            errorInfo: errorInfo
        });
        
        if (process.env.NODE_ENV === 'development') {
        }
    }

    render() {
        if (this.state.hasError) {
            return (
                <div className="error-boundary">
                    <div className="container">
                        <div className="row justify-content-center">
                            <div className="col-md-6">
                                <div className="card border-danger">
                                    <div className="card-body text-center">
                                        <i className="fas fa-exclamation-triangle text-danger mb-3" style={{fontSize: '3rem'}}></i>
                                        <h2 className="card-title text-danger">Đã xảy ra lỗi</h2>
                                        <p className="card-text">
                                            Xin lỗi, đã có lỗi xảy ra trong ứng dụng. Vui lòng thử lại sau.
                                        </p>
                                        <button 
                                            className="btn btn-primary"
                                            onClick={() => window.location.reload()}
                                        >
                                            <i className="fas fa-refresh me-2"></i>
                                            Tải lại trang
                                        </button>
                                        
                                        {process.env.NODE_ENV === 'development' && this.state.error && (
                                            <details className="mt-3 text-start">
                                                <summary className="btn btn-outline-secondary btn-sm">
                                                    Chi tiết lỗi (Development)
                                                </summary>
                                                <pre className="mt-2 p-2 bg-light border rounded" style={{fontSize: '0.8rem'}}>
                                                    {this.state.error && this.state.error.toString()}
                                                    <br />
                                                    {this.state.errorInfo.componentStack}
                                                </pre>
                                            </details>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            );
        }

        return this.props.children;
    }
}

export default ErrorBoundary;
