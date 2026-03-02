import { Link } from "react-router-dom";
import useSidebarContext from "../../../context/SideBarContext";

export default function SidearItem({ icon, text, href, active, alert }) {

    const { isCollapsed } = useSidebarContext();

    return (
    <li className={`rounded-2 p-1 m-2 is-hoverable ${active ? "bg-secondary-500" : ""}`} >
        <Link 
            className="nav-link" 
            to={href}
            title={text}>

            <div className={`fs-4 ${isCollapsed ? "text-center" : "text-start"} text-white fw-bold`}>
                <i className={`bi ${icon} `}></i>
                {!isCollapsed && <span className="overflow-hidden ms-2">{text}</span>}
            </div>

            {alert && (
                <span className="badge bg-danger rounded-circle position-absolute top-0 start-100 translate-middle">
                    {alert}
                </span>
            )}

        </Link>
    </li>
    );
}