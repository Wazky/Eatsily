import useAuthContext from "../context/AuthContext";

export default function DashboardPage() {
    
    const { user, loading, error, logout, ping, debugPing, debugPingSecured, debugEchoHeader } = useAuthContext();

    return (
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

            <button className="btn btn-secondary mt-3" onClick={logout}>Logout</button>
            <button className="btn btn-primary mt-3" onClick={ping}>Ping</button>

        </div>
    );
}