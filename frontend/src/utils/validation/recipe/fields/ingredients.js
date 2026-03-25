import { ingredientValid, minArrayLength } from "../../validationHelpers";

const INGREDIENTS_FIELD_NAME = 'entities.recipe.labels.ingredients';
const INGREDIENTS_MIN_COUNT = 1;

export const recipeIngredientsValidationRules = [
    minArrayLength(INGREDIENTS_FIELD_NAME, INGREDIENTS_MIN_COUNT),
    ingredientValid(INGREDIENTS_FIELD_NAME)
];