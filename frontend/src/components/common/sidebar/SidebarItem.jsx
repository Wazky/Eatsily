import { Link } from "react-router-dom";
import useSidebarContext from "../../../context/SideBarContext";
import { upperFirst } from "../../../utils/helpers";
import { useTranslation } from "react-i18next";
import { useState } from "react";

export default function SidearItem({ icon, text, href, active, alert }) {
    const { t } = useTranslation('common');
    const { isCollapsed } = useSidebarContext();

    return (
    <li className={`rounded-2 p-1 m-2 is-hoverable ${active ? "bg-secondary-500" : ""} aa-txt-text`} >

        {/* Link Component for navigation */}
        <Link className="nav-link" to={href}>

            {/* Icon and Text Container */}
            <div className={`fs-4 ${isCollapsed ? "text-center" : "text-start"} fw-bold`}>
                
                <i className={`bi ${icon} `}></i>
                {!isCollapsed && 
                <span className="overflow-hidden ms-2">
                    {upperFirst(t(text))}
                </span>
                }            

            </div>

            {/* Alert indicator  (Complete if needed) */}
            {alert && (<span></span>)}

        </Link>

        {/* Hover text for collapsed state */}
        {isCollapsed && (
            <span className={`sidebar-item-hover-text ${active ? "bg-secondary-500" : "bg-primary-400"}`}>
                {upperFirst(t(text))}
            </span>      
        )}

    </li>
    );
}