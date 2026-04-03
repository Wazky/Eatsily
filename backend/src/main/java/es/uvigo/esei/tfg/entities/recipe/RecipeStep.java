package es.uvigo.esei.tfg.entities.recipe;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * An entity that represents a step in a recipe.
 */
public class RecipeStep {
    
    private long id;
    private int stepNumber;
    private String imagePath;
    private Recipe recipe;
    // Lazy loading of translations  
    private List<RecipeStepTranslation> translations = new ArrayList<>();

    // Constructor needed for the JSON conversion
    public RecipeStep() {}

    /**
     * Constructs a new instance of {@link RecipeStep}.
     * Without the title and image path.
     * 
     * @param id identifier of the recipe step.
     * @param stepNumber number of the step in the recipe.
     * @param recipe associated recipe entity.
     */
    public RecipeStep(long id, int stepNumber, Recipe recipe) {
        this.id = id;
        this.setStepNumber(stepNumber);        
        this.setRecipe(recipe);
    }

    /**
     * Constructs a new instance of {@link RecipeStep}.
     * With all the fields.
     * 
     * @param id identifier of the recipe step.
     * @param stepNumber number of the step in the recipe.
     * @param imagePath path to the image associated with the step.
     * @param recipe associated recipe entity.
     * @param translations list of translations for the step.
     */
    public RecipeStep(long id, int stepNumber, String imagePath, Recipe recipe, List<RecipeStepTranslation> translations) {
        this.id = id;
        this.setStepNumber(stepNumber);
        this.setImagePath(imagePath);
        this.setRecipe(recipe);
        this.translations = translations;
    }

    // Getters and setters

    // Id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // Step number
    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { 
        if (stepNumber < 1) {
            throw new IllegalArgumentException("Step number must be greater than 0");
        }
        this.stepNumber = stepNumber;
    }

    // Image path
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // Recipe
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = requireNonNull(recipe, "Recipe cannot be null"); }

    // Translations
    public List<RecipeStepTranslation> getTranslations() { return translations; }
    public void setTranslations(List<RecipeStepTranslation> translations) {
        if (translations == null) {
            this.translations = new ArrayList<>();
        } else {
            this.translations = translations;
        }
    }
    
    public RecipeStepTranslation getTranslation(String locale) {
        for (RecipeStepTranslation translation : translations) {
            if (translation.getLocale().equals(locale)) {
                return translation;
            }
        }
        return null; 
    }

    public RecipeStepTranslation getTranslationWithFallback(String locale) {
        RecipeStepTranslation translation = getTranslation(locale);
        if (translation != null) {
            return translation;
        }
        if (!translations.isEmpty()) {
            return translations.get(0);
        }
        return null;
    }

    // Override hashCode and equals  based on id

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Long.hashCode(id);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RecipeStep)) 
            return false;
        RecipeStep other = (RecipeStep) obj;
        return id == other.id;
    }
    

    // Override toString 

    @Override
    public String toString() {
        return "RecipeStep{id=" + id + ", stepNumber=" + stepNumber + ",  recipeId=" + recipe.getId() + "}";
    }

}
