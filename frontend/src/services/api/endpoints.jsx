export const ENDPOINTS = {
    AUTH: {
        LOGIN: '/auth/login',
        LOGOUT: '/auth/logout',
        REGISTER: '/auth/register',
        REFRESH: '/auth/refresh'
    },
    USERS: {
        BASE: '/users',
        BY_ID: (userId) => `/users/${userId}`,
        PROFILE: '/users/profile'
    },
    RECIPES: {
        BASE: '/recipes',
        BY_ID: (recipeId) => `/recipes/${recipeId}`,
        MY_RECIPES: '/recipes/my',
        VISIBILITY: (recipeId) => `/recipes/${recipeId}/visibility`
    },
    CATALOG: {
        UNITS: '/catalog/units',
        INGREDIENTS: '/catalog/ingredients',
        CATEGORIES: '/catalog/categories'
    }
}