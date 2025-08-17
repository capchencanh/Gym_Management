import React, { createContext, useContext, useReducer, useEffect } from 'react';
import { MyUserContext, MyDispatchContext } from './Contexts';
import MyUserReducer from '../reducer/MyUserReducer';
import Apis, { endpoints } from './Apis';

const initialState = null;

export const UserProvider = ({ children }) => {
    const [state, dispatch] = useReducer(MyUserReducer, initialState);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            // Load user profile từ API
            const loadUserProfile = async () => {
                try {
                    const response = await Apis.get(endpoints['profile']);
                    const userData = response.data;
                    
                    dispatch({
                        type: "login",
                        payload: {
                            id: userData.user_id,
                            email: userData.email,
                            name: userData.name,
                            role: userData.role,
                            phone_number: userData.phone_number,
                            gender: userData.gender,
                            birthdate: userData.birthdate,
                            height: userData.height,
                            weight: userData.weight,
                            fitness_goal: userData.fitness_goal
                        },
                    });
                } catch (error) {
                    console.error('Lỗi khi load user profile:', error);
                    // Nếu token không hợp lệ, xóa khỏi localStorage
                    localStorage.removeItem('token');
                }
            };
            
            loadUserProfile();
        }
    }, []);

    return (
        <MyUserContext.Provider value={state}>
            <MyDispatchContext.Provider value={dispatch}>
                {children}
            </MyDispatchContext.Provider>
        </MyUserContext.Provider>
    );
};


export const useUser = () => {
    const context = useContext(MyUserContext);
    if (context === undefined) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return context;
};

export const useDispatch = () => {
    const context = useContext(MyDispatchContext);
    if (context === undefined) {
        throw new Error('useDispatch must be used within a UserProvider');
    }
    return context;
};
