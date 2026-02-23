import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../constants/routes";

import AuthInputField from "./common/AuthInputField";
import useAuthContext from "../../context/AuthContext";
import useForm from "../../hooks/useForm";

// Import the validation schema for the registration form
import { registrationValidationSchema } from "../../utils/validation/auth/registrationValidationSchema";

export default function RegisterForm() {
    const { t } = useTranslation('auth');
    const { t: tCommon } = useTranslation('common');
    const { t: tErrors } = useTranslation('errors');

    const navigate = useNavigate();
    const { register, loading, error } = useAuthContext();

    // State to hold any error message to display to the user
    const [ errorMessage, setErrorMessage] = useState(null);
    // State to hold any error message related to confirm password validation
    const [ confirmPasswordError, setConfirmPasswordError ] = useState(null);

    const initialValues = {
        name: "",
        surname: "",
        email: "",
        username: "",
        password: "",
        confirmPassword: ""
    };

    const handleRegister = async (formData) => {
        // Clear previous error message
        setErrorMessage(null);
        setConfirmPasswordError(null);
        
        // Check if password and confirm password match
        if (formData.password !== formData.confirmPassword) {
            setConfirmPasswordError({ code: 'validation.confirmPasswordMatch'});
            return;
        }
        
        // Call the register function from the useAuth hook with the form data
        const result = await register(formData);

        console.log('Registration result in form:', result);
        // Handle successful registration
        if (result.success) {
            // Redirect to login page after successful registration
            console.log('Registration successful, redirecting to login page...');
            navigate(ROUTES.DASHBOARD);
        
        // Handle registration failure
        } else {
            setErrorMessage(t('register.registerPage.main.form.error'));
        }

    };

    // Use the custom useForm hook to manage form state and validation
    const {
        formData,
        formErrors,
        isSubmitting,
        handleChange,
        handleSubmit
    } = useForm(initialValues, registrationValidationSchema, handleRegister);


    return (
    <form className="auth-form" onSubmit={handleSubmit}>

        {/* Display submission error if exists */}
        {errorMessage && (
            <div className="alert alert-danger text-center w-75 mt-3 me-3">
                <p className="mb-0 fw-bold">{errorMessage}</p>

                {error?.code && (
                    <small>
                        {tErrors(`errors:${error.code}`)}
                    </small>
                )}
            </div>
        )}

        <AuthInputField 
            fieldName={t('register.registerPage.main.form.fields.name')}
            name="name"            
            type="text"
            inputClasses="w-75"            
            placeholder={t('register.registerPage.main.form.placeholders.name')}            
            fieldValue={formData.name} 
            handleChange={handleChange}
            error={formErrors.name}
            errorBack={error?.details?.field === 'name' ? error.code : null}
        />

        <AuthInputField
            fieldName={t('register.registerPage.main.form.fields.surname')}
            name="surname" 
            type="text" 
            inputClasses="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.surname')}
            fieldValue={formData.surname} 
            handleChange={handleChange}
            error={formErrors.surname}
            errorBack={error?.details?.field === 'surname' ? error.code : null}
        />
        
        <AuthInputField 
            fieldName={t('register.registerPage.main.form.fields.email')}
            name="email"
            type="email"
            inputClasses="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.email')}
            fieldValue={formData.email}
            handleChange={handleChange}
            error={formErrors.email}
            errorBack={error?.details?.field === 'email' ? error.code : null}
        />

        <AuthInputField
            fieldName={t('register.registerPage.main.form.fields.username')}
            name="username"
            type="text"
            inputClasses="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.username')}
            fieldValue={formData.username}
            handleChange={handleChange}
            error={formErrors.username}
            errorBack={error?.details?.field === 'username' ? error.code : null}
        />

        <AuthInputField
            fieldName={t('register.registerPage.main.form.fields.password')}
            name="password"
            type="password"
            inputClasses="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.password')}
            fieldValue={formData.password}
            handleChange={handleChange}
            error={formErrors.password}
            errorBack={error?.details?.field === 'password' ? error.code : null}
        />

        <AuthInputField
            fieldName={t('register.registerPage.main.form.fields.confirmPassword')}
            name="confirmPassword"
            type="password"
            inputClasses="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.confirmPassword')}
            fieldValue={formData.confirmPassword}
            handleChange={handleChange}
            error={formErrors.confirmPassword || confirmPasswordError}
        />

        <div className="d-flex justify-content-start my-4 w-auto me-3">
            <button 
                type="submit" 
                className="aa-bg-secondary fw-bold rounded-2 border-light py-2 w-100"
                disabled={isSubmitting || loading}
            >
                {( isSubmitting || loading ) ?
                    t('register.registerPage.main.form.buttons.registering') :
                    t('register.registerPage.main.form.buttons.register')
                } 
            </button>
        </div>
    </form>
    );
}