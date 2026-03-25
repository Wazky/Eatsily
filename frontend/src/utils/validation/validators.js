/**
 * A collection of validation functions for form inputs.
 */

export const validators = {

    required: (value) => {
        return value !== null && value !== undefined && String(value).trim() !== '';
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
    },

    positiveInteger: (value) => {
        const num = Number(value);
        return Number.isInteger(num) && num >= 1;
    },

    nonNegativeInteger: (value) => {
        const num = Number(value);
        return Number.isInteger(num) && num >= 0;
    },


    minArrayLength: (value, min) => {
        return Array.isArray(value) && value.length >= min;
    },

    /**
     * Validates that each ingredient in the list has all required fields:
     * - ingredientId must be a positive number
     * - quantity must be a positive number
     * - unitId must be a positive number
     */
    ingredientValid: (value) => {
        if (!Array.isArray(value) || value.length === 0) return false;
        return value.every(ing =>
            ing.ingredientId > 0 &&
            Number(ing.quantity) > 0 &&
            ing.unitId > 0
        );
    },

    ingredientFieldErrors: (value) => {
        if (!Array.isArray(value)) return [];

        return value.map(ing => ({
            ingredientId: !ing.ingredientId || ing.ingredientId <= 0,
            quantity: !ing.quantity || Number(ing.quantity) <= 0,
            unitId: !ing.unitId || ing.unitId <= 0
        }));
    },

    /**
     * Validates that each step in the list has all required fields:
     * - description must be a non-empty string
     */
    stepValid: (value) => {
        if (!Array.isArray(value) || value.length === 0) return false;
        return value.every(step =>
            step.description && step.description.trim() !== ''
        );
    },

};

export default validators;