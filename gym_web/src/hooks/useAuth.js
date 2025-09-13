import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from '../configs/Contexts';
import Apis, { endpoints } from '../configs/Apis';
import { useApi } from './useApi';

/**
 * Authentication hook
 */
export const useAuth = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { loading, error, execute, reset } = useApi();

    const login = useCallback(async (email, password) => {
        return execute(
            () => Apis.post(endpoints['login'], { email, password }),
            async () => {
                const profileResponse = await Apis.get(endpoints['profile']);
                dispatch({
                    type: "login",
                    payload: {
                        id: profileResponse.data.user_id,
                        email: profileResponse.data.email,
                        name: profileResponse.data.name,
                        role: profileResponse.data.role,
                        phone_number: profileResponse.data.phone_number,
                        gender: profileResponse.data.gender,
                        birthdate: profileResponse.data.birthdate,
                        height: profileResponse.data.height,
                        weight: profileResponse.data.weight,
                        fitness_goal: profileResponse.data.fitness_goal,
                        avatar_url: profileResponse.data.avatar_url
                    }
                });
                navigate('/');
            }
        );
    }, [dispatch, navigate, execute]);

    const register = useCallback(async (userData) => {
        return execute(
            () => Apis.post(endpoints['register'], userData),
            () => {
                navigate('/login');
            }
        );
    }, [navigate, execute]);

    const logout = useCallback(async () => {
        return execute(
            () => Apis.post(endpoints['logout']),
            () => {
                dispatch({ type: "logout" });
                localStorage.clear();
                sessionStorage.clear();
                navigate('/login');
            }
        );
    }, [dispatch, navigate, execute]);

    return {
        login,
        register,
        logout,
        loading,
        error,
        reset
    };
};
