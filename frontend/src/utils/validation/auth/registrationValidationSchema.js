import { usernameValidationRules } from "./fields/username";
import { passwordValidationRules } from "./fields/password";
import { nameValidationRules } from "./fields/name";
import { surnameValidationRules } from "./fields/surname";
import { emailValidationRules } from "./fields/email";

/**
 * Registration Validation Schema:
 * - Name: Required, 2-50 characters, alphabetic only
 * - Surname: Required, 2-50 characters, alphabetic only
 * - Email: Required, must be a valid email format
 * - Username: Required, 3-30 characters, alphanumeric with underscores
 * - Password: Required, 8-100 characters, must contain letters and numbers
 * - Confirm Password: Required, must match the password field (validation to be implemented in the form)
 */
export const registrationValidationSchema = {
    name: nameValidationRules,
    surname: surnameValidationRules,
    email: emailValidationRules,
    username: usernameValidationRules,
    password: passwordValidationRules,
    confirmPassword: passwordValidationRules // Add rule to checl if matches password
}
