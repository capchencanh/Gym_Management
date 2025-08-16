import React from 'react';
import { Spinner } from 'react-bootstrap';

const MySpinner = () => {
    return (
        <div className="text-center my-3">
            <Spinner animation="border" role="status" variant="primary">
                <span className="visually-hidden">Đang tải...</span>
            </Spinner>
        </div>
    );
};

export default MySpinner;
