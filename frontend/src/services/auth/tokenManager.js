import AUTH_CONSTANTS from "../../constants/auth";
import { jwtDecode } from 'jwt-decode';

class TokenManager {

    constructor() {
        this.keys = AUTH_CONSTANTS.STORAGE_KEYS;
    }

    // ========== ACCESS TOKEN METHODS ==========

    getAccessToken() {
        return localStorage.getItem(this.keys.ACCESS_TOKEN);
    }

    setAccessToken(token) {
        if (token) {
            localStorage.setItem(this.keys.ACCESS_TOKEN, token);
        }
    }

    removeAccessToken() {
        localStorage.removeItem(this.keys.ACCESS_TOKEN);
    }

    // ========== REFRESH TOKEN METHODS ==========

    getRefreshToken() {
        return localStorage.getItem(this.keys.REFRESH_TOKEN);
    }

    setRefreshToken(token) {
        if (token) {
            localStorage.setItem(this.keys.REFRESH_TOKEN, token);
        }
    }

    removeRefreshToken() {
        localStorage.removeItem(this.keys.REFRESH_TOKEN);
    }

    // ========== USER DATA METHODS ==========

    getUserData() {
        const userData = localStorage.getItem(this.keys.USER_DATA);
        return userData ? JSON.parse(userData) : null;
    }

    setUserData(userData) {
        if (userData) {
            localStorage.setItem(this.keys.USER_DATA, JSON.stringify(userData));
        }
    }

    removeUserData() {
        localStorage.removeItem(this.keys.USER_DATA);
    }

    // ========== OPERATIONS ==========

    /**
     * Save authentication data (tokens and user data) to localStorage
     * @param {object} response - The response object containing tokenResponse and user data 
     */
    saveAuthData(response) {
        if (response.tokenResponse) {
            this.setAccessToken(response.tokenResponse.accessToken);
            this.setRefreshToken(response.tokenResponse.refreshToken);
        }

        if (response.user) {
            this.setUserData(response.user);
        }
    }

    /**
     * Clear all authentication-related data from localStorage, effectively logging the user out
     */
    clearAuthData() {
        this.removeAccessToken();
        this.removeRefreshToken();
        this.removeUserData();
    }

    /**
     * Check if both access and refresh tokens are present in localStorage
     * @returns {boolean} True if both tokens are present, false otherwise
     */
    hasTokens() {
        return !!(this.getAccessToken() && this.getRefreshToken());
    }

    /**
     * Check if the user is authenticated by verifying the presence of tokens and user data in localStorage
     * @returns {boolean} True if the user is authenticated, false otherwise
     */
    isAuthenticated() {
        return this.hasTokens() && !!this.getUserData();
    }

    /**
     * Decode a JWT token to extract its payload (e.g., expiration time)
     * @param {string} token - The JWT token to decode
     * @returns {object|null} The decoded token payload, or null if decoding fails
     */
    decodeToken(token) {
        try {
            return jwtDecode(token);
        } catch (error) {
            console.error('Error decoding token:', error);
            return null;
        }
    }

    /**
     * Check if the given token is expiring soon (within the defined threshold)
     * @param {string} token - The JWT token to check
     * @returns {boolean} True if the token is expiring soon, false otherwise
     */
    isTokenExpiringSoon(token) {
        const decoded = this.decodeToken(token);
        if (!decoded || !decoded.exp) return true;

        const expirationTime = decoded.exp * 1000; // Convert to milliseconds
        const currentTime = Date.now();

        return (expirationTime - currentTime) < AUTH_CONSTANTS.REFRESH_THRESHOLD;
    }

    /**
     * Check if the given token is expired
     * @param {string} token - The JWT token to check
     * @returns {boolean} True if the token is expired, false otherwise
     */
    isTokenExpired(token) {
        const decoded = this.decodeToken(token);
        if (!decoded || !decoded.exp) return true;

        const expirationTime = decoded.exp * 1000; // Convert to milliseconds
        return Date.now() > expirationTime;
    }

}

const tokenManager = new TokenManager();   
export default tokenManager;