import { useState } from "react";
import SidebarFooter from "./SidebarFooter";
import SidebarHeader from "./SidebarHeader";
import useSidebarContext, { SidebarContext, SidebarProvider } from "../../../context/SideBarContext";

export default function Sidebar({ children}) {
    const { isCollapsed } = useSidebarContext();

    return (
    <aside className={`h-100 d-flex bg-primary-500 shadow-lg`}>
        <nav className="nav flex-column flex-grow-1 aa-txt-text">
            
            <div className="mb-4 p-3 bg-primary-700 rounded-bottom border-bottom shadow-lg">
                <SidebarHeader />
            </div>
            
            <ul className="flex-grow-1 list-unstyled mx-2 rounded-3 bg-primary-400">
                {children}
            </ul>
            
            <div className="text-center rounded-top bg-primary-500 px-3 mt-1">
                <SidebarFooter />
            </div>  
        </nav>
    </aside>            
    );
}