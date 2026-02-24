/**
 * A collection of validation functions for form inputs.
 */

export const validators = {

    required: (value) => {
        return value && value.trim() !== ''
    },

    minLength: (value, min) => {
        return value && value.length >= min
    },

    maxLength: (value, max) => {
        return value && value.length <= max
    },

    alphabetic: (value) => {
        const alphaRegex = /^[A-Za-z]+$/;
        return alphaRegex.test(value);
    },

    alphanumeric: (value) => {
        const alphaNumRegex = /^[A-Za-z0-9]+$/;
        return alphaNumRegex.test(value);
    },

    alphanumericUnderscore: (value) => {
        const alphaNumUnderscoreRegex = /^[A-Za-z0-9_]+$/;
        return alphaNumUnderscoreRegex.test(value);
    },

    email: (value) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(value);
    },

    password: (value) => {
        // Password must be at least 8 characters long and contain at least one letter and one number
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
        return passwordRegex.test(value);
    }
};

export default validators;