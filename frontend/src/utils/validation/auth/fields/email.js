import { required, email } from "../../validationHelpers";

// == Email Validation ==

// Email from Registration Form constants for field name and validation parameters
const EMAIL_FIELD_NAME = 'Email';

export const emailValidationRules = [
    required(EMAIL_FIELD_NAME),
    email(EMAIL_FIELD_NAME)
];
