import { Link } from "react-router-dom";
import { ROUTES } from "../../../constants/routes";
import LanguageSwitcher from "../../common/LanguageSwitcher";
import { useTranslation } from "react-i18next";

export default function AuthFooter({ authType }) {
    const { t } = useTranslation("auth");

    const content = {
        register: {
            message: t('register.registerPage.footer.haveAccount'),
            linkText: t('register.registerPage.footer.loginLink'),
            linkTo: ROUTES.LOGIN
        },
        login: {
            message: t('login.loginPage.footer.noAccount'),
            linkText: t('login.loginPage.footer.registerLink'),
            linkTo: ROUTES.REGISTER
        }
    }

    return (
    <footer className="d-flex justify-content-between align-items-center mb-3">
        <p className="ms-2 my-auto aa-txt-text fst-italic">
            {content[authType].message}
            <Link 
                to={content[authType].linkTo}
                className="text-decoration-none aa-txt-secondary is-hoverable ms-2"    
            >
                {content[authType].linkText}
            </Link>
        </p>
        <LanguageSwitcher />
    </footer>
    );
}