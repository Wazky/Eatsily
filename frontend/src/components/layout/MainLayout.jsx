import Sidebar from "../common/sidebar/Sidebar";
import SidearItem from "../common/sidebar/SidebarItem";
import Header from "../common/header/Header";

import { useLocation } from "react-router-dom";
import { ROUTES } from "../../constants/routes";
import { SidebarProvider } from "../../context/SideBarContext";

export default function MainLayout({ page, children }) {
    const location = useLocation();

    return (
    <SidebarProvider>
    <div className="d-flex vh-100 w-100 overflow-hidden align-items-start justify-content-start">
        
        {/* Sidebar Component */}
        <Sidebar>
            <SidearItem icon="bi-house" text="pages.home" href={ROUTES.HOME} active={location.pathname === ROUTES.HOME} />
            <SidearItem icon="bi-person" text="pages.profile" href={ROUTES.PROFILE} active={location.pathname === ROUTES.PROFILE} />
            <SidearItem icon="bi-gear" text="pages.settings" href={ROUTES.SETTINGS} active={location.pathname === ROUTES.SETTINGS} />
        </Sidebar>

        {/* Main Content Area */}
        <main className="main-content flex-grow-1 h-100 overflow-auto bg-background-600 rounded-3 shadow-lg"
        >

            {/* Page Header */}
            <Header title={page}/>

            {/* Page Content */}
            {children}

        </main>
        
    </div>        
    </SidebarProvider>
    );
}