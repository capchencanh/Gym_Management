import axios from 'axios';

const BASE_URL = 'http://localhost:8080/';

const Apis = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Add request interceptor to include auth token
Apis.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor to handle errors
Apis.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export const endpoints = {
    // Auth endpoints
    'login': 'api/auth/login',
    'register': 'api/auth/register',
    'logout': 'api/auth/logout',
    
    // User endpoints
    'users': 'api/users',
    'profile': 'api/users/profile',
    'update-profile': 'api/users/update-profile',
    
    // Categories endpoints
    'categories2': 'api/categories2',
    
    // Workout endpoints
    'workout-logs': 'api/workout-logs',
    'workout-history': 'api/workout-logs/history',
    'schedule': 'api/schedule',
    
    // Diet endpoints
    'diet-plans': 'api/diet-plans',
    'meal-suggestions': 'api/meals/suggestions',
    
    // Class endpoints
    'classes': 'api/classes',
    'enrollments': 'api/enrollments',
    
    // PT Chat endpoints
    'pt-chat': 'api/pt-chat',
    'messages': 'api/messages',
    
    // Notification endpoints
    'notifications': 'api/notifications',
    
    // Goals endpoints
    'goals': 'api/goals',
    'progress': 'api/progress'
};

export default Apis;
