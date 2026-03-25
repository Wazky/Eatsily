import { useTranslation } from "react-i18next";
import { PlaceholderIcon, ClockIcon, ServingsIcon, LockIcon, DifficultyBadge } from "./RecipeCardComponents";

/**
 * Component for displaying a recipe card in the recipe list.
 * Shows recipe image, title, description, metadata (time and servings), author and privacy status.
 * Also includes badges for difficulty and lunchbox status.
 * 
 * @param {Object} recipe - The recipe object containing all necessary data to display.
 * @param {Function} onDelete - Optional callback function to handle recipe deletion.
 * @param {Function} onToggleVisibility - Optional callback function to handle toggling recipe visibility.
 * @returns {JSX.Element} The rendered recipe card component.
 */
export default function RecipeCard({ recipe, onDelete, onToggleVisibility }) {
    const { t } = useTranslation("recipes");
    const initials = recipe.authorUsername.slice(0,2).toUpperCase();
    const totalTime = recipe.preparationTime + recipe.cookingTime;

    return (
    <div className="recipe-card shadow-sm">

        {/* Image Section: Recipe Image and Badges */}
        <div className="recipe-card__image">

            {/* Recipe image or placeholder */}
            {recipe.imagePath 
                ? <img src={recipe.imagePath} alt={recipe.title} />
                : <PlaceholderIcon />
            }

            {/* Badges (Difficulty, Lunchbox) */}
            <div className="recipe-card__badges" >
                {recipe.difficulty && <DifficultyBadge difficulty={recipe.difficulty} />}
                {recipe.lunchbox && <span className="badge badge-lunchbox">{t("recipeLabels.lunchbox")}</span>}
            </div>
            
        </div>

        {/* Body Section: Title, Description and Metadata */}
        <div className="recipe-card__body">
        
            <h3 className="recipe-card__title">{recipe.title}</h3>
            <p className="recipe-card__description">{recipe.description}</p>
        
            {/* Metadata (Total Time and Servings) */}
            <div className="recipe-card__meta">
                <span className="meta-item">
                    <ClockIcon />
                    {t("recipeList.recipeGrid.recipeCard.metadata.totalTime", { time: totalTime })}
                </span>
                <span className="meta-item">
                    <ServingsIcon />
                    {t("recipeList.recipeGrid.recipeCard.metadata.servings", { count: recipe.servings })}
                </span>
            </div>            
        
        </div>
        
        {/* Footer Section: Author and Privacy Status */}
        <div className="recipe-card__footer">
            
            <div className="recipe-card__author">
                <div className="author-avatar">{initials}</div>
                {recipe.authorUsername}
            </div>
            
            {!recipe.public && 
                <span className="recipe-card__private">
                    <LockIcon />
                    {t("recipeList.recipeGrid.recipeCard.privacy.private")}
                </span>                
            }

        </div>
    
    </div>
    );
}