import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useNavigate } from "react-router-dom";
import { ROUTES } from "../../constants/routes";
import useRecipeDetail from "../../hooks/recipes/useRecipeDetail";
import { DifficultyBadge, LunchBoxBadge, PlaceholderIcon, PrivateBadge } from "../../components/recipe/list/RecipeCardComponents";
import { upperFirst } from "../../utils/helpers";
import useAuthContext from "../../context/AuthContext";

const TABS = {
    INGREDIENTS: 'ingredients',
    STEPS: 'steps',
}

export default function RecipeDetailPage() {
    const { t } = useTranslation("recipes");
    const navigate = useNavigate();

    const { recipe, loading, error, locale, changeLocale, toggleRecipeVisibility, deleteRecipe } = useRecipeDetail();
    
    const { user } = useAuthContext();  
    const isAuthor = recipe?.authorUsername === user?.username;

    const [activeTab, setActiveTab] = useState(TABS.INGREDIENTS);
    const [servings, setServings] = useState(null);

    const baseServings = recipe?.servings ?? 1;
    const currentServings = servings ?? baseServings;

    const changeServings = (delta) => {
        const next = currentServings + delta;
        if (next >= 1) {
            setServings(next);
        }
    };

    if (loading) {
        return (
        <div className="page-container">
            <p className="loading-text">{t("recipeDetail.loading")}</p>
        </div>
        );
    }

    if (error || !recipe) {
        return (
        <div className="page-container">
            <p className="error-text">{t("recipeDetail.notFound")}</p>
        </div>
        );
    }

    const handleToggleVisibility = async () => {
        if (window.confirm(recipe.public ? t("recipeDetail.makePrivateConfirmation") : t("recipeDetail.makePublicConfirmation"))) {
            try {
                await toggleRecipeVisibility(recipe.id, !recipe.public);
            } catch (err) {
                alert(t("recipeDetail.toggleVisibilityError"));
            }
        }
    };

    const handleDelete = async () => {
        if (window.confirm(t("recipeDetail.deleteConfirmation"))) {
            try {
                await deleteRecipe(recipe.id);
                navigate(ROUTES.RECIPES); //Adjust navigation after deletion as needed
            } catch (err) {
                alert(t("recipeDetail.deleteError"));
            }
        }
    };

    const hasMultipleLocales = recipe.availableLocales?.length > 1;

    return (
    <div className="page-container">
        <div className="recipe-detail">

            {/* Action Bar */}
            <div className="recipe-detail__action-bar">
                
                {/* Create a component for the back button (with logic) */}
                <button
                    type="button"
                    className="btn aa-ghost"
                    onClick={() => navigate(-1)}
                >
                    <i className="bi bi-arrow-left me-2"/>
                    {t("recipeDetail.backButton")}
                </button>

            </div> 

            <div className="recipe-detail__header">

                {/* Recipe Image */}
                <div className="recipe-detail__image">
                    {recipe.imagePath
                        ? <img src={recipe.imagePath} alt={recipe.name} className="img-fluid rounded-3" />
                        : <PlaceholderIcon/>
                    }
                </div>

                <div className="recipe-detail__info-actions">

                    {/* Basic Recipe Information */}
                    <div className="recipe-detail__info">

                        {/* Locale Selector */}
                        {hasMultipleLocales && (
                            <div className="recipe-detail__locales">                            
                                {recipe.availableLocales.map(lcl => (
                                    <LocaleButton
                                        key={lcl}
                                        locale={lcl}
                                        changeLocale={changeLocale}
                                        active={locale === lcl}
                                    />
                                ))}
                            </div>                        
                        )}


                        {/* Title and Description */}
                        <h1 className="recipe-detail__title">{recipe.title}</h1>
                        {recipe.description  
                            ?   <p className="recipe-detail__description">
                                    {recipe.description}
                                </p>
                            :   <p className="recipe-detail__description recipe-detail__description--empty">
                                    {t("recipeDetail.noDescription")}
                                </p>
                        }

                        {/* Badges */}
                        <div className="recipe-detail__badges">
                            
                            {recipe.difficulty && (
                                <DifficultyBadge difficulty={recipe.difficulty} />
                            )}
                            
                            {recipe.lunchbox && (
                                <LunchBoxBadge />
                            )}

                            {!recipe.public && (
                                <PrivateBadge />
                            )}

                        </div>

                        {/* Meta Information */}
                        <div className="recipe-detail__meta">
                            
                            <div className="recipe-detail__meta-item">
                                <i className="bi bi-clock" />
                                <span>
                                    {t("recipeDetail.metadata.prepTime", { time: recipe.preparationTime })}
                                </span>
                            </div>

                            {recipe.cookingTime && (
                                <div className="recipe-detail__meta-item">
                                    <i className="bi bi-clock" />
                                    <span>
                                        {t("recipeDetail.metadata.cookingTime", { time: recipe.cookingTime })}
                                    </span>
                                </div>                        
                            )}

                            <div className="recipe-detail__meta-item">
                                <i className="bi bi-clock-history" />
                                <span>
                                    {t("recipeDetail.metadata.totalTime", { time: recipe.preparationTime + recipe.cookingTime })}
                                </span>
                            </div>

                        </div>

                        {/* Servings Adjuster */}
                        <div className="recipe-detail__servings">
                            
                            <span className="recipe-detail__servings-label">
                                {t("recipeDetail.metadata.servings")}
                            </span>
                            
                            <div className="servings-adjuster">
                            
                                <button
                                    type="button"
                                    className="servings-adjuster__btn"
                                    onClick={() => changeServings(-1)}
                                    disabled={currentServings <= 1}
                                >
                                    <i className="bi bi-dash"></i>
                                </button>
                            
                                <span className="servings-adjuster__value">
                                    {currentServings}
                                </span>

                                <button
                                    type="button"
                                    className="servings-adjuster__btn"
                                    onClick={() => changeServings(1)}
                                >
                                    <i className="bi bi-plus"></i>
                                </button>

                            </div>
                        </div>

                        {/* Author */}
                        <Link className="recipe-detail__author" to={ROUTES.USER.replace(":username", recipe.authorUsername)}>
                            <div className="author-avatar">
                                {recipe.authorUsername?.slice(0, 2).toUpperCase()}
                            </div>
                            <span>{recipe.authorUsername}</span>
                        </Link>

                    </div>

                    {/* Author Actions */}
                    {isAuthor && (                    
                        <div className="recipe-detail__actions"> 
                            <button
                                type="button"
                                className="btn aa-secondary aa-outline"
                                onClick={() => navigate(ROUTES.RECIPE_EDIT.replace(':id', recipe.id))}
                            >
                                <i className="bi bi-pencil me-2" />
                                {t("recipeDetail.actions.edit")}
                            </button>

                            {/* Add toggle visibility logic */}
                            <button
                                type="button"
                                className={`btn ${recipe.public ? 'aa-text' : 'aa-accent'}`}
                                onClick={handleToggleVisibility} 
                            >
                                <i className={`bi ${recipe.public ? 'bi-lock' : 'bi-globe'} me-2`} />
                                {recipe.public
                                    ? t("recipeDetail.actions.makePrivate")
                                    : t("recipeDetail.actions.makePublic")
                                }
                            </button>

                            {/* Add delete logic */}
                            <button
                                type="button"
                                className="btn aa-danger aa-outline"
                                onClick={handleDelete}
                            >
                                <i className="bi bi-trash me-2"/>
                                {t("recipeDetail.actions.delete")}
                            </button>
                        </div>
                    )}

                </div>
            
            
            </div>

            {/* Tabs for Ingredients and Steps */}
            <div className="recipe-detail__tabs-section">

                {/* Tabs */}
                <div className="recipe-detail__tabs">
                    <button
                        type="button"
                        className={`recipe-detail__tab ${activeTab === TABS.INGREDIENTS ? 'recipe-detail__tab--active' : ''}`}
                        onClick={() => setActiveTab(TABS.INGREDIENTS)}
                    >
                        <i className="bi bi-list-ul me-2" />
                        {t("recipeDetail.ingredientsTab.title")}
                        <span className="tab-count">
                            {recipe.ingredients?.length ?? 0}
                        </span>
                    </button>
                    <button
                        type="button"
                        className={`recipe-detail__tab ${activeTab === TABS.STEPS ? 'recipe-detail__tab--active' : ''}`}
                        onClick={() => setActiveTab(TABS.STEPS)}
                    >
                        <i className="bi bi-list-ol me-2" />
                        {t("recipeDetail.stepsTab.title")}
                        <span className="tab-count">{recipe.steps?.length ?? 0}</span>
                    </button>                
                </div>

                {/* Ingredients Tab */}
                {activeTab === TABS.INGREDIENTS && (
                    <ul className="ingredient-list">
                        {/* Header Row */}
                        <li className="ingredient-list__item">
                            
                            {/* Quantity & Unit */}
                            <span className="ingredient-list__header">
                                {t("ingredientLabels.quantity")}
                            </span>

                            {/* Ingredient Name */}
                            <span className="ingredient-list__header">
                                {t("ingredientLabels.name")}                                
                            </span>

                            {/* Notes */}
                            <span className="ingredient-list__header">
                                {t("ingredientLabels.notes")}      
                            </span>        
                            
                        </li>

                        {recipe.ingredients?.map(ingredient => (
                            <IngredientItem 
                                key={ingredient.id}
                                ingredient={ingredient}
                                currentServings={currentServings}
                                baseServings={baseServings}
                            />
                        ))}
                    </ul>
                )}

                {/* Steps Tab */}
                {activeTab === TABS.STEPS && (
                    <ol className="step-list">
                        {recipe.steps?.map((step, index) => (
                            <StepItem 
                                key={index}
                                step={step}
                                index={index}   
                            />
                        ))}
                    </ol>
                )}

            </div>

        </div>
    </div>
    );
}

export function LocaleButton({ locale, changeLocale, active }) {
    const { t } = useTranslation("recipes");
    const { t: tCommon } = useTranslation("common");

    return (
    <button
        key={locale}
        type="button"
        className={`locale-pill ${active ? 'locale-pill--active' : ''}`}
        onClick={() => changeLocale(locale)}
    >
        {tCommon("lang." + locale).toUpperCase()}
    </button>
    );
}

export function IngredientItem({ ingredient, currentServings, baseServings }) {
    const { t } = useTranslation("recipes");
    const scaleQuantity = (baseQuantity) => {
        if (!baseQuantity) return null;

        const scaled = (baseQuantity * currentServings) / baseServings;
        return Number.isInteger(scaled) ? scaled : scaled.toFixed(2);
    };

    return (
    <li className="ingredient-list__item">


        {/* Quantity & Unit */}
        <span className="ingredient-list__amount">
            {scaleQuantity(ingredient.quantity)}{ingredient.unitAbbreviation} ({ingredient.unitName})
        </span>

        {/* Ingredient Name */}
        <span className="ingredient-list__name">
            {upperFirst(ingredient.ingredientName)}
            
        </span>

        {/* Notes */}
        <span className="ingredient-list__notes">
            {ingredient.notes || "------"}           
        </span>        
        
        
    </li>
    );
}

export function StepItem({ step, index }) {

    return (
    <li className="step-list__item">
        <div className="step-list__number">
            {index + 1}
        </div>
        <div className="step-list__content">
            {step.title && (
                <h4 className="step-list__title">
                    {step.title}
                </h4>
            )}
            <p className="step-list__description">
                {step.description}
            </p>
        </div>
    </li>
    );
}