import { useState,   useCallback, useEffect } from "react";
import RecipeService from "../../services/recipes/RecipeService";

export default function useMyRecipes() {
    const [recipes, setRecipes] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const fetchMyRecipes = useCallback(async () => {
        setLoading(true);
        setError(null);

        try {
            const result = await RecipeService.listMyRecipes();

            if (result.success) {
                setRecipes(result.data);
                return result.data;
            } else {
                throw new Error(result.error?.message || 'Failed to load your recipes');
            }
        } catch (err) {
            setError(err.message);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchMyRecipes();
    }, [fetchMyRecipes]);

    const deleteRecipe = useCallback(async (recipeId) => {
        try {
            const result = await RecipeService.deleteRecipe(recipeId);

            if (result.success) {
                setRecipes(prev => prev.filter(r => r.id !== recipeId));
            } else {
                throw new Error(result.error?.message || 'Failed to delete recipe');
            }
        } catch (err) {
            setError(err.message);
            throw err;
        }
    }, []);

    const toggleRecipeVisibility = useCallback(async (recipeId, newVisibility) => {
        try {
            const result = await RecipeService.updateVisibility(recipeId, newVisibility);

            if (result.success) {
                setRecipes(prev => 
                    prev.map(r => r.id === recipeId ? { ...r, public: newVisibility } : r)
                );
            } else {
                throw new Error(result.error?.message || 'Failed to update recipe visibility');
            }
        } catch (err) {
            setError(err.message);
            throw err;
        } 
    }, []);

    return {
        recipes,
        loading,
        error,
        fetchMyRecipes,
        deleteRecipe,
        toggleRecipeVisibility
    };
}