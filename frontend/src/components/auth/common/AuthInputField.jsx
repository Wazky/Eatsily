import { useTranslation } from "react-i18next";
import { validationMessage } from "../../../utils/helpers";

export default function AuthInputField({
    fieldName,
    name,
    type,
    placeholder = "",
    fieldValue,
    handleChange,
    inputClasses = "",
    error = null,
    errorBack = null
}) {
    const { t } = useTranslation('common', 'errors');

    return (
        <div className="mt-2 pb-4 me-2">
            
            {/* Label for the input field */}
            <label
                className="form-label justify-content-start fw-bold mb-2"
                htmlFor={name}
            >
                {fieldName}
            </label>
            
            {/* Input field */}
            <input
                className={"form-control " + inputClasses + (error || errorBack ? " is-invalid" : "") }
                type={type} 
                name={name}
                placeholder={placeholder}
                value={fieldValue}                
                onChange={handleChange}
            />
            
            {/* Error message display */}
            {error && (
                <div className="text-danger ms-2 mt-1">
                    {validationMessage(t, error)}
                </div>
            )}
            {errorBack && (
                <div className="text-danger ms-2 mt-1">
                    {t(`errors:${errorBack}`)}
                </div>
            )}

        </div>
    );
} 
    