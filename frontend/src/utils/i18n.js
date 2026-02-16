import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// Import translation files

// English translations
import enAuth from '../i18n/locales/en/auth.json';
import enCommon from '../i18n/locales/en/common.json';

// Spanish translations
import esAuth from '../i18n/locales/es/auth.json';
import esCommon from '../i18n/locales/es/common.json';

// Galician translations
import glAuth from '../i18n/locales/gl/auth.json';
import glCommon from '../i18n/locales/gl/common.json';

// Combine translations into a single object
const resources = {
    en: {
        auth: enAuth,
        common: enCommon
    },
    es: {
        auth: esAuth,
        common: esCommon
    },
    gl: {
        auth: glAuth,
        common: glCommon
    }
};

// Initialize i18n
i18n
    .use(initReactI18next) // Passes i18n down to react-i18next
    .init({
        resources,
        lng: 'en', // Default language
        interpolation: {
            escapeValue: false, // React already safes from xss
        }
    });

export default i18n;