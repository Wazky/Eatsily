import $, { url } from './api/ApiConfig';
import { ENDPOINTS } from './api/endpoints';


class AuthService {

    constructor() {
        this.ACCESS_TOKEN_KEY = 'accessToken';
        this.REFRESH_TOKEN_KEY = 'refreshToken';
        this.USER_KEY = 'userData';
    }

    /**
     * LOGIN
     * @param {object} credentials - The login credentials { username, password }
     * @returns {object} - An object containing success status and either user data or error information
     */
    async login(credentials) {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.AUTH.LOGIN),
                method: 'POST',
                data: JSON.stringify(credentials),
            });

            // Save authentication data (tokens and user info)
            this.saveAuthData(response);

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
     * @param {object} userData - The user data to register
     * @returns {object} - An object containing success status and either user data or error information
     */
    async register(userData) {
        try {
            const response = await $.ajax({
                url: url(ENDPOINTS.AUTH.REGISTER),
                method: 'POST',
                data: JSON.stringify(userData)
            });            

            // Save authentication data (tokens and user info)
            this.saveAuthData(response);

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
        // Implement token refresh logic here
    }

    /**
     * LOGOUT
     * @returns {object} - An object containing success status and either a message or error information
     */
    async logout() {
        try {
            const accessToken = this.getAccessToken();

            // If no access token is found, clear local auth data
            if (!accessToken) {
                console.warn('No access token found, cannot perform logout API call');

                this.clearAuthData();
                return {
                    success: true,
                    message: 'Logged out locally, but no API call was made due to missing access token'
                }
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

            this.clearAuthData();
            return {
                success: true,
                message: 'Logged out successfully'
            }

        } catch (error) {
            console.error('Logout error Service:', error);

            // Clear local authentication data even if API call fails
            this.clearAuthData();
            return {
                success: false,
                error: error.responseJSON || { message: 'Logout failed' }
            }
        }
    }

    /**
     * Save authentication data (tokens and user info) to localStorage 
     * after successful login or registration
     * @param {object} response - The response object containing tokens and user data
     */
    saveAuthData(response) {
        
        // Store tokens
        if (response.tokenResponse && response.tokenResponse.tokenType === 'Bearer') {
            this.setAccessToken(response.tokenResponse.accessToken);
            this.setRefreshToken(response.tokenResponse.refreshToken);
        }

        // Store user data
        if (response.user) {
            this.setUserData(response.user);
        }

    }

    /**
     * Clear all authentication-related data from localStorage, effectively logging the user out
     */
    clearAuthData() {
        localStorage.removeItem(this.ACCESS_TOKEN_KEY);
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
    }    

    /**
     * Get the current access token from localStorage
     * @returns {string|null} The access token, or null if not found
     */
    getAccessToken() {
        return localStorage.getItem(this.ACCESS_TOKEN_KEY);
    }

    /**
     * Store the access token in localStorage
     * @param {string} token - The access token to store
     */
    setAccessToken(token) {
        localStorage.setItem(this.ACCESS_TOKEN_KEY, token);
    }

    /**
     * Get the current refresh token from localStorage
     * @returns {string|null} The refresh token, or null if not found
     */
    getRefreshToken() {
        return localStorage.getItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Store the refresh token in localStorage
     * @param {string} token - The refresh token to store
     */
    setRefreshToken(token) {
        localStorage.setItem(this.REFRESH_TOKEN_KEY, token);
    }

    /**
     * Get the current user data from localStorage
     * @returns {object|null} The user data object, or null if not found
     */
    getUserData() {
        const userData = localStorage.getItem(this.USER_KEY);
        return userData ? JSON.parse(userData) : null;
    }

    /**
     * Store the user data object in localStorage
     * @param {object} userData - The user data object to store
     */
    setUserData(userData) {
        localStorage.setItem(this.USER_KEY, JSON.stringify(userData));
    }

    /**
     * Check if the user is currently authenticated by verifying the presence of a valid access token
     * @returns {boolean} True if the user is authenticated, false otherwise
     */
    isAuthenticated() {
        const accessToken = this.getAccessToken();
        return !!accessToken;
    }

}


const authService = new AuthService();
export default authService;