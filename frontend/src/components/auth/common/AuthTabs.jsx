import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { ROUTES } from "../../../constants/routes";

export default function AuthTabs({ activeTab = "login" }) {  
    
    const { t } = useTranslation("auth");

    let baseLinkClass = " text-center text-decoration-none fw-bold py-2 rounded-top-3 ";
    let activeLinkClass = "col-md-8 aa-bg-primary ";
    let inactiveLinkClass = "col-md-4 bg-text-400 aa-txt-background is-hoverable";

    return (
    <nav className="w-75 nav justify-content-between rounded-top-3 mt-3">
        
        {/* Register Tab */}
        <Link 
            to={ROUTES.REGISTER} 
            className={(activeTab === "register" ? activeLinkClass : inactiveLinkClass) + baseLinkClass}
        >
            {t("register.register").toUpperCase()}
        </Link>

        {/* Login Tab */}
        <Link 
            to={ROUTES.LOGIN} 
            className={(activeTab === "login" ? activeLinkClass : inactiveLinkClass) + baseLinkClass}
        >
            {t("login.login").toUpperCase()}
        </Link>
    
    </nav>
    );
}