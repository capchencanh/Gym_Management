import { createContext, useContext, useReducer } from 'react';

// User Context
export const MyUserContext = createContext();

// Dispatch Context
export const MyDispatchContext = createContext();

// Initial state
const initialState = {
    user: null,
    isAuthenticated: false,
    token: localStorage.getItem('token') || null
};

// Reducer function
const userReducer = (state, action) => {
    switch (action.type) {
        case 'login':
            return {
                ...state,
                user: action.payload.user,
                isAuthenticated: true,
                token: action.payload.token
            };
        
        case 'logout':
            localStorage.removeItem('token');
            return {
                ...state,
                user: null,
                isAuthenticated: false,
                token: null
            };
        
        case 'update_user':
            return {
                ...state,
                user: { ...state.user, ...action.payload }
            };
        
        case 'set_token':
            return {
                ...state,
                token: action.payload
            };
        
        default:
            return state;
    }
};

// Provider component
export const UserProvider = ({ children }) => {
    const [state, dispatch] = useReducer(userReducer, initialState);

    return (
        <MyUserContext.Provider value={state}>
            <MyDispatchContext.Provider value={dispatch}>
                {children}
            </MyDispatchContext.Provider>
        </MyUserContext.Provider>
    );
};

// Custom hooks
export const useUser = () => {
    const context = useContext(MyUserContext);
    if (!context) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return context;
};

export const useDispatch = () => {
    const context = useContext(MyDispatchContext);
    if (!context) {
        throw new Error('useDispatch must be used within a UserProvider');
    }
    return context;
};
