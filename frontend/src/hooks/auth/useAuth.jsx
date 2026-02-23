import { useState, useCallback, useEffect } from 'react';
import authService from '../../services/AuthService';

export default function useAuth() {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [isInitialized, setIsInitialized] = useState(false);

    useEffect(() => {
        const initializeAuth = () => {
            try {
                const token = authService.getAccessToken();
                const userData = authService.getUserData();

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
        setLoading(true);
        setError(null);

        try {
            const result = await authService.logout();

            if (result.success) {
                clearAuthStates();
                return { success: true };
            
            } else {
                setError(result.error);
            }

        } catch (err) {
            console.error('Logout error:', err);
            // Clear authentication states anyway
            clearAuthStates();
            return { success: true };

        } finally {
            setLoading(false);
        }
    }, []);

    const ping = useCallback(async () => {
        try {
            const result = await authService.ping();

            if (result.success) {
                console.log('Ping successful:', result.data);
                return { success: true, data: result.data };
            } else {
                console.error('Ping failed:', result.error);
                return { success: false, error: result.error };
            }
        } catch (err) {
            console.error('Ping error:', err);  
            return { success: false, error: err};
        }          

    }, []);

    const debugPing = useCallback(async () => {
        try {
            const result = await authService.debugPing();
            return { success: true, data: result };
        } catch (err) {
            console.error('Debug ping error:', err);
            return { success: false, error: err };
        }
    }, []);

    const debugPingSecured = useCallback(async () => {
        try {
            const result = await authService.debugPingSecured();
            return { success: true, data: result };
        } catch (err) {
            console.error('Debug ping secured error:', err);
            return { success: false, error: err };
        }
    }, []);

    const debugEchoHeader = useCallback(async () => {
        try {
            const result = await authService.debugEchoHeader();
            return { success: true, data: result };
        } catch (err) {
            console.error('Debug echo header error:', err);
            return { success: false, error: err };
        }
    }, []);


    /**
     * Clear all authentication-related states (user, isAuthenticated, error) 
     * - used after logout or when no valid token is found
     */
    const clearAuthStates = useCallback(() => {
        setUser(null);
        setIsAuthenticated(false);
        setError(null);
    })

    return {
        user,
        isAuthenticated,
        loading,
        isInitialized,
        error,
        login,
        register,
        logout,
        ping,
        debugPing,
        debugPingSecured,
        debugEchoHeader
    };

}