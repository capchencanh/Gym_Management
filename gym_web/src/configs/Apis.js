import axios from 'axios';

const BASE_URL = 'http://localhost:8080/';

const Apis = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
});


Apis.interceptors.request.use(
    (config) => {
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
            window.location.href = '/login';
        } else if (error.response?.status === 403) {
        } else if (error.response?.status >= 500) {
        } else if (error.code === 'NETWORK_ERROR') {
        }
        
        if (error.response?.data) {
            const { success, message, error: errorMessage, data } = error.response.data;
            error.transformedData = {
                success: success || false,
                message: message || 'Có lỗi xảy ra',
                error: errorMessage || error.message,
                data: data || null
            };
        }
        
        return Promise.reject(error);
    }
);

export const endpoints = {
    // Auth 
    'login': 'api/v1/auth/login',
    'register': 'api/v1/auth/register',
    'logout': 'api/v1/auth/logout',
    
    // User 
    'users': 'api/users',
    'profile': 'api/users/profile',
    'active-memberships': 'api/users/memberships/active',
    'update-profile': 'api/users/profile',
    'update-avatar': 'api/users/avatar',
    'change-password': 'api/users/change-password',
    
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
    'pt-sessions-by-user-trainer': 'api/pt/training/user',
    
    // Packages
    'packages': 'api/packages',
    'package-detail': 'api/packages',
    
    // Payment
    'create-payment': 'api/payment/create-payment',
    'payment-callback': 'api/payment/payment-callback',
    'check-payment-status': 'api/payment/check-status',

    // AI Chat
    'ai-chat': 'api/ai/chat'
};

export default Apis;
