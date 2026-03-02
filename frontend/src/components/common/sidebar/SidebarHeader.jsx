import { Link } from "react-router-dom";
import { ROUTES } from "../../../constants/routes";
import useSidebarContext from "../../../context/SideBarContext";

export default function SidebarHeader() {

    const { isCollapsed, setIsCollapsed } = useSidebarContext();
    const headerTitle = "EATSILY";

    return (    
    <div className={`d-flex justify-content-${isCollapsed ? "center" : "start"} align-items-center position-relative`}>
        
            <Link to={ROUTES.HOME} className="d-flex align-items-center text-decoration-none py-0">
            
            {/*
            <img src="/src/assets/logo.png" alt="Logo" className="img-fluid" />
            */}
            <i className="bi bi-fork-knife fs-1 hover-bright-icon"></i>
            {!isCollapsed && (
            <h2 className="ms-2 fw-bold mb-0 hover-bright-text">
                {headerTitle}
            </h2>
            )}
        </Link>

        
        <button 
            className="btn overflow-x-visible position-absolute translate-middle-x bg-text-important fs-4 rounded-circle hover-bright-text" 
            style={{ right: '-65px' }}
            onClick={() => setIsCollapsed(!isCollapsed)}>
            
            <i className={`bi bi-list txt-secondary-important`}></i>
        </button>
        
    </div>
    );
}