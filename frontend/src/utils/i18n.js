import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// Import translation files

// English translations
import enAuth from '../i18n/locales/en/auth.json';
import enCommon from '../i18n/locales/en/common.json';
import enErrors from '../i18n/locales/en/errors.json';
import enRecipes from '../i18n/locales/en/recipes.json';

// Spanish translations
import esAuth from '../i18n/locales/es/auth.json';
import esCommon from '../i18n/locales/es/common.json';
import esErrors from '../i18n/locales/es/errors.json';
import esRecipes from '../i18n/locales/es/recipes.json';

// Galician translations
import glAuth from '../i18n/locales/gl/auth.json';
import glCommon from '../i18n/locales/gl/common.json';
import glErrors from '../i18n/locales/gl/errors.json';
import glRecipes from '../i18n/locales/gl/recipes.json';

// Combine translations into a single object
const resources = {
    en: {
        auth: enAuth,
        common: enCommon,
        errors: enErrors,
        recipes: enRecipes
    },
    es: {
        auth: esAuth,
        common: esCommon,
        errors: esErrors,
        recipes: esRecipes
    },
    gl: {
        auth: glAuth,
        common: glCommon,
        errors: glErrors,
        recipes: glRecipes
    }
};

// Initialize i18n
i18n
    .use(initReactI18next) // Passes i18n down to react-i18next
    .init({
        resources,
        // Check localStorage for saved language, default to English
        lng: localStorage.getItem('language') || 'en', 
        interpolation: {
            escapeValue: false, // React already safes from xss
        }
    });

export default i18n;