import { useCallback, useEffect, useState } from "react";
import RecipeService from "../../services/recipes/RecipeService";

export default function useRecipes() {
    const[recipes, setRecipes] = useState([]);
    const[loading, setLoading] = useState(false);
    const[error, setError] = useState(null);

    const fetchRecipes = useCallback(async () => {
        setLoading(true);
        setError(null);

        try {
            const result = await RecipeService.listRecipes()
            
            if (result.success) {
                setRecipes(result.data);
                return result.data;
            } else {
                throw new Error(result.error?.message || 'Failed to load recipes');
            }
        } catch (error) {
            setError(error.message);
            throw error;
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchRecipes();
    }, [fetchRecipes]);

    return {
        recipes,
        loading,
        error,
        fetchRecipes
    };
}