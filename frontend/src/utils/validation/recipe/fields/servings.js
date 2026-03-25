import { positiveInteger, required } from "../../validationHelpers";

const SERVNGS_FIELD_NAME = 'entities.recipe.labels.servings';

export const recipeServingsValidationRules = [
    required(SERVNGS_FIELD_NAME),
    positiveInteger(SERVNGS_FIELD_NAME)
];