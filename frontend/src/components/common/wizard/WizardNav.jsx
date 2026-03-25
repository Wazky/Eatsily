import { useTranslation } from "react-i18next";

export default function WizardNav({
    currentStep,
    totalSteps,
    onNext,
    onPrev,
    omCancel,
    onSubmit,
    loading
}) {
    const { t } = useTranslation("recipes");
    const isFirst = currentStep === 1;
    const isLast = currentStep === totalSteps;

    return (
    <div className="wizard-nav">
        <BackButton
            isFirst={isFirst}
            onPrev={onPrev}
            onCancel={omCancel}
        />

        <span className="wizard-nav__counter">
            {t("recipeCreate.wizard.nav.stepOf", { current: currentStep, total: totalSteps })}
        </span>

        {isLast 
            ? <SubmitButton loading={loading} onSubmit={onSubmit} /> 
            : <NextButton onNext={onNext} />
        }    

    </div>
    );
}

export function BackButton({ isFirst, onPrev, onCancel }) {
    const { t } = useTranslation("recipes");

    return (
    <button
        type="button"
        className="btn aa-ghost"
        onClick={isFirst ? onCancel : onPrev}
    >
        {isFirst 
            ? t("recipeCreate.wizard.nav.cancel")
            : <><i className="bi bi-arrow-left me-2"></i>{t("recipeCreate.wizard.nav.previous")}</>
        }
    </button>        
    );
}

export function SubmitButton({ loading, onSubmit }) {
    const { t } = useTranslation("recipes");

    return (
    <button type="button" className="btn aa-primary aa-lg aa-pill" onClick={onSubmit} disabled={loading}>
        {loading 
            ? <><i className="bi bi-hourglass-split me-2" />{t("recipeCreate.wizard.nav.submitting")}</>
            : <><i className="bi bi-check-lg me-2" />{t("recipeCreate.wizard.nav.submit")}</>
        }
    </button>        
    );
}

export function NextButton({ onNext }) {
    const { t } = useTranslation("recipes");

    return (
    <button type="button" className="btn aa-primary" onClick={onNext}>
        {t("recipeCreate.wizard.nav.next")}
        <i className="bi bi-arrow-right ms-2"></i>
    </button>
    );
}