import { useState, useCallback, useEffect, useRef } from 'react';
import tokenManager from '../../services/auth/tokenManager';
import authService from '../../services/auth/AuthService';

export default function useAuth() {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isInitialized, setIsInitialized] = useState(false);

    const isRefreshing = useRef(false);

    /**
     * Initialization
     */
    useEffect(() => {
        const initializeAuth = () => {
            try {
                const token = tokenManager.getAccessToken();
                const userData = tokenManager.getUserData();

                console.log('Initializing authentication - token:', token);
                console.log('Initializing authentication - userData:', userData);

                if (token && userData) {
                    setUser(userData);
                    setIsAuthenticated(true);
                } else {
                    setUser(null);
                    setIsAuthenticated(false);
                }

            } catch (err) {
                console.error('Error initializing authentication:', err);
                setUser(null);
                setIsAuthenticated(false);
            
            } finally {
                setLoading(false);
                setIsInitialized(true);
            }
        }
        initializeAuth();
    }, []);

    useEffect(() => {
        if (!isAuthenticated) return;

        const checkTokenValidity = async () => {
            const accessToken = tokenManager.getAccessToken();

            if (!accessToken || isRefreshing.current) return;

            if (tokenManager.isTokenExpiringSoon(accessToken)) {

                isRefreshing.current = true;
                
                const result = await authService.refreshToken();

                if (!result.success) {
                    console.error('Token refresh failed:', result.error);
                    await handleLogout();
                } else {
                    console.log('Token refreshed successfully');
                }

                isRefreshing.current = false;
            
                }
        };

        const interval = setInterval(checkTokenValidity, 60 * 1000); // Check every minute

        checkTokenValidity(); // Initial check on mount

        return () => clearInterval(interval);
    }, [isAuthenticated]);

    /**
     * LOGIN
     */
    const login = useCallback(async (credentials) => {
        setLoading(true);
        setError(null);

        try {
            const result = await authService.login(credentials);

            console.log('Login result:', result);

            // Handle successful login
            if (result.success) {
                setUser(result.data.user);
                setIsAuthenticated(true);
                return { success: true };

            // Handle login failure
            } else {                
                setError(result.error);
                return { success: false };
            }

        } catch (err) {
            console.error('Login error:', err);
            setError(err);
            return { success: false };
        
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * REGISTER
     */
    const register = useCallback(async (userData) => {
        setLoading(true);
        setError(null);

        // Prepare user data for registration
        const userDataToRegister = {
            name: userData.name,
            surname: userData.surname,
            email: userData.email,
            username: userData.username,
            password: userData.password
        }

        try {
            const result = await authService.register(userDataToRegister);

            // Handle successful registration
            if (result.success) {
                setUser(result.data.user);
                setIsAuthenticated(true);
                return { success: true };
            
            // Handle registration failure
            } else {
                setError(result.error);
                return { success: false };
            }

        } catch (err) {
            console.error('Registration error:', err);
            setError(err);
            return { success: false };

        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * LOGOUT
     */
    const logout = useCallback(async () => {
        return await handleLogout();
    }, []);

    const handleLogout = async () => {
        setLoading(true);
        setError(null);

        try {
            const result = await authService.logout();

            clearAuthStates();

            return { success: true };

        } catch (err) {
            console.error('Logout error:', err);
            clearAuthStates();
            return { success: true };

        } finally {
            setLoading(false);
        }
    };

    /**
     * Clear all authentication-related states (user, isAuthenticated, error) 
     * used after logout or when no valid token is found
     */
    const clearAuthStates = useCallback(() => {
        setUser(null);
        setIsAuthenticated(false);
        setError(null);
    }, []);

    const clearError = useCallback(() => {
        setError(null);
    }, []);

    return {
        user,
        isAuthenticated,
        loading,
        isInitialized,
        error,
        login,
        register,
        logout,
        clearError
    };

}