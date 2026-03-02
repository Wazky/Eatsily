import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../constants/routes";

import AuthInputField from "./common/AuthInputField";
import useAuthContext from "../../context/AuthContext";
import useForm from "../../hooks/useForm";

// Import the validation schema for the login form
import { loginValidationSchema } from "../../utils/validation/auth/loginValidationSchema";

export default function LoginForm() {
    const { t } = useTranslation('auth', 'errors');
    const navigate = useNavigate();
    const { login, loading, error } = useAuthContext();

    const initialValues = {
        username: "",
        password: ""
    };

    const handleLogin = async (formData) => {
        const result = await login(formData);

        console.log('Login result in form:', result);
        // Handle successful login
        if (result.success) {
            // Redirect to dashboard
            navigate(ROUTES.HOME);
        } 
    };

    // Use the custom useForm hook to manage form state and validation
    const {
        formData,
        formErrors,
        isSubmitting,
        handleChange,
        handleSubmit,
    } = useForm(initialValues, loginValidationSchema, handleLogin);


    return (
    <form className="auth-form me-5" onSubmit={handleSubmit}>

        {/* Display submission error if exists */}
        {error?.code && (
            <div className="alert alert-danger mt-3">
                {t(`errors:${error.code}`)}
            </div>
        )}

        {/* Username Field */}
        <AuthInputField
            fieldName={t('login.loginPage.main.form.fields.username')}
            name="username"
            type="text"
            placeholder={t('login.loginPage.main.form.placeholders.username')}
            fieldValue={formData.username}
            handleChange={handleChange}
            error={formErrors.username}
        />        

        {/* Password Field */}
        <AuthInputField
            fieldName={t('login.loginPage.main.form.fields.password')}
            name="password"
            type="password"
            placeholder={t('login.loginPage.main.form.placeholders.password')}
            fieldValue={formData.password}
            handleChange={handleChange}
            error={formErrors.password}    
        />

        <div className="d-flex justify-content-start mt-2 mb-4 w-auto">
            <button 
                type="submit" 
                className="aa-bg-secondary fw-bold rounded-2 border-light is-hoverable py-2 w-100"
            >
                {t('login.loginPage.main.form.buttons.login')}
            </button>
        </div>
    </form>
    );
}