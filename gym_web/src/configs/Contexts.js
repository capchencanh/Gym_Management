import { createContext, useContext, useMemo } from "react";

export const MyUserContext = createContext();
export const MyDispatchContext = createContext();

export const useUser = () => {
    const context = useContext(MyUserContext);
    if (context === undefined) {
        throw new Error('useUser must be used within a MyUserContext.Provider');
    }
    return context;
};

export const useDispatch = () => {
    const context = useContext(MyDispatchContext);
    if (context === undefined) {
        throw new Error('useDispatch must be used within a MyDispatchContext.Provider');
    }
    return context;
};

export const useUserContext = () => {
    const user = useUser();
    const dispatch = useDispatch();
    
    return useMemo(() => ({
        user,
        dispatch,
        isAuthenticated: !!user,
        isAdmin: user?.role === 'ADMIN',
        isPT: user?.role === 'PT',
        isUser: user?.role === 'USER'
    }), [user, dispatch]);
};
