import  { recipeTitleValidationRules } from "./fields/title";
import  { recipeServingsValidationRules } from "./fields/servings";
import  { recipeIngredientsValidationRules } from "./fields/ingredients";
import  { recipeStepsValidationRules } from "./fields/steps";
import { recipePreparationTimeValidationRules } from "./fields/preparationTime";

// Step 1: Basic Info
export const recipeCreationStep1ValidationSchema = {
    title: recipeTitleValidationRules,
    servings: recipeServingsValidationRules,
    preparationTime: recipePreparationTimeValidationRules

};

// Step 2: Ingredients
export const recipeCreationStep2ValidationSchema = {
    ingredients: recipeIngredientsValidationRules
};

// Step 3: Preparation Steps
export const recipeCreationStep3ValidationSchema = {
    steps: recipeStepsValidationRules
};

// Step 4: Publish Options
export const recipeCreationStep4ValidationSchema = {}

// Full Schema for final submission
export const recipeFullValidationSchema = {
    ...recipeCreationStep1ValidationSchema,
    ...recipeCreationStep2ValidationSchema,
    ...recipeCreationStep3ValidationSchema,
    ...recipeCreationStep4ValidationSchema
};