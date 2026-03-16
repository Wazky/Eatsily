import MainLayout from "../components/layout/MainLayout";
import useAuthContext from "../context/AuthContext";
import authService from "../services/auth/AuthService";

export default function HomePage() {
    
    const { user, loading, error, logout } = useAuthContext();

    const handleLogout = async () => {
        const response = await logout();

        if (response.success) {
            alert("Logged out successfully!");
        } else {
            alert("Logout failed: " + response.error);
        }

    }

    const handleRefresh = async () => {
        const response = await authService.refreshToken();
        if (response.success) {
            alert("Token refreshed successfully!");
        }
            else {
            alert("Token refresh failed: " + response.error);
            }
    }

    return (
        <div className="bg-secondary-400 m-3 p-3 rounded-3 shadow-lg">
            <h1>Welcome to your dashboard!</h1>

            {loading && <p>Loading user data...</p>}

            {error && <p className="text-danger">Error loading user data: {error.message}</p>}

            {user && (
                <div>
                    <p><strong>Id:</strong> {user.id}</p>
                    <p><strong>Username:</strong> {user.username}</p>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Role:</strong> {user.role}</p>
                </div>
            )}

            <div className="d-flex gap-2">
                <button className="aa-bg-primary rounded-2 border-light mt-3" onClick={handleLogout}>Logout</button>
                <button className="aa-bg-accent rounded-2 border-light mt-3" onClick={handleRefresh}>Refresh Token</button>
            </div>
        </div>
    );
}