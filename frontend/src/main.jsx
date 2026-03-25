import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
// CSS
import './styles/index.css';
// Bootstrap (CSS, ICONS and JS)
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
// i18n
import './utils/i18n.js';

import { AuthProvider } from './context/AuthContext.jsx'
import { ROUTES } from './constants/routes.js'
import ProtectedRoute from './routes/ProtectedRoute.jsx'
import LoginPage from './pages/auth/LoginPage.jsx'
import RegisterPage from './pages/auth/RegisterPage.jsx'
import ErrorPage from './pages/ErrorPage.jsx'
import HomePage from './pages/HomePage.jsx';
import ProfilePage from './pages/ProfilePage.jsx';
import SettingsPage from './pages/SettingsPage.jsx';
import RecipeListPage from './pages/recipes/RecipeListPage.jsx';
import MyRecipesPage from './pages/recipes/MyRecipesPage.jsx';
import RecipeCreatePage from './pages/recipes/RecipeCreatePage.jsx';

const router = createBrowserRouter([
  {
    path: ROUTES.LOGIN,
    element: <LoginPage />,
    errorElement: <ErrorPage />
  },
  {
    path: ROUTES.REGISTER,
    element: <RegisterPage />,
    errorElement: <ErrorPage />
  },
  {
    path: ROUTES.HOME,
    element: <ProtectedRoute />,
    children: [
      { path: ROUTES.HOME, element: <HomePage /> },
      { path: ROUTES.PROFILE, element: <ProfilePage /> },
      { path: ROUTES.SETTINGS, element: <SettingsPage /> },

      // Recipes
      { path: ROUTES.RECIPES , element: <RecipeListPage />},
      { path: ROUTES.MY_RECIPES , element: <MyRecipesPage />},
      { path: ROUTES.RECIPE_DETAIL , element: <></>},
      { path: ROUTES.RECIPE_CREATE , element: <RecipeCreatePage />},
      { path: ROUTES.RECIPE_EDIT , element: <></>},
    ],
  },
])

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  </StrictMode>,
)
