import { useTranslation } from "react-i18next";
import useIngredientSearch from "../../../hooks/catalog/useIngredientSearch";
import { useEffect, useRef, useState } from "react";

export default function IngredientAutocomplete({
    value,
    onSelect,
    onChange,
    isInvalid
}) {
    const { t } = useTranslation("recipes");
    const { ingredients, loading, search, clear } = useIngredientSearch();
    const [open, setOpen] = useState(false);
    const wrapperRef = useRef(null);

    const handleChange = (e) => {
        const val = e.target.value;
        onChange(val);
        search(val);
        setOpen(true);
    }

    const handleSelect = (ingredient) => {
        onSelect({ 
            ingredientId: ingredient.id,
            ingredientName: ingredient.name
        });
        clear();
        setOpen(false);
    };

    useEffect(() => {
        
        const handleClickOutside = (e) => {
            if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
                setOpen(false);
            }
        };
        
        document.addEventListener('mousedown', handleClickOutside);

        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const showDropdown = open && (loading || ingredients.length > 0);

    return (
        <div className="ingredient-autocomplete" ref={wrapperRef}>
            <input 
                type="text"
                className={`form-control aa-input ${isInvalid ? 'is-invalid' : ''}`}
                placeholder={t('recipeCreate.wizard.form.placeholders.ingredientName')}
                value={value}
                onChange={handleChange}
                onFocus={() => value?.length >= 2 && setOpen(true)}
                autoComplete="off"
            />

            {showDropdown && (
                <ul className="ingredient-autocomplete__dropdown">
                    
                    {loading && (
                        <li className="ingredient-autocomplete__item ingredient-autocomplete__item-loading">
                            <i className="bi bi-hourglass-split"></i>
                            {t('search.searching')}
                        </li>
                    )}

                    {!loading && ingredients.map(ingredient => (
                        <li
                            key={ingredient.id}
                            className="ingredient-autocomplete__item"
                            onMouseDown={() => handleSelect(ingredient)}
                        >
                            <span className="ingredient-autocomplete__name">
                                {ingredient.name}
                                {ingredient.categoryName && (
                                    <span className="ingredient-autocomplete__category">
                                        ({ingredient.categoryName})
                                    </span>
                                )}
                            </span>

                        </li>
                    ))}

                    {!loading && ingredients.length === 0 && (
                        <li className="ingredient-autocomplete__item ingredient-autocomplete__item-empty">
                            {t('search.noResults')}
                        </li>
                    )}

                </ul>
            )}
        </div>
    );

}