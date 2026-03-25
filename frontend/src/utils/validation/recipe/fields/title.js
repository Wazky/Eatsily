import { required, minLength, maxLength } from "../../validationHelpers";

const TITLE_FIELD_NAME = 'entities.recipe.labels.title';
const TITLE_MIN_LENGTH = 3;
const TITLE_MAX_LENGTH = 100;

export const recipeTitleValidationRules = [
    required(TITLE_FIELD_NAME),
    minLength(TITLE_FIELD_NAME, TITLE_MIN_LENGTH),
    maxLength(TITLE_FIELD_NAME, TITLE_MAX_LENGTH)
];