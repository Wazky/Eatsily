import { useState } from "react";
import { useTranslation } from "react-i18next";
import AuthInputField from "./common/AuthInputField";

export default function LoginForm() {
    const { t } = useTranslation('auth');

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        // Handle login logic here
    }

    return (
    <form className="auth-form me-5" onSubmit={handleSubmit}>

        <AuthInputField
            fieldName={t('login.loginPage.main.form.fields.username')}
            name="username"
            type="text"
            placeholder={t('login.loginPage.main.form.placeholders.username')}
            fieldValue={username}
            handleChange={(e) => setUsername(e.target.value)}
        />

        <AuthInputField
            fieldName={t('login.loginPage.main.form.fields.password')}
            name="password"
            type="password"
            placeholder={t('login.loginPage.main.form.placeholders.password')}
            fieldValue={password}
            handleChange={(e) => setPassword(e.target.value)}
        />

        <div className="d-flex justify-content-start mt-2 mb-4 w-auto">
            <button 
                type="submit" 
                className="aa-bg-secondary fw-bold rounded-2 border-light py-2 w-100"
            >
                {t('login.loginPage.main.form.buttons.login')}
            </button>
        </div>
    </form>
    );
}