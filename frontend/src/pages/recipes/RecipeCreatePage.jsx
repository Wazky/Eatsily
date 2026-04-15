import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../constants/routes";
import WizardStepper from "../../components/common/wizard/WizardStepper";
import Step0Locale from "../../components/recipe/create/Step0Locale";
import Step1BasicInfo from "../../components/recipe/create/Step1BasicInfo";
import Step2Ingredients from "../../components/recipe/create/Step2Ingredients";
import Step3Steps from "../../components/recipe/create/Step3Steps";
import Step4Publish from "../../components/recipe/create/Step4Publish";
import WizardNav from "../../components/common/wizard/WizardNav";
import useCreateRecipe from "../../hooks/recipes/useCreateRecipe";

const CREATE_RECIPE_STEP_LABELS = [
    "recipeCreate.stepLabels.step1",
    "recipeCreate.stepLabels.step2",
    "recipeCreate.stepLabels.step3",
    "recipeCreate.stepLabels.step4"
];

export default function RecipeCreatePage() {
    const { t } = useTranslation("recipes");
    const navigate = useNavigate();

    const {
        currentStep,
        wizardStep,
        totalSteps,
        isLocaleStep,
        formData,
        errors,
        ingredientFieldErrors,
        loading,
        submitError,
        updateField,
        addIngredient,
        updateIngredient,
        removeIngredient,
        addStep,
        updateStep,
        removeStep,
        nextStep,
        prevStep,
        submit
    } = useCreateRecipe();

    const stepProps = { formData, errors, updateField };


    return (
    <div className="page-container">        
        <div className="wizard-container">

            <WizardStepper
                currentStep={wizardStep}
                totalSteps={totalSteps}
                stepLabels={CREATE_RECIPE_STEP_LABELS}
            />

            <div className="wizard-card">

                <div className="wizard-card__header">
                    <h2 className="wizard-card__title">
                        {t(CREATE_RECIPE_STEP_LABELS[wizardStep - 1])}
                    </h2>
                </div>

                {isLocaleStep && (
                    <Step0Locale
                        formData={formData}
                        updateField={updateField}
                    />                    
                )}

                {wizardStep === 1 && (
                    <Step1BasicInfo {...stepProps} />
                )}

                {wizardStep === 2 && (
                    <Step2Ingredients
                        {...stepProps}
                        ingredientFieldErrors={ingredientFieldErrors}
                        addIngredient={addIngredient}
                        updateIngredient={updateIngredient}
                        removeIngredient={removeIngredient}
                    />
                )}

                {wizardStep === 3 && (
                    <Step3Steps
                        {...stepProps}
                        addStep={addStep}
                        updateStep={updateStep}
                        removeStep={removeStep}
                    />                
                )}

                {wizardStep === 4 && (
                    <Step4Publish {...stepProps} />
                )}

                {submitError && (
                    <div className="aa-alert aa-alert-error mt-3">
                        <i className="bi bi-exclamation-circle"></i>
                        {submitError}
                    </div>
                )}

                <WizardNav 
                    currentStep={currentStep}
                    totalSteps={totalSteps}
                    onNext={nextStep}
                    onPrev={prevStep}
                    onCancel={() => navigate(ROUTES.MY_RECIPES)}
                    onSubmit={submit}
                    loading={loading}
                />

            </div>

        </div>
    </div>
    );
}