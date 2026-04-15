import { Navigate, Outlet, useLocation, matchPath } from "react-router-dom";
import useAuthContext from "../context/AuthContext";
import { ROUTES } from "../constants/routes";
import MainLayout from "../components/layout/MainLayout";

const PAGES_TITLES = {
    [ROUTES.HOME]: "pages.home",
    [ROUTES.PROFILE]: "pages.profile",
    [ROUTES.SETTINGS]: "pages.settings",
    [ROUTES.RECIPES]: "pages.recipes",
    [ROUTES.MY_RECIPES]: "pages.myRecipes",
    [ROUTES.RECIPE_DETAIL]: "pages.recipeDetail",
    [ROUTES.RECIPE_CREATE]: "pages.createRecipe",
    [ROUTES.RECIPE_EDIT]: "pages.editRecipe",
}

function getPageTittle(pathname) {
    for (const [pattern, title] of Object.entries(PAGES_TITLES)) {
        if (matchPath(pattern, pathname)) {
            return title;
        }
    }
    return "pages.default";
}

export default function ProtectedRoute() {
    const location = useLocation();
    const { isAuthenticated, isInitialized, loading } = useAuthContext();

    console.log("Location:", location.pathname);
    const page = getPageTittle(location.pathname);

    if (loading || !isInitialized) {
        return (
            <div className="d-flex justify-content-center align-items-center min-vh-100">
                <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando...</span>
                </div>
            </div>
        );
    }

    if (!isAuthenticated) {
        return <Navigate to={ROUTES.LOGIN} />;
    }

    return (
    <MainLayout page={page}>
        <Outlet />
    </MainLayout>
    );

}