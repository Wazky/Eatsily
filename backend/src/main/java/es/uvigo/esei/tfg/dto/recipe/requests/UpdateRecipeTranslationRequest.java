package es.uvigo.esei.tfg.dto.recipe.requests;

import java.util.List;

public class UpdateRecipeTranslationRequest {
    
    private String title;
    private String description;
    private List<RecipeStepTranslationRequest> steps;

    public UpdateRecipeTranslationRequest() {}

    public UpdateRecipeTranslationRequest(String title, String description, List<RecipeStepTranslationRequest> steps) {
        this.title = title;
        this.description = description;
        this.steps = steps;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<RecipeStepTranslationRequest> getSteps() { return steps; }
    public void setSteps(List<RecipeStepTranslationRequest> steps) { this.steps = steps; }

}
