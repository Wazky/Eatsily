import { useCallback, useEffect, useState } from "react";
import RecipeService from "../../services/recipes/RecipeService";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom";
import { changeLanguage } from "i18next";

export default function useRecipeDetail() {
    const { id } = useParams();
    const { i18n } = useTranslation();

    const [recipe, setRecipe] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [locale, setLocale] = useState(i18n.language);

    const fetchRecipe = useCallback(async (recipeId, requestedLocale) => {
        console.log("fetchRecipe called with:", { recipeId, requestedLocale }); // Debug log

        if (!recipeId) {
            const errorMsg = 'Recipe ID is required to fetch recipe details';
            setError(errorMsg);            
            throw new Error(errorMsg);
        }

        setLoading(true);
        setError(null);

        try {
            const result = await RecipeService.getRecipe(recipeId, requestedLocale);
            console.log("fetchRecipe result:", result); // Debug log


            if (result.success) {
                setRecipe(result.data);
                return result.data;
            } else {
                throw new Error(result.error?.message || 'Failed to load recipe');
            }
        } catch (error) {
            setError(error.message);
            return false;
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (id) {
            fetchRecipe(id, locale)
        }
    }, [id, locale, fetchRecipe]);

    const changeLocale = (newLocale) => {
        setLocale(newLocale);
    }

    const toggleRecipeVisibility = useCallback(async (recipeId, newVisibility) => {
        try {
            const result = await RecipeService.updateVisibility(recipeId, newVisibility);

            if (result.success) {
                setRecipe(prev => ({ ...prev, public: newVisibility }));
                return true;
            } else {
                throw new Error(result.error?.message || 'Failed to update recipe visibility');
            }
        } catch (err) {
            setError(err.message);
            throw err;
        }
    }, []);

    const deleteRecipe = useCallback(async (recipeId) => {
        try {
            const result = await RecipeService.deleteRecipe(recipeId);

            if (result.success) {
                setRecipe(null);
                return true;
            } else {
                throw new Error(result.error?.message || 'Failed to delete recipe');
            }
        } catch (err) {
            setError(err.message);
            throw err;
        }
    })

    return {
        recipe,
        loading,
        error,
        locale,
        fetchRecipe,
        changeLocale,
        toggleRecipeVisibility,
        deleteRecipe
    };

}