import { required, minLength, maxLength, alphabetic } from "../../validationHelpers";

// == Surname Validation ==

// Surname from Registration Form constants for field names and validation parameters
const SURNAME_FIELD_NAME = 'Surname';
const SURNAME_MIN_LENGTH = 2;
const SURNAME_MAX_LENGTH = 50;

export const surnameValidationRules = [
    required(SURNAME_FIELD_NAME),
    minLength(SURNAME_FIELD_NAME, SURNAME_MIN_LENGTH),
    maxLength(SURNAME_FIELD_NAME, SURNAME_MAX_LENGTH),
    alphabetic(SURNAME_FIELD_NAME)
];
