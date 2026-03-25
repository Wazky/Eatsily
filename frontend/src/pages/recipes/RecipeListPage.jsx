import { useState } from "react";
import { useTranslation } from "react-i18next";
import RecipeGrid from "../../components/recipe/list/Recipegrid";
import useRecipes from "../../hooks/recipes/useRecipes";

export default function RecipesListPage() {
    const { t } = useTranslation("recipes");
    const [isSearching, setIsSearching] = useState(false);
    const { 
        recipes: publicRecipes, 
        loading: publicLoading, 
        error: recipesError 
    } = useRecipes();

    /*
    const { 
        recipes: searchResults, 
        loading: searchLoading, 
        error: searchError
        search,
        clear    
    } = useSearchRecipes();
    */

    const recipes = isSearching ? [] : publicRecipes;
    const loading = isSearching ? false : publicLoading;

    return (
    <div className="page-container">
        
        <div className="page-header">
            <p className="page-title">
                {t("recipeList.foundRecipes", { count: recipes.length })}
            </p>
        </div>
        
        {/* <RecipeSearchBar onSearch={handleSearch} onClear={handleClear} loading={searchLoading} /> */}
        
        { loading 
            ? <p className="loading-text">{t("recipeList.loadingRecipes")}</p>
            : <RecipeGrid 
                recipes={recipes}
                emptyState={isSearching ? 'search' : 'comunity'}
            />
        }

    </div>
    );

}