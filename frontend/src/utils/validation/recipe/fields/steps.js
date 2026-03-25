import { minArrayLength, stepValid } from "../../validationHelpers";

const STEPS_FIELD_NAME = 'entities.recipe.labels.steps';
const STEPS_MIN_COUNT = 1;

export const recipeStepsValidationRules = [
    minArrayLength(STEPS_FIELD_NAME, STEPS_MIN_COUNT),
    stepValid(STEPS_FIELD_NAME)
];