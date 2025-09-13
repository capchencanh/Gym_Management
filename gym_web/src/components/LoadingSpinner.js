import React from 'react';

const LoadingSpinner = ({ 
    size = 'medium', 
    text = 'Đang tải...', 
    centered = true,
    className = '' 
}) => {
    const getSizeClass = () => {
        switch (size) {
            case 'small': return 'spinner-border-sm';
            case 'large': return 'spinner-border-lg';
            default: return '';
        }
    };

    const getTextSizeClass = () => {
        switch (size) {
            case 'small': return 'small';
            case 'large': return 'h5';
            default: return '';
        }
    };

    const containerClass = centered ? 'd-flex justify-content-center align-items-center' : '';
    const heightClass = centered ? 'min-vh-50' : '';

    return (
        <div className={`${containerClass} ${heightClass} ${className}`}>
            <div className="text-center">
                <div className={`spinner-border text-primary ${getSizeClass()}`} role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
                {text && (
                    <div className={`mt-2 text-muted ${getTextSizeClass()}`}>
                        {text}
                    </div>
                )}
            </div>
        </div>
    );
};

export default LoadingSpinner;
