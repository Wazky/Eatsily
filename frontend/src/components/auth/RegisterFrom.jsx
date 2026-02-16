import { useState } from "react";
import { useTranslation } from "react-i18next";
import AuthInputField from "./common/AuthInputField";

export default function RegisterForm() {
    const { t } = useTranslation('auth');

    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        // Handle registration logic here
    }

    return (
    <form className="auth-form" onSubmit={handleSubmit}>

        <AuthInputField 
            fieldName={t('register.registerPage.main.form.fields.name')}
            name="name"            
            type="text"
            inputClass="w-75"            
            placeholder={t('register.registerPage.main.form.placeholders.name')}            
            fieldValue={name} 
            handleChange={(e) => setName(e.target.value)}
        />

        <AuthInputField
            fieldName={t('register.registerPage.main.form.fields.surname')}
            name="surname" 
            type="text" 
            inputClass="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.surname')}
            fieldValue={surname} 
            handleChange={(e) => setSurname(e.target.value)} 
        />
        
        <AuthInputField 
            fieldName={t('register.registerPage.main.form.fields.email')}
            name="email"
            type="email"
            inputClass="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.email')}
            fieldValue={email}
            handleChange={(e) => setEmail(e.target.value)}
        />

        <AuthInputField
            fieldName={t('register.registerPage.main.form.fields.username')}
            name="username"
            type="text"
            inputClass="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.username')}
            fieldValue={username}
            handleChange={(e) => setUsername(e.target.value)}
        />

        <AuthInputField
            fieldName={t('register.registerPage.main.form.fields.password')}
            name="password"
            type="password"
            inputClass="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.password')}
            fieldValue={password}
            handleChange={(e) => setPassword(e.target.value)}
        />

        <AuthInputField
            fieldName={t('register.registerPage.main.form.fields.confirmPassword')}
            name="confirmPassword"
            type="password"
            inputClass="w-75"
            placeholder={t('register.registerPage.main.form.placeholders.confirmPassword')}
            fieldValue={password}
            handleChange={(e) => setPassword(e.target.value)}
        />

        <div className="d-flex justify-content-start my-4 w-auto">
            <button 
                type="submit" 
                className="aa-bg-secondary fw-bold rounded-2 border-light py-2 w-100"
            >
                {t('register.registerPage.main.form.buttons.register')}
            </button>
        </div>
    </form>
    );
}