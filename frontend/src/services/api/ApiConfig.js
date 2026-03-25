import $ from 'jquery';
import { ENDPOINTS } from './endpoints';
import tokenManager from '../auth/tokenManager';
import authService from '../auth/AuthService';
import { ROUTES } from '../../constants/routes';

const BACKEND_URL = '/Eatsily/rest';

let isRefreshingToken = false;
let refreshPromise = null;

const isAuthEndpoint = (url) => {
    return url.includes(ENDPOINTS.AUTH.LOGIN) ||
        url.includes(ENDPOINTS.AUTH.REGISTER) ||
        url.includes(ENDPOINTS.AUTH.REFRESH);
}

$.ajaxSetup({
    contentType: 'application/json',
    dataType: 'json',
    
    // Automatically include Authorization header with access token for all requests
    beforeSend: function(xhr, settings){
        // Skip adding Authorization header for login and register endpoints
        if (isAuthEndpoint(settings.url)) {
            return;
        }

        const token = tokenManager.getAccessToken();

        if (token) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        }
    },

    error: function(xhr, status, error) {
        console.error('AJAX Error:', {
            status: xhr.status,
            statusText: xhr.statusText,
            error: error
        });

        // Skip token refresh for auth-related endpoints
        if (isAuthEndpoint(this.url)) return;

        if (xhr.status === 401 && !isRefreshingToken) {
            handleTokenRefresh();
        }
    }

});

async function handleTokenRefresh() {
    if (!refreshPromise) {
        isRefreshingToken = true;
        refreshPromise = authService.refreshToken();
    }

    try {
        const result = await refreshPromise;

        if (result.success) {
            console.log('Token refreshed successfully');
        } else {
            console.error('Token refresh failed:', result.error);
            tokenManager.clearAuthData();
            window.location.href = ROUTES.LOGIN;
        }

    } catch (err) {
        console.error('Error during token refresh:', err);
        tokenManager.clearAuthData();
        window.location.href = ROUTES.LOGIN;

    } finally {
        isRefreshingToken = false;
        refreshPromise = null;
    }
}

// Utility function to construct full API URLs
export const url = (endpoint) => `${BACKEND_URL}${endpoint}`;

export default $;