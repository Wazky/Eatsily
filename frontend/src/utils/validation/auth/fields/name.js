import { required, minLength, maxLength, alphabetic } from "../../validationHelpers";

// == Name Validation ==

// Name from Registration Form constants for field names and validation parameters
const NAME_FIELD_NAME = 'Name';
const NAME_MIN_LENGTH = 2;
const NAME_MAX_LENGTH = 50;

export const nameValidationRules = [
    required(NAME_FIELD_NAME),
    minLength(NAME_FIELD_NAME, NAME_MIN_LENGTH),
    maxLength(NAME_FIELD_NAME, NAME_MAX_LENGTH),
    alphabetic(NAME_FIELD_NAME)
];
