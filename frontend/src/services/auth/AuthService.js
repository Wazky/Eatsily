import $, { url } from '../api/ApiConfig';
import { ENDPOINTS } from '../api/endpoints';
import tokenManager from './tokenManager';

class AuthService {

    /**
     * LOGIN
     */
    async login(credentials) {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.AUTH.LOGIN),
                method: 'POST',
                data: JSON.stringify(credentials),
            });

            // Save tokens and user data
            tokenManager.saveAuthData(response);

            return {
                success: true,
                data: {
                    user: response.user,
                    message: response.message
                }
            };

        } catch (error) {
            return {
                success: false,
                error: error.responseJSON 
            };
        }
    }

    /**
     * REGISTER
     */
    async register(userData) {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.AUTH.REGISTER),
                method: 'POST',
                data: JSON.stringify(userData)
            });            

            // Save tokens and user data
            tokenManager.saveAuthData(response);

            return {
                success: true,
                data: {
                    user: response.user,
                    message: response.message
                }
            }

        } catch (error) {
            return {
                success: false,
                error: error.responseJSON
            }
        }
    }

    async refreshToken() {
        try {
            const refreshToken = tokenManager.getRefreshToken();

            if (!refreshToken) {
                throw new Error('No refresh token available');
            }

            const response = await $.ajax({
                url: url(ENDPOINTS.AUTH.REFRESH),
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${refreshToken}`
                }
            });

            // Update tokens in localStorage
            if (response.accessToken) {
                tokenManager.setAccessToken(response.accessToken);

                if (response.refreshToken) {
                    tokenManager.setRefreshToken(response.refreshToken);
                }
            }

            return {
                success: true,
                data: response
            };

        } catch (error) {
            console.error('Refresh token error:', error);

            // Clear local authentication data on refresh failure
            tokenManager.clearAuthData();

            return {
                success: false,
                error: error.responseJSON || { message: 'Token refresh failed' }
            }
        }
    }

    /**
     * LOGOUT
     */
    async logout() {
        try {
            const accessToken = tokenManager.getAccessToken();

            if (!accessToken) {
                console.warn('No access token found, cannot perform logout API call');
                tokenManager.clearAuthData();

                return {
                    success: true,
                    message: 'Logged out locally'
                };
            }

            // Make API call to logout endpoint
            const response = await $.ajax({
                url: url(ENDPOINTS.AUTH.LOGOUT),
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                },
                dataType: 'text'  
            })

            // Clear local authentication data after successful logout
            tokenManager.clearAuthData();

            return {
                success: true,
                message: 'Logged out successfully'
            }

        } catch (error) {
            console.error('Logout error Service:', error);

            // Clear local authentication data even if API call fails
            tokenManager.clearAuthData();
            return {
                success: false,
                error: error.responseJSON || { message: 'Logout failed' }
            }
        }
    }

    isAuthenticated() {
        return tokenManager.isAuthenticated();
    }

    getUserData() {
        return tokenManager.getUserData();
    }

    getAccessToken() {
        return tokenManager.getAccessToken();
    }
}

const authService = new AuthService();
export default authService;