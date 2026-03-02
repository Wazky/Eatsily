import { useState } from "react";
import SidebarFooter from "./SidebarFooter";
import SidebarHeader from "./SidebarHeader";
import { SidebarContext, SidebarProvider } from "../../../context/SideBarContext";

export default function Sidebar({ children}) {

    return (
        <SidebarProvider>
            <aside className="vh-100 d-flex bg-primary-500 border-end border-light shadow-lg">
                <nav className="nav flex-column flex-grow-1  aa-txt-text">
                    
                    <div className="mb-4 p-3">
                        <SidebarHeader />
                    </div>
                    
                    <ul className="flex-grow-1 list-unstyled mx-2 rounded-3 bg-primary-400">
                        {children}
                    </ul>
                
                <div className="p-3 text-center">
                    <SidebarFooter />
</div>  
                </nav>
            </aside>            
        </SidebarProvider>
    );
}