import { createContext, useContext, useState } from "react";

export const SidebarContext = createContext(null);

export const SidebarProvider = ({ children }) => {
    
    const [isCollapsed, setIsCollapsed] = useState(true);

    return (
        <SidebarContext value={{ isCollapsed, setIsCollapsed }} >
            {children}
        </SidebarContext>
    );
}

export default function useSidebarContext() {
    const context = useContext(SidebarContext);

    if (!context) {
        throw new Error("useSidebarContext must be used within a SidebarProvider");
    }

    return context;
}