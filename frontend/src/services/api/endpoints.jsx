export const ENDPOINTS = {
    AUTH: {
        LOGIN: '/auth/login',
        LOGOUT: '/auth/logout',
        REGISTER: '/auth/register',
        REFRESH: '/auth/refresh',
    },
    USERS: {
        BASE: '/users',
        BY_ID: (userId) => `/users/${userId}`,
        PROFILE: '/users/profile',
    }
}