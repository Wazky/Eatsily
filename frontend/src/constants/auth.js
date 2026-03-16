
export const AUTH_CONSTANTS = {

    STORAGE_KEYS: {
        ACCESS_TOKEN: 'accessToken',
        REFRESH_TOKEN: 'refreshToken',
        USER_DATA: 'userData'
    },

    TOKEN_EXPIRATION: {
        ACCESS_TOKEN: 15 * 60 * 1000, // 15 minutes
        REFRESH_TOKEN: 2 * 24 * 60 * 60 * 1000 // 2 days
    },

    REFRESH_THRESHOLD: 5 * 60 * 1000, // 5 minutes before expiration

    AUTH_ERROR_CODES: [
        'AUTH_006', // Invalid token
        'AUTH_007',  // Expired token
        'AUTH_008' // Unauthorized access
    ]

}

export default AUTH_CONSTANTS;