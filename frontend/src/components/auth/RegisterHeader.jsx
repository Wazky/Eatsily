import { useTranslation } from "react-i18next";

export default function RegisterHeader() {
    const { t } = useTranslation('auth');

    return (
    <div>
        <h1 className="font-bold mt-2 mb-4">{t('register.registerPage.header.title')}</h1>
        <p className="text-gray-600 mb-4">
            {t('register.registerPage.header.subtitle')}
        </p>
    </div>
    );
}