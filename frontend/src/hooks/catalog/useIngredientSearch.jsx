import { useCallback, useEffect, useState } from "react";
import CatalogService from "../../services/catalog/CatalogService";

const DEBOUNCE_DELAY = 300; // ms

export default function useIngredientSearch() {
    const [ingredients, setIngredients] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [term, setTerm] = useState('');

    const search = useCallback((value) => {
        setTerm(value);
    }, []);

    const clear = useCallback(() => {
        setTerm('');
        setIngredients([]);        
    },[]);

    useEffect(() => {
        if (!term || term.trim().length < 2) {
            setIngredients([]);
            return;
        }

        const timer = setTimeout(async () => {
            setLoading(true);
            setError(null);
            try {
                const result = await CatalogService.searchIngredients(term.trim());

                if (result.success) {
                    setIngredients(result.data);
                } else {
                    throw new Error(result.error?.message || 'Unknown error');
                }
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        }, DEBOUNCE_DELAY);

        return () => clearTimeout(timer);
    }, [term]);

    return {
        ingredients,
        loading,
        error,
        search,
        clear,
        term
    };

}