import { required, minLength, maxLength, password } from "../../validationHelpers";

// == Password Validation ==

// Password Constants for field name and validation parameters
const PASSWORD_FIELD_NAME = 'Password';
const PASSWORD_MIN_LENGTH = 8;
const PASSWORD_MAX_LENGTH = 100;

export const passwordValidationRules = [
    required(PASSWORD_FIELD_NAME),
    minLength(PASSWORD_FIELD_NAME, PASSWORD_MIN_LENGTH),
    maxLength(PASSWORD_FIELD_NAME, PASSWORD_MAX_LENGTH),
    password(PASSWORD_FIELD_NAME)
];
