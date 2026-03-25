import { useTranslation } from "react-i18next";
import { formatDate } from "../../../utils/helpers";

export default function Step4Publish({ formData, updateField }) {
    const { t } = useTranslation("recipes");

    return (
    <div className="wizard-step-content">
        <div className="publish-options">

            {/* Public Option */}
            <Option 
                isOn={formData.isPublic}
                variant={"default"}
                fieldName={'isPublic'}
                updateField={updateField}
                icon={formData.isPublic ? 'bi-globe' : 'bi-lock'}
                title={formData.isPublic ? t('recipeCreate.wizard.form.privacy.public.title') : t('recipeCreate.wizard.form.privacy.private.title')}
                desc={formData.isPublic ? t('recipeCreate.wizard.form.privacy.public.desc') : t('recipeCreate.wizard.form.privacy.private.desc')}
            />

            {/* Lunchbox Option */}
            <Option 
                isOn={formData.isLunchbox}
                variant={"primary"}
                fieldName={'isLunchbox'}
                updateField={updateField}
                icon={'bi-suitcase-lg'}
                title={t('recipeCreate.wizard.form.lunchbox.title')}
                desc={t('recipeCreate.wizard.form.lunchbox.desc')}
            />

        </div>
    </div>
    );
}

export function Option({ 
    isOn,
    variant = 'default',
    fieldName, 
    updateField, 
    icon, 
    title, 
    desc 
}) {

    return (
    <div 
        className={`publish-option ${isOn ? `publish-option--on--${variant}` : ''}`}
        onClick={() => updateField(fieldName, !isOn)}
        role="button"
        tabIndex={0}
    >
        <ToggleElement isOn={isOn} variant={variant} />

        <OptionInfo
            isOn={isOn}
            variant={variant} 
            icon={icon}
            title={title}
            desc={desc}
        />

    </div>        
    );
}

export function ToggleElement({ isOn, variant = 'default' }) {
    return (
    <div className={`publish-option__toggle ${isOn ? `publish-option__toggle--on--${variant}` : ''}`}></div>        
    );
}

export function OptionInfo({ isOn, variant = 'default', icon, title, desc }) {
    return (
    <div className="publish-option__info">
        <i className={`bi ${icon} publish-option__icon`}></i>
        <div>
            <p className={`publish-option__title ${isOn ? `publish-option__title--on--${variant}` : ''}`}>
                {title}
            </p>
            <p className={`publish-option__sub ${isOn ? `publish-option__sub--on--${variant}` : ''}`}>
                {desc}
            </p>
        </div>
    </div>
    );
}