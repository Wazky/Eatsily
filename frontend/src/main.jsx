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
import DashboardPage from './pages/DashboardPage.jsx'
import ErrorPage from './pages/ErrorPage.jsx'

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
    path: ROUTES.DASHBOARD,
    element: <ProtectedRoute />,
    children: [
      {
        path: ROUTES.DASHBOARD,
        element: <DashboardPage />
      },
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
