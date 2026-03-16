import MainLayout from "../components/layout/MainLayout";
import useUser from "../hooks/useUser";
import { formatDate } from "../utils/helpers";

export default function ProfilePage() {
    const { user, loading, error } = useUser();

    if (loading) {
        return (
        <div className="d-flex justify-content-center align-items-center min-vh-100">
            <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Loading...</span>
            </div>
        </div>
        );
    }

    return (

    <div className="container-fluid py-4">
        <div className="row g-4">
            
            {/* Columna izquierda - Account Information */}
            <div className="col-12 col-md-8">
                <div className="bg-accent-500 aa-txt-text rounded-3 shadow-lg p-4">
                    <h2 className="fs-2 border-bottom pb-2 fw-bold mb-4">
                        <i className="bi bi-person-circle me-2"></i>
                        Account Information
                    </h2>
                    
                    <div className="d-flex flex-column gap-3">
                        <div className="d-flex flex-column flex-sm-row gap-2">
                            <span className="fw-bold" style={{ minWidth: '120px' }}>USERNAME:</span>
                            <span>{user?.username || "Not available"}</span>
                        </div>
                        
                        <div className="d-flex flex-column flex-sm-row gap-2">
                            <span className="fw-bold" style={{ minWidth: '120px' }}>EMAIL:</span>
                            <span className="text-break">{user?.email || "Not available"}</span>
                        </div>
                        
                        <div className="d-flex flex-column flex-sm-row gap-2">
                            <span className="fw-bold" style={{ minWidth: '120px' }}>CREATED:</span>
                            <span>{formatDate(user?.creationDate) || "Not available"}</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Columna derecha - Personal Information */}
            <div className="col-12 col-md-4">
                <div className="bg-secondary-500 aa-txt-text rounded-3 shadow-lg p-4">
                    <h2 className="fs-2 border-bottom pb-2 fw-bold mb-4">
                        <i className="bi bi-person-circle me-2"></i>
                        Personal Information
                    </h2>
                    
                    <div className="d-flex flex-column gap-3">
                        <div className="d-flex flex-column flex-sm-row gap-2">
                            <span className="fw-bold" style={{ minWidth: '100px' }}>NAME:</span>
                            <span>{user?.name || "Not available"}</span>
                        </div>
                        
                        <div className="d-flex flex-column flex-sm-row gap-2">
                            <span className="fw-bold" style={{ minWidth: '100px' }}>SURNAME:</span>
                            <span>{user?.surname || "Not available"}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    );
}