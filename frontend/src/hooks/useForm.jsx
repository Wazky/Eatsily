import { useCallback, useState } from "react";

export default function useForm(initialState, validationSchema = {}, onSubmit) {

    const [formData, setFormData] = useState(initialState);
    const [formErrors, setFormErrors] = useState({});

    const [isSubmitting, setIsSubmitting] = useState(false);

    /**
     * Validates a single field based on the provided validation schema.
     */
    const validateField = useCallback((fieldName, value) => {
        // Get the validation rules for the field from the schema
        const fieldValidations = validationSchema[fieldName] || [];

        // Iterate through the validation rules 
        for (const validation of fieldValidations) {
            // If the validation fails, return the corresponding error message
            if (!validation.validator(value)) {
                return validation.message;
            }
        }

        return null;

    }, [validationSchema]);

    /**
     * Validates the entire form by validating each field and collecting any errors.
     */
    const validateForm = useCallback(() => {
        const newErrors = {};

        Object.keys(validationSchema).forEach((fieldName) => {
            const error = validateField(fieldName, formData[fieldName]);

            if (error) {
                newErrors[fieldName] = error;
            }
        });

        return newErrors;

    }, [validateField, formData, validationSchema]);

    const handleChange = useCallback((e) => {
        const { name, value } = e.target;

        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));

        setFormErrors((prevErrors) => ({
            ...prevErrors,
            [name]: null,
        }));
    }, []);

    const handleSubmit = useCallback(async (e) => {
        e.preventDefault();

        // Validate the form before submission
        const errors = validateForm();
        setFormErrors(errors);

        // If there are validation errors, do not proceed with form submission
        if (Object.keys(errors).length > 0) {
            console.log("Form validation failed", errors);
            console.log("Form Errors:", formErrors);
            return;
        }

        // Start form submission process
        setIsSubmitting(true);
        try {
            await onSubmit(formData);

        } catch (error) {
            console.error("Error submitting form", error);
        
        } finally {
            setIsSubmitting(false);
        }

    }, [formData, onSubmit, validateForm]);

    // Set a form field value manually
    const setFormDataValue = useCallback((fieldName, value) => {
        setFormData((prevData) => ({
            ...prevData,
            [fieldName]: value,
        }));
    }, []);

    // Set a form field error manually
    const setFormErrorValue = useCallback((fieldName, error) => {
        setFormErrors((prevErrors) => ({
            ...prevErrors,
            [fieldName]: error,
        }));
    }, []);

    return {
        formData,
        formErrors,
        isSubmitting,
        handleChange,
        handleSubmit,
        setFormDataValue,
        setFormErrorValue
    };

};