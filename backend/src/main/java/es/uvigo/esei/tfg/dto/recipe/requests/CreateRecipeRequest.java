package es.uvigo.esei.tfg.dto.recipe.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for creating a new recipe.
 */
public class CreateRecipeRequest {
    
    // Basic recipe data
    private String title;
    private String description;
    private Integer preparationTime;
    private Integer cookingTime;
    private String difficulty;
    private Integer servings;

    @JsonProperty("isPublic")
    private Boolean isPublic;
    @JsonProperty("isLunchbox")
    private Boolean isLunchbox;

    private List<RecipeIngredientRequest> ingredients;
    private List<RecipeStepRequest> steps;

    // Constructor needed for JSON conversion
    public CreateRecipeRequest() {}

    public CreateRecipeRequest(
        String title,
        String description,
        Integer preparationTime,
        Integer cookingTime,
        String difficulty,
        Integer servings,
        Boolean isPublic,
        Boolean isLunchbox,
        List<RecipeIngredientRequest> ingredients,
        List<RecipeStepRequest> steps
    ) {
        this.title = title;
        this.description = description;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.difficulty = difficulty;
        this.servings = servings;
        this.isPublic = isPublic;
        this.isLunchbox = isLunchbox;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // Getters and setters

    public String getTitle() { return title;}
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPreparationTime() { return preparationTime; }
    public void setPreparationTime(Integer preparationTime) { this.preparationTime = preparationTime; }

    public Integer getCookingTime() { return cookingTime; }
    public void setCookingTime(Integer cookingTime) { this.cookingTime = cookingTime; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }

    public Boolean isPublic() { return isPublic; }
    public void setPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Boolean isLunchbox() { return isLunchbox; }
    public void setLunchbox(Boolean isLunchbox) { this.isLunchbox = isLunchbox; }

    public List<RecipeIngredientRequest> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredientRequest> ingredients) { this.ingredients = ingredients; }

    public List<RecipeStepRequest> getSteps() { return steps; }
    public void setSteps(List<RecipeStepRequest> steps) { this.steps = steps; }

}
