import { useState, useCallback, useEffect } from "react";
import UserService from "../services/UserService";


export default function useUser() {

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const loadProfile = useCallback(async () => {
        setLoading(true);
        setError(null);

        try {
            const userData = await UserService.getProfile();

            if (userData.success) {
                setUser(userData.data);
                return userData.data;
            } else{
                throw new Error(userData.error?.message || 'Failed to load user profile');
            }

        } catch (err) {
            setError(err.message);
            throw err;
        
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadProfile();
    }, [loadProfile]);

    return {
        user,
        loading,
        error,
        loadProfile
    };
}