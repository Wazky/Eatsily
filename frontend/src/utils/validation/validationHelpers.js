import validators from "./validators";

/**
 * ===== HELPER FUNCTIONS =====
 * These functions generate validation rules 
 * based on the provided field names and parameters,
 * utilizing the validators defined in validators.js.
 */

export const required = (fieldName) => ({
    validator: validators.required,
    message: {
        code: 'validation.fieldRequired',
        params: { field: fieldName }
    }
});

export const minLength = (fieldName, fieldLength) => ({
    validator: (value) => validators.minLength(value, fieldLength),
    message: {
        code: 'validation.minLength',
        params: { field: fieldName, length: fieldLength }
    }
});

export const maxLength = (fieldName, fieldLength) => ({
    validator: (value) => validators.maxLength(value, fieldLength),
    message: {
        code: 'validation.maxLength',
        params: { field: fieldName, length: fieldLength }
    }    
});

export const alphabetic = (fieldName) => ({
    validator: (value) => validators.alphabetic(value),
    message: {
        code: 'validation.alphabetic',
        params: { field: fieldName }
    }
});

export const alphanumeric = (fieldName) => ({
    validator: (value) => validators.alphanumeric(value),
    message: {
        code: 'validation.alphanumeric',
        params: { field: fieldName }
    }
});

export const alphanumericUnderscore = (fieldName) => ({
    validator: (value) => validators.alphanumericUnderscore(value),
    message: {
        code: 'validation.alphanumericUnderscore',
        params: { field: fieldName }
    }
});

export const email = (fieldName) => ({
    validator: (value) => validators.email(value),
    message: {
        code: 'validation.email'
    }
});

export const password = (fieldName) => ({
    validator: (value) => validators.password(value),
    message: {
        code: 'validation.password'
    }
});
