import { useTranslation } from 'react-i18next';
import esFlag from '../../assets/images/flags/es.png';
import enFlag from '../../assets/images/flags/gb.png';
import glFlag from '../../assets/images/flags/gl.png';

const languages = [
    { code: 'es', name: 'lang.es', flag: esFlag },
    { code: 'en', name: 'lang.en', flag: enFlag },
    { code: 'gl', name: 'lang.gl', flag: glFlag } 
];

export default function LanguageSwitcher({ textColorClass = "aa-txt-text" }) {
    const { t } = useTranslation("common");
    const { i18n } = useTranslation();
    
    const changeLanguage = (lng) => {
        i18n.changeLanguage(lng);
        localStorage.setItem('language', lng);
    };
    
    return (
            <div className='dropdown m-2 hover-move-up rounded-3'>
                <button 
                    className={`btn ${textColorClass} dropdown-toggle d-flex align-items-center gap-2 py-1`}
                    type="button"                    
                    data-bs-toggle="dropdown"
                    aria-expanded="false"
                >
                    <i className="bi bi-globe"></i>
                    <span className='d-none d-sm-inline'>
                        {i18n.language.toUpperCase()}
                    </span>
                    
                </button>
                <ul className="dropdown-menu dropdown-menu-end bg-text-important">
                    {languages.map(lang => (
                        <li key={lang.code}>
                            <button 
                                className={`
                                    dropdown-item
                                    d-flex 
                                    align-items-center                                           
                                    is-hoverable
                                    gap-2                                
                                    ${i18n.language === lang.code ? "fw-bold bg-primary-important" : ""}
                                `}
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