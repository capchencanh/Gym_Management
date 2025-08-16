import axios from 'axios';

const BASE_URL = 'http://localhost:8080/';

const Apis = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});


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
    // Auth 
    'login': 'api/auth/login',
    'register': 'api/auth/register',
    'logout': 'api/auth/logout',
    
    // User 
    'users': 'api/users',
    'profile': 'api/users/profile',
    'update-profile': 'api/users/profile',
    
    // Categories 
    'categories2': 'api/categories2',
    
    // Workout 
    'workout-logs': 'api/workout-logs',
    'workout-history': 'api/workout-logs/history',
    'schedule': 'api/schedule',
    
    // Diet 
    'diet-plans': 'api/diet-plans',
    'meal-suggestions': 'api/meals/suggestions',
    
    // Class 
    'classes': 'api/classes',
    'enrollments': 'api/enrollments',
    
    // PT Chat 
    'pt-chat': 'api/pt-chat',
    'messages': 'api/messages',
    
    // Notification 
    'notifications': 'api/notifications',
    
    // Goals 
    'goals': 'api/goals',
    'progress': 'api/progress'
};

export default Apis;
