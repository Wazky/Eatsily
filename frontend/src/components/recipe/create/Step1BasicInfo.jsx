import { useTranslation } from "react-i18next";
import { upperFirst, validationMessage } from "../../../utils/helpers";

export default function Step1BasicInfo({ formData, errors, updateField }) {
    const { t } = useTranslation("recipes");

    return (
        <div className="wizard-step-content">

            {/* Title */}
            <div className="mb-3">
                <WizardFormGroup 
                    labelTitle={t("recipeLabels.title")}
                    required
                >
                    <input
                        type="text"
                        className={`form-control aa-input ${errors.title ? 'is-invalid' : ''}`}
                        placeholder={t("recipeCreate.wizard.form.placeholders.title")}
                        value={formData.title}
                        onChange={(e) => updateField("title", e.target.value)}
                    />
                    
                    {errors.title && <ErrorMessage error={errors.title} />}

                </WizardFormGroup>                
            </div>

            {/* Description */}
            <div className="mb-3">
                <WizardFormGroup 
                    labelTitle={t("recipeLabels.description")}
                >
                    <textarea
                        className={`form-control aa-input ${errors.description ? 'is-invalid' : ''}`}
                        rows={4}
                        placeholder={t("recipeCreate.wizard.form.placeholders.description")}
                        value={formData.description}
                        onChange={(e) => updateField("description", e.target.value)}
                    />

                    {errors.description && <ErrorMessage error={errors.description} />}

                </WizardFormGroup>                
            </div>

            {/* Difficulty, Servings and Times */}
            <div className="row g-3 mb-3">
                <div className="col-md-3">
                    
                    {/* Difficulty */}
                    <WizardFormGroup
                        labelTitle={t("recipeLabels.difficulty.label")}
                    >
                        <select
                            className={`form-select aa-input`}
                            value={formData.difficulty}
                            onChange={e => updateField("difficulty", e.target.value)}
                        >
                            {/* Obtain difficulty options from backend ? */}
                            <option disabled value=""> {t("recipeCreate.wizard.form.placeholders.difficulty")} </option>
                            <option value="none"> {t("recipeLabels.difficulty.none")} </option>
                            <option value="easy"> {t("recipeLabels.difficulty.easy")} </option>
                            <option value="medium"> {t("recipeLabels.difficulty.medium")} </option>
                            <option value="hard"> {t("recipeLabels.difficulty.hard")} </option>
                        </select>
                    </WizardFormGroup>
                </div>

                {/* Servings */}
                <div className="col-md-3">
                    <WizardFormGroup
                        labelTitle={t("recipeLabels.servings")}
                        required
                    >
                        <input
                            type="number"
                            className={`form-control aa-input ${errors.servings ? 'is-invalid' : ''}`}
                            min={1}
                            value={formData.servings}
                            onChange={e => updateField('servings', parseInt(e.target.value))}
                        />

                        {errors.servings && <ErrorMessage error={errors.servings} />}

                    </WizardFormGroup>
                </div>

                {/* Preparation Time */}
                <div className="col-md-3">
                    <WizardFormGroup
                        labelTitle={t("recipeLabels.preparationTime")}
                        required
                    >
                        <div className={`input-group ${errors.preparationTime ? 'is-invalid' : ''}`}>
                            <input
                                type="number"
                                className={`form-control aa-input ${errors.preparationTime ? 'is-invalid' : ''}`}
                                min={1}
                                value={formData.preparationTime}
                                onChange={e => updateField('preparationTime', parseInt(e.target.value))}
                            />
                            <span className="input-group-text aa-input-addon">min</span>

                        </div>   

                        { errors.preparationTime && <ErrorMessage error={errors.preparationTime} />}
                        
                    </WizardFormGroup>
                </div>

                {/* Cooking Time */}                    
                <div className="col-md-3">
                    <WizardFormGroup
                        labelTitle={t("recipeLabels.cookingTime")}
                    >
                        <div className="input-group">
                            <input
                                type="number"
                                className="form-control aa-input"
                                min={1}
                                value={formData.cookingTime}
                                onChange={e => updateField('cookingTime', parseInt(e.target.value))}
                            />
                            <span className="input-group-text aa-input-addon">min</span>
                        </div>
                    </WizardFormGroup>
                </div>

            </div>

        </div>
    );
}

export function WizardFormGroup({
    labelClasses = '',
    labelTitle = '',
    required = false,
    children
}) {
    const { t } = useTranslation("recipes");
    return (
        <div className="form-group">

            <label className={`form-label aa-label ${labelClasses}`}>
                {t(labelTitle)}
                {required && <span className="aa-required">*</span>}
            </label>

            {children}

        </div>
    );
}

export function ErrorMessage({ error }) {
    const { t } = useTranslation();
    return (
    <div className="invalid-feedback">
        {upperFirst(
            validationMessage(t, error)
        )}
    </div>     
    );
}