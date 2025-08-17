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
    'progress': 'api/progress',
    
    // PT Request
    'pt-request': 'api/user/pt-request/request',
    'pt-availability': 'api/user/pt-request/availability',
    'pt-status': 'api/user/pt-request/status',
    
    // PT Training
    'pt-sessions': 'api/pt/training/session',
    'pt-sessions-by-trainer': 'api/pt/training/trainer',
    'pt-sessions-by-date': 'api/pt/training/trainer/date',
    'pt-sessions-by-user-trainer': 'api/pt/training/user'
};

export default Apis;
