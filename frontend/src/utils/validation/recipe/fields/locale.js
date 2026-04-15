import { required } from "../../validationHelpers";

const LOCALE_FIELD_NAME = 'entitites.recipe.labels.locale';

export const recipeLocaleValidationRules = [
    required(LOCALE_FIELD_NAME)
];