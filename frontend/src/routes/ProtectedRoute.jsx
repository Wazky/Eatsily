import { Navigate, Outlet, useLocation } from "react-router-dom";
import useAuthContext from "../context/AuthContext";
import { ROUTES } from "../constants/routes";
import MainLayout from "../components/layout/MainLayout";

const PAGES_TITLES = {
    [ROUTES.HOME]: "pages.home",
    [ROUTES.PROFILE]: "pages.profile",
    [ROUTES.SETTINGS]: "pages.settings",
}

export default function ProtectedRoute() {
    const location = useLocation();
    const { isAuthenticated, isInitialized, loading } = useAuthContext();

    const page = PAGES_TITLES[location.pathname] || "pages.default";

    if (loading || !isInitialized) {
        return (
            <div className="d-flex justify-content-center align-items-center min-vh-100">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando...</span>
                </div>
            </div>
        );
    }

    console.log("ProtectedRoute - isAuthenticated:", isAuthenticated);

    if (!isAuthenticated) {
        return <Navigate to={ROUTES.LOGIN} />;
    }

    return (
    <MainLayout page={page}>
        <Outlet />
    </MainLayout>
    );

}