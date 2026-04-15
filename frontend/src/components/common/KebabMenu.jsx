import { useEffect, useState, useRef } from "react";

export default function KebabMenu({ actions = [] }) {
    const [open, setOpen] = useState(false);
    const menuRef = useRef();

    useEffect(() => {
        if (!open) return;
        
        const handleClickOutside = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);

    }, [open]);

    // If no actions provided, do not render the menu 
    if (actions.length === 0) return null;

    const handleToggle = (e) => {
        e.stopPropagation();
        setOpen(prev => !prev);
    };

    const handleActionClick = (e, action) => {
        e.stopPropagation();
        setOpen(false);
        action.onClick();
    };

    return (
    <div className="kebab-menu" ref={menuRef}>
        
        <button
            type="button"
            className="kebab-menu__trigger"
            onClick={handleToggle}
            aria-label="More options"
            aria-expanded={open}
        >
            <i className="bi bi-three-dots-vertical" />
        </button>

        {open && (
            <div className="kebab-menu__dropdown" role="menu">
                {actions.map((action, index) => (
                    <KebabMenuItem
                        key={index}
                        index={index}
                        action={action}
                        handleActionClick={handleActionClick}
                    />
                ))}
            </div>                  
        )}
    </div>
    );

}

export function KebabMenuItem({ index, action, handleActionClick }) {

    return (
    <li key={index}>
        <button
            type="button"
            className={`kebab-menu__item ${action.danger ? 'kebab-menu__item--danger' : ''}`}
            onClick={(e) => handleActionClick(e, action)}
        >
            {action.icon && <i className={`${action.icon} me-2`}></i>}
            {action.label}
        </button>
    </li>
    );
}