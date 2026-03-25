import { required, minLength, maxLength, alphanumericUnderscore } from "../../validationHelpers";
// == Username Validation ==

// Username Constants for field name and validation parameters
const USERNAME_FIELD_NAME = 'entities.user.labels.username';
const USERNAME_MIN_LENGTH = 3;
const USERNAME_MAX_LENGTH = 30;

/**
 * Username Validation Rules:
 * - Required
 * - Minimum length of 3 characters
 * - Maximum length of 30 characters
 * - Must be alphanumeric and can include underscores
 */
export const usernameValidationRules =  [
    required(USERNAME_FIELD_NAME),
    minLength(USERNAME_FIELD_NAME, USERNAME_MIN_LENGTH),
    maxLength(USERNAME_FIELD_NAME, USERNAME_MAX_LENGTH),
    alphanumericUnderscore(USERNAME_FIELD_NAME)
];