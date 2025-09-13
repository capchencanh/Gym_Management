import { useState, useCallback } from 'react';

export const useApi = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const execute = useCallback(async (apiCall, onSuccess, onError) => {
        try {
            setLoading(true);
            setError(null);
            const result = await apiCall();
            if (onSuccess) {
                onSuccess(result);
            }
            return result;
        } catch (err) {
            const errorMessage = err.response?.data?.message || err.message || 'Có lỗi xảy ra';
            setError(errorMessage);
            if (onError) {
                onError(err);
            }
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    const reset = useCallback(() => {
        setError(null);
        setLoading(false);
    }, []);

    return { loading, error, execute, reset };
};
