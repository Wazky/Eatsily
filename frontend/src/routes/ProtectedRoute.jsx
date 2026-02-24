import { Navigate, Outlet } from "react-router-dom";
import useAuthContext from "../context/AuthContext";
import { ROUTES } from "../constants/routes";

export default function ProtectedRoute() {
    const { isAuthenticated, isInitialized, loading } = useAuthContext();

    console.log('ProtectedRoute - isAuthenticated:', isAuthenticated);
    console.log('ProtectedRoute - isInitialized:', isInitialized);
    console.log('ProtectedRoute - loading:', loading);

    if (loading || !isInitialized) {
        return (
            <div className="d-flex justify-content-center align-items-center min-vh-100">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando...</span>
                </div>
            </div>
        );
    }

    return isAuthenticated ? <Outlet /> : <Navigate to={ROUTES.LOGIN} />;
}