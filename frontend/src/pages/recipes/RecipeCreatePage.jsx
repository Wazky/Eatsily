import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../constants/routes";
import WizardStepper from "../../components/common/wizard/WizardStepper";
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
        totalSteps,
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
                currentStep={currentStep}
                totalSteps={totalSteps}
                stepLabels={CREATE_RECIPE_STEP_LABELS}
            />

            <div className="wizard-card">

                <div className="wizard-card__header">
                    <h2 className="wizard-card__title">
                        {t(CREATE_RECIPE_STEP_LABELS[currentStep - 1])}
                    </h2>
                </div>

                {currentStep === 1 && (
                    <Step1BasicInfo {...stepProps} />
                )}

                {currentStep === 2 && (
                    <Step2Ingredients
                        {...stepProps}
                        ingredientFieldErrors={ingredientFieldErrors}
                        addIngredient={addIngredient}
                        updateIngredient={updateIngredient}
                        removeIngredient={removeIngredient}
                    />
                )}

                {currentStep === 3 && (
                    <Step3Steps
                        {...stepProps}
                        addStep={addStep}
                        updateStep={updateStep}
                        removeStep={removeStep}
                    />                
                )}

                {currentStep === 4 && (
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