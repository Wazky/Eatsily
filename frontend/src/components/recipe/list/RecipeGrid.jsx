import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../../constants/routes";
import RecipeCard from "./RecipeCard";

/**
 * Component to display a grid of recipes. 
 * It receives an array of recipe objects and renders a RecipeCard for each one.
 * If the array is empty, it shows an appropriate empty state message.
 * 
 * @param {Array} recipes - Array of recipe objects to display in the grid.
 * @param {Function} onDelete - Callback function to handle recipe deletion (optional).
 * @param {Function} onToggleVisibility - Callback function to handle toggling recipe visibility (optional).
 * @param {string} emptyState - Type of empty state to show when there are no recipes (default: 'search').
 * @returns {JSX.Element} The rendered recipe grid or empty state.
 */
export default function RecipeGrid({ 
    recipes, 
    onDelete, 
    onToggleVisibility, 
    emptyState = 'search',
}) {

    if (!recipes || recipes.length === 0) {
        return <EmptyState type={emptyState} />;
    }

    return (
    <div className="recipe-grid">
        { recipes.map(recipe => (
            <RecipeCard 
                key={recipe.id}
                recipe={recipe} 
                onDelete={onDelete} 
                onToggleVisibility={onToggleVisibility} 
            />
        ))}
    </div>
    );
}

/**
 *  EmptyStates for RecipeGrid when there are no recipes to show.
 *  The type prop determines which message and action to show.
 *  Types:
 *   - comunity: No recipes in the community (default state for recipe list)
 *   - search: No results for the search query
 *   - myRecipes: User has no recipes, with a call to action to create one (default state for my recipes)
 */
const EMPTY_STATES = {
    comunity: {
        title: "recipeList.recipeGrid.emptyGrid.comunity.title",
        subtitle: "recipeList.recipeGrid.emptyGrid.comunity.subtitle",
        subtitle2: "recipeList.recipeGrid.emptyGrid.comunity.subtitle2",
        action: null
    },
    search: {
        title: "recipeList.recipeGrid.emptyGrid.search.title",
        subtitle: "recipeList.recipeGrid.emptyGrid.search.subtitle",
        action: null
    },
    myRecipes: {
        title: "recipeList.recipeGrid.emptyGrid.myRecipes.title",
        subtitle: "recipeList.recipeGrid.emptyGrid.myRecipes.subtitle",
        action: {
            label: "recipeList.recipeGrid.emptyGrid.myRecipes.action",
            route: ROUTES.RECIPE_CREATE
        }
    }
}

/**
 * Component to show when there are no recipes to display in the grid.
 * 
 * @param {string} type - The type of empty state to display (comunity, search, myRecipes) 
 * @returns {JSX.Element} JSX element with the appropriate message and action based on the type prop.
 */
function EmptyState({ type }) {
    const { t } = useTranslation("recipes");
    const navigate = useNavigate();
    const state = EMPTY_STATES[type] || EMPTY_STATES.search;

    return (
    <div className="recipe-grid__empty">
        
        {/* Icon can be changed based on the type if desired, for now it's the same for all */}
        <div className="recipe-grid__empty-icon">
            <i className="bi bi-info-circle"></i>                
        </div>        
        
        {/* Title and subtitle from the EMPTY_STATES configuration */}
        <p className="recipe-grid__empty-title">{t(state.title)}</p>
        <p className="recipe-grid__empty-sub">{t(state.subtitle)}</p>
        {state.subtitle2 && <p className="recipe-grid__empty-sub">{t(state.subtitle2)}</p>}
        
        {/* If there's an action defined for this state, show a button to navigate to the specified route */}
        {state.action && (
            <button className="btn aa-primary aa-lg mt-2" onClick={() => navigate(state.action.route)}>
                {t(state.action.label)}
            </button>
        )}
    </div>
    );
}