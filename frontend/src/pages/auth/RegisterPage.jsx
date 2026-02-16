import AuthLayout from "../../components/layout/AuthLayout";
import AuthContentContainer from "../../components/auth/common/AuthContentContainer";
import RegisterHeader from "../../components/auth/RegisterHeader";
import RegisterForm from "../../components/auth/RegisterFrom";
import AuthFooter from "../../components/auth/common/AuthFooter";

import AuthSideImg from "../../components/auth/common/AuthSideImg";
// Image Imports
import registerImg from "../../assets/images/register-img.jpg"
import { useTranslation } from "react-i18next";


export default function RegisterPage() {
    const { t } = useTranslation('auth');

    return (
    <AuthLayout authType="register">

        {/* Content Container */}
        <AuthContentContainer>
            
            {/* Register Header */}
            <RegisterHeader />

            {/* Register Form */}
            <RegisterForm />   

            {/* Footer with link to Login */}
            <AuthFooter authType="register" />

        </AuthContentContainer>
            
        {/* Side Image */}
        <AuthSideImg
            imgSrc={registerImg}
            altText={t('register.registerPage.main.sideImageAlt')}
        />

    </AuthLayout>
    );
}