import { useContext, createContext, useState, useEffect } from "react";

// Create the AuthContext
const AuthContext = createContext(null);

export function AuthProvider({children}) {

    const [isAuthenticated, setIsAuthenticated] = useState(false);
    
    return (
        <AuthContext.Provider value={{ isAuthenticated }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);