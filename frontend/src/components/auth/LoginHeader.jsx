import { useTranslation } from 'react-i18next';

export default function RegisterHeader() {
    const { t } = useTranslation('auth');

    return (
    <div>
    
        {/* Login Title */}
        <h1 className="font-bold mt-2 mb-4">{t('login.loginPage.header.title')}</h1>
        

        {/* Login Subtitle */}
        <p className="text-gray-600 mb-5">
            {t('login.loginPage.header.subtitle1')}
            <br />
            {t('login.loginPage.header.subtitle2')}
        </p>

    
    </div>
    );
}