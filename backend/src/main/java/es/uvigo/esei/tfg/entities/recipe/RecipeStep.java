package es.uvigo.esei.tfg.entities.recipe;

import static java.util.Objects.requireNonNull;

/**
 * An entity that represents a step in a recipe.
 */
public class RecipeStep {
    
    private long id;
    private int stepNumber;
    private String title;
    private String description;
    private String imagePath;
    private Recipe recipe;

    // Constructor needed for the JSON conversion
    public RecipeStep() {}

    /**
     * Constructs a new instance of {@link RecipeStep}.
     * Without the title and image path.
     * 
     * @param id identifier of the recipe step.
     * @param stepNumber number of the step in the recipe.
     * @param description description of the step.
     * @param recipe associated recipe entity.
     */
    public RecipeStep(long id, int stepNumber, String description, Recipe recipe) {
        this.id = id;
        this.setStepNumber(stepNumber);
        this.setDescription(description);
        this.setRecipe(recipe);
    }

    /**
     * Constructs a new instance of {@link RecipeStep}.
     * With all the fields.
     * 
     * @param id identifier of the recipe step.
     * @param stepNumber number of the step in the recipe.
     * @param title title of the step.
     * @param description description of the step.
     * @param imagePath path to the image associated with the step.
     * @param recipe associated recipe entity.
     */
    public RecipeStep(long id, int stepNumber, String title, String description, String imagePath, Recipe recipe) {
        this.id = id;
        this.setStepNumber(stepNumber);
        this.setTitle(title);
        this.setDescription(description);
        this.setImagePath(imagePath);
        this.setRecipe(recipe);
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

    // Title
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = requireNonNull(description, "Description cannot be null"); }

    // Image path
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // Recipe
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = requireNonNull(recipe, "Recipe cannot be null"); }

    
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
