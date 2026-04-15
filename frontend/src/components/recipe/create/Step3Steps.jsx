import { useTranslation } from "react-i18next";
import { formatDate, validationMessage } from "../../../utils/helpers";

export default function Step3Steps({
    formData,
    errors,
    addStep,
    updateStep,
    removeStep
}) {
    const { t } = useTranslation("recipes");

    return (
    <div className="wizard-step-content">

        {errors.steps && (
            <div className="aa-alert aa-alert--error mb-3">
                <i className="bi bi-exclamation-circle"></i>
                {validationMessage(t, errors.steps)}
            </div>
        )}

        {formData.steps.map((step, index) => {
            return <StepRow 
                key={index}
                index={index}
                step={step}
                descriptionError={errors.steps && !step.description}
                updateStep={updateStep}
                removeStep={removeStep}
            />;
        })}

        <button
            type="button"
            className="wizard-add-btn"
            onClick={addStep}
        >
            <i className="bi bi-plus-lg"></i>
            {t('recipeCreate.wizard.actions.addStep')}    
        </button>
        
    </div>
    );
}

export function StepRow({ 
    index, 
    step, 
    descriptionError,
    updateStep, 
    removeStep 
}) {
    const { t } = useTranslation("recipes");

    return (
    <div className="step-row">
        
        <div className="step-row__number">
            {index + 1}
        </div>

        <div className="step-row__fields">
            <input 
                type="text"
                className="form-control aa-input mb-2"
                placeholder={t('recipeCreate.wizard.form.placeholders.stepTitle')}
                value={step.title}
                onChange={e => updateStep(index, 'title', e.target.value)}
            />

            <textarea 
                className={`form-control aa-input 
                    ${descriptionError ? 'is-invalid' : ''}`
                }
                rows={3}
                placeholder={t('recipeCreate.wizard.form.placeholders.instruction')}
                value={step.description}
                onChange={e => updateStep(index, 'description', e.target.value)}
            />
        </div>

        <button
            type="button"
                className="aa-btn-icon aa-btn-icon--danger"
                onClick={() => removeStep(index)}
                title={t('recipeCreate.wizardForm.step3.removeStep')}
        >
            <i className="bi bi-trash"></i>
        </button>

    </div>
    );
}