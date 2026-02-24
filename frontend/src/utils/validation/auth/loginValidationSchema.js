import { usernameValidationRules } from "./fields/username";
import { passwordValidationRules } from "./fields/password";

/**
 * Login Validation Schema: 
 * - Username: Required, 3-30 characters, alphanumeric with underscores
 * - Password: Required, 8-100 characters, must contain letters and numbers
 */
export const loginValidationSchema = {
    username: usernameValidationRules,
    password: passwordValidationRules
};