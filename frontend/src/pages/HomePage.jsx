import Sidebar from "../components/common/sidebar/Sidebar";
import SidearItem from "../components/common/sidebar/SidebarItem";
import useAuthContext from "../context/AuthContext";

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

    return (
        <div className="d-flex align-items-start justify-content-start shadow-lg">
            <Sidebar>
                <SidearItem icon="bi-house" text="Home" href="/" active />
                <SidearItem icon="bi-person" text="Profile" href="/profile" />
                <SidearItem icon="bi-gear" text="Settings" href="/settings" />
            </Sidebar>

            <div className="flex-grow-1 vh-100 bg-background-500 rounded-3 shadow-lg">

                <div className="d-flex justify-content-center aa-bg-secondary mb-4 p-3 shadow-lg"> 
                    <header className="text-white">Dashboard</header>
                </div>

                <div>
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

                    <button className="btn btn-secondary mt-3" onClick={handleLogout}>Logout</button>

                </div>

            </div>
        </div>
    );
}