
export default function AuthInputField({
    fieldName,
    name,
    type,
    fieldValue,
    handleChange,
    placeholder = "",
    inputClass = ""
}) {

    return (
        <div className="mt-2">
            <div className="pb-4">
                <label
                    className="form-label justify-content-start fw-bold mb-2"
                    htmlFor={name}
                >
                    {fieldName}
                </label>
                <input
                    className={"form-control " + inputClass}
                    type={type} 
                    name={name}
                    placeholder={placeholder}
                    value={fieldValue}                
                    onChange={handleChange}
                />
                
            </div>
        </div>
    );
} 
    