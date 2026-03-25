
import { useNavigate } from "react-router-dom";
import { 
    recipeCreationStep1ValidationSchema,
    recipeCreationStep2ValidationSchema,
    recipeCreationStep3ValidationSchema,
    recipeCreationStep4ValidationSchema,
    recipeFullValidationSchema
} from "../../utils/validation/recipe/recipeCreationValidationSchema";
import { useState } from "react";
import RecipeService from "../../services/recipes/RecipeService";
import { ROUTES } from "../../constants/routes";
import validators from "../../utils/validation/validators";

const TOTAL_STEPS = 4;

// Step 4 has no specific fields, but we keep the structure for consistency
const FORM_STEP_SCHEMAS = {
    1: recipeCreationStep1ValidationSchema,
    2: recipeCreationStep2ValidationSchema,
    3: recipeCreationStep3ValidationSchema,
    4: recipeCreationStep4ValidationSchema 
};

const initialFormData = {
    // Step 1: Basic Info
    title : '',
    description: '',
    difficulty: '',
    servings: 1,
    preparationTime: 0,
    cookingTime: 0,
    // Step 2: Ingredients
    ingredients: [],
    // Step 3: Preparation Steps
    steps: [],
    // Step 4: Publish Options
    isPublic: false,
    isLunchbox: false
};

/**
 * Validates a set of fields against a schema.
 * Returns an errors object { fieldName: errorCode } or {} if valid.
 */
function validateSchema(data, schema) {
    const errors = {};
    for (const [field, rules] of Object.entries(schema)) {
        for (const rule of rules) {
            if (!rule.validator(data[field])) {
                errors[field] = rule.message;
                break;
            }
        }
    }
    return errors;
}

export default function useCreateRecipe() {
    const navigate = useNavigate();

    const [currentStep, setCurrentStep] = useState(1);
    const [formData, setFormData] = useState(initialFormData);
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [submitError, setSubmitError] = useState(null);

    const [ingredientFieldErrors, setIngredientFieldErrors] = useState([]);

    const updateField = (field, value) => {
        setFormData(prev => ({ ...prev, [field]: value }));

        if (errors[field]) {
            setErrors(prev => { 
                const e = { ...prev };
                delete e[field];
                return e
            });
        }
    };

    // -- Ingredient Handlers -------------------------------------------------

    const addIngredient = () => {
        setFormData(prev => ({ 
            ...prev,
            ingredients: [
                ...prev.ingredients,
                { 
                    ingredientId: null,
                    ingredientName: '',
                    quantity: '',
                    unitId: null,
                    notes: ''
                }
            ]
        }));
    };

    const updateIngredient =  (index, field, value) => {
        setFormData(prev => {
            const updatedIngredients = [...prev.ingredients];
            updatedIngredients[index] = { ...updatedIngredients[index], [field]: value };
            return { ...prev, ingredients: updatedIngredients };
        });
        setIngredientFieldErrors(prev => {
            const updated = [...prev];
            if (updated[index]) {
                updated[index] = { ...updated[index], [field]: false};
            }

            // If no field errors, clear general ingredients error
            const allClear = updated.every(ingErr => (ingErr === null || Object.values(ingErr).every(e => e === false)));
            if (allClear) {
                setErrors(prevErrors => {
                    const newErrors = { ...prevErrors};
                    delete newErrors.ingredients;
                    return newErrors;
                })
            }

            return updated;
        });
    };

    const removeIngredient = (index) => {
        setFormData(prev => ({
            ...prev,
            ingredients: prev.ingredients.filter((_, i) => i !== index)
        }));
    };

    // -- Step Handlers -------------------------------------------------

    const addStep = () => {
        setFormData(prev => ({
            ...prev,
            steps: [
                ...prev.steps,
                {
                    stepNumber: prev.steps.length + 1,
                    title: '',
                    description: ''
                }
            ]
        }));
    };

    const updateStep = (index, field, value) => {
        setFormData(prev => {
            const steps = [...prev.steps];
            steps[index] = { ...steps[index], [field]: value };
            return { ...prev, steps: steps };
        });
        if (field === 'description' && errors.steps) {
            setFormData(prev => {
                const allDescribed = prev.steps.every((prevStep, prevIndex) =>
                    prevIndex === index ? value?.trim() !== '' : prevStep.description?.trim() !== ''
                );
                if (allDescribed) {
                    setErrors(prevErrors => {
                        const newErrors = { ...prevErrors };
                        delete newErrors.steps;
                        return newErrors;
                    });
                }
                return prev;
            });
        }
    };

    const removeStep = (index) => {
        setFormData(prev => ({
            ...prev,
            steps: prev.steps.filter((_, i) => i !== index)
        }));
    };

    // -- Navigation Handlers -------------------------------------------------

    const nextStep = () => {
        const schema = FORM_STEP_SCHEMAS[currentStep];
        const stepErrors = validateSchema(formData, schema);

        if (Object.keys(stepErrors).length > 0) {
            setErrors(stepErrors);
            if (stepErrors.ingredients) {
                setIngredientFieldErrors(
                    validators.ingredientFieldErrors(formData.ingredients)
                );
            }
            return;
        }

        setErrors({});
        setIngredientFieldErrors([]);
        setCurrentStep(prev => Math.min(prev + 1, TOTAL_STEPS));
        return true;
    };

    const prevStep = () => {
        setErrors({});
        setCurrentStep(prev => Math.max(prev - 1, 1));
    };

    // -- Submission Handler -------------------------------------------------

    const submit = async () => {
        // Validate all steps before submission
        const fullErrors = validateSchema(formData, recipeFullValidationSchema);
        if (Object.keys(fullErrors).length > 0) {
            setErrors(fullErrors);
            return false;
        }

        setLoading(true);
        setSubmitError(null);

        try {
            const result = await RecipeService.createRecipe(formData);

            if (result.success) {
                navigate(ROUTES.RECIPE_DETAIL.replace(':id', result.data.id));
                return true;                
            } else {
                setSubmitError(result.error);
                return false;
            }
        } catch (err) {
            setSubmitError(err.message || 'An error occurred while creating the recipe');
            return false;
        } finally {
            setLoading(false);
        }
    };

    return {
        currentStep,
        totalSteps: TOTAL_STEPS,
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
    };

}