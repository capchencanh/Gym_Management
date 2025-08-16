import React, { createContext, useContext, useReducer, useEffect } from 'react';
import { MyUserContext, MyDispatchContext } from './Contexts';
import MyUserReducer from '../reducer/MyUserReducer';
import cookie from 'react-cookies';


const initialState = null;


export const UserProvider = ({ children }) => {
    const [state, dispatch] = useReducer(MyUserReducer, initialState);


    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
         
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
