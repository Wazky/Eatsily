import { useTranslation } from 'react-i18next';
import esFlag from '../../assets/images/flags/es.png';
import enFlag from '../../assets/images/flags/gb.png';
import glFlag from '../../assets/images/flags/gl.png';

const languages = [
    { code: 'es', name: 'lang.es', flag: esFlag },
    { code: 'en', name: 'lang.en', flag: enFlag },
    { code: 'gl', name: 'lang.gl', flag: glFlag } 
];

export default function LanguageSwitcher() {
    const { t } = useTranslation("common");
    const { i18n } = useTranslation();
    
    const changeLanguage = (lng) => {
        i18n.changeLanguage(lng);
        localStorage.setItem('language', lng);
    };
    
    return (
        <div className='dropdown m-2'>
            <button 
                className="btn aa-txt-text dropdown-toggle"
                type="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
            >
                <i className="bi bi-globe"></i>
            </button>
            <ul className="dropdown-menu dropdown-menu-end bg-text-important">
                {languages.map(lang => (
                    <li key={lang.code}>
                        <button 
                            className="dropdown-item txt-primary-important fw-bold"
                            onClick={() => changeLanguage(lang.code)}
                        >
                            <img 
                                src={lang.flag} 
                                alt={t(lang.name)} 
                                className="me-2" 
                                style={{ width: '20px', height: '15px' }}
                            />
                            {t(lang.name)}
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
}