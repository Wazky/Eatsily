import { useTranslation } from "react-i18next";
import esFlag from "../../../assets/images/flags/es.png";
import enFlag from "../../../assets/images/flags/gb.png";
import glFlag from "../../../assets/images/flags/gl.png";


const SUPPORTED_LOCALES = [
    { value: 'es', flag: esFlag, labelKey: 'lang.es' },
    { value: 'en', flag: enFlag, labelKey: 'lang.en' },
    { value: 'gl', flag: glFlag, labelKey: 'lang.gl' },
];

export default function Step0Locale({ formData, updateField }) {
    const { t } = useTranslation("recipes");
    const { t: tCommon } = useTranslation("common");

    return (
        <div className="locale-select-screen">

            <div className="locale-select-screen__header">

                <i className="bi bi-translate locale-select-screen__icon" />
                <h2 className="locale-select-screen__title">
                    {t("recipeCreate.locale.title")}
                </h2>
                
                <p className="locale-select-screen__subtitle">
                    {t("recipeCreate.locale.subtitle")}
                </p>

            </div>

            <div className="locale-select-screen__options">
                {SUPPORTED_LOCALES.map(locale => (
                    <button
                        key={locale.value}
                        type="button"
                        className={`locale-option ${formData.locale === locale.value ? 'locale-option--selected' : ''}`}
                        onClick={() => updateField('locale', locale.value)}
                    >
                        <img 
                            src={locale.flag}
                            alt={t(locale.labelKey)}
                            className="locale-option__flag"
                        />
                        <span className="locale-option__name">{tCommon(locale.labelKey)}</span>
                        <span className="locale-option__desc">{t("recipeCreate.locale.create", { language: tCommon(locale.labelKey) })}</span>
                        {formData.locale === locale.value && (
                            <i className="bi bi-check-circle-fill locale-option__check" />
                        )}
                    </button>
                ))}
            </div>

            <p className="locale-select-screen__note">
                <i className="bi bi-info-circle me-1" />
                {t("recipeCreate.locale.note")}
            </p>

        </div>
    );
}