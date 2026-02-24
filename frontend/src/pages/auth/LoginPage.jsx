import { useTranslation } from "react-i18next";

import AuthLayout from "../../components/layout/AuthLayout";
import AuthContentContainer from "../../components/auth/common/AuthContentContainer";
import LoginHeader from "../../components/auth/LoginHeader";
import LoginForm from "../../components/auth/LoginForm";
import AuthFooter from "../../components/auth/common/AuthFooter";

import AuthSideImg from "../../components/auth/common/AuthSideImg";
// Image Imports
import loginImg from "../../assets/images/login-img3.jpg"    

export default function LoginPage() {

    const { t } = useTranslation('auth');

    return (
    <AuthLayout authType="login">
        
        {/* Side Image */}
        <AuthSideImg 
            imgSrc={loginImg}
            altText={t('loginPage.main.sideImageAlt')}
        />

        {/* Content Container */}
        <AuthContentContainer>

            <div className="me-5">
                {/* Login Header */}
                <LoginHeader />
            </div>

            {/* Login Form */}
            <LoginForm />

            <div className="me-5">
                {/* Footer with link to Register */}
                <AuthFooter authType="login" />
            </div>

        </AuthContentContainer>
        
    </AuthLayout>
    );
}