import { use } from "react";
import LanguageSwitcher from "../LanguageSwitcher";
import { useTranslation } from "react-i18next";

export default function Header({ title }) {
    const { t } = useTranslation('common');

    return (
    <header className="d-flex flex-wrap align-content-center justify-content-between rounded-bottom bg-text-500 aa-txt-background mb-3 p-3 shadow-lg"> 
        <h1 className="fw-bold mb-0 ms-3">
            {t(title).toUpperCase()}
        </h1>

        <div className="mt-1">
            <LanguageSwitcher textColorClass="aa-txt-background" />
        </div>
    </header>
    );
}