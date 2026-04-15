import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../constants/routes";
import RecipeGrid from "../../components/recipe/list/Recipegrid";
import useMyRecipes from "../../hooks/recipes/useMyRecipes";

export default function MyRecipesPage() {
    const { t } = useTranslation("recipes");
    const navigate = useNavigate();
    const {
        recipes,
        loading,
        error,
        deleteRecipe,
        toggleRecipeVisibility
    } = useMyRecipes();

    const handleDelete = async (recipeId) => {
        if (!window.confirm("Are you sure you want to delete this recipe?")) return;
        await deleteRecipe(recipeId);
    };

    const handleToggleVisibility = async (recipeId, currentVisibility) => {
        await toggleRecipeVisibility(recipeId, !currentVisibility);
    };

    const getActions = (recipe) => [
        {
            label: t("recipeList.recipeGrid.recipeCard.actions.edit"),
            icon: "bi bi-pencil",
            onClick: () => navigate(ROUTES.RECIPE_EDIT.replace(':id', recipe.id))
        },
        {
            label: recipe.public ? t("recipeList.recipeGrid.recipeCard.actions.makePrivate") : t("recipeList.recipeGrid.recipeCard.actions.makePublic"),
            icon: recipe.public ? "bi bi-lock" : "bi bi-globe",
            onClick: () => handleToggleVisibility(recipe.id, recipe.public)
        },
        {
            label: t("recipeList.recipeGrid.recipeCard.actions.delete"),
            icon: "bi bi-trash",
            danger: true,
            onClick: () => handleDelete(recipe.id)
        }
    ];

    return (
    <div className="page-container">

        {recipes.length > 0 && (
        <div className="page-header">

            <p className="page-title">
                {t("recipeList.myRecipes", { count: recipes.length })}
            </p>

            <button className="btn aa-primary aa-lg" onClick={() => navigate(ROUTES.RECIPE_CREATE)}>
                {t("createNewRecipe")}
            </button>    
        </div>                
        )}

        { error && <p className="error-text">{error}</p>}

        { loading
            ? <p className="loading-text">{t("loadingYourRecipes")}</p>
            : <RecipeGrid 
                recipes={recipes}
                getActions={getActions}
                emptyState="myRecipes"
            />
        }

    </div>
    );

}