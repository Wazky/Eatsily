import { positiveInteger, required } from "../../validationHelpers";

const PREPARATION_TIME_FIELD_NAME = 'entities.recipe.labels.preparationTime';

export const recipePreparationTimeValidationRules = [
    required(PREPARATION_TIME_FIELD_NAME),
    positiveInteger(PREPARATION_TIME_FIELD_NAME)
];