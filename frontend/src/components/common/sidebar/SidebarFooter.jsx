import { Link } from "react-router-dom";
import { ROUTES } from "../../../constants/routes";
import { upperFirst } from "../../../utils/helpers"
import useAuthContext from "../../../context/AuthContext";
import useSidebarContext from "../../../context/SideBarContext";

export default function SidebarFooter() {

    const { isCollapsed } = useSidebarContext();

    const { user, logout } = useAuthContext();
    const initials = (user) ? upperFirst(user.username).slice(0, 2).toUpperCase() : "GU";
    const userName = (user) ? upperFirst(user.username) : "Guest";

        
    return (
    <>

        {isCollapsed && (                
            <button className="btn bg-primary-500 rounded-2 mb-2 hover-bright-icon" onClick={logout}>
                <i className="bi bi-door-open fs-2 aa-txt-text"></i>
            </button>
        )}

        <div className="d-flex flex-column justify-content-between align-items-center aa-txt-text ">
            
            <div className={`d-flex align-items-center gap-3 w-100`}>
                <Link to={ROUTES.PROFILE} className="d-flex align-items-center text-decoration-none py-0 gap-2">
                    
                    <h3 className="rounded-2 aa-txt-text fw-bold px-4 py-3 bg-background-500 is-hoverable">
                        {initials}
                    </h3>
                    
                    {!isCollapsed && (
                        <div className="text-start">
                            <p className="mb-0 fw-bold hover-bright-text">{userName}</p>
                            <p className="mb-0 hover-bright-text">{user?.email || "Email"}</p>
                        </div>                    
                    )}

                </Link>
                
                {!isCollapsed && (                
                    <button className="btn rounded-2 mt-1 hover-bright-icon" onClick={logout}>
                        <i className="bi bi-box-arrow-in-right fs-2"></i>
                    </button>
                )}

            </div>

            {!isCollapsed && (
                <div className="w-100">    
                    <hr className="border-2" />
                    <div className="d-flex flex-row justify-content-end p-1 mt-0">
                        <p className="small">© 2026 EATSILY</p>
                    </div>
                </div>
            )}

        </div>

    </>


    );
}