import AuthTabs from "../auth/common/AuthTabs";

export default function AuthLayout({ authType, children }) {

    return (
        <div className=" d-flex flex-column align-items-center justify-content-center mb-2">
        
            {/* Selection Tabs (Login/Register) */}
            <AuthTabs activeTab={authType} />
            
            {/* Content Container */}
            <div className="w-75 d-flex align-items-stretch justify-content-between gap-2 rounded-bottom-3 shadow-lg aa-bg-primary">
                
                {/* Render the children components (Login/Register Content) */}
                {children}
            
            </div>
        
        </div>
    );
}