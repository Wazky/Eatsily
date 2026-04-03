package es.uvigo.esei.tfg.dto.recipe.requests;

public class UpdateRecipeStepTranslationRequest {

    private String title;       
    private String description; 

    // Constructor needed for JSON conversion
    public UpdateRecipeStepTranslationRequest() {}

    public UpdateRecipeStepTranslationRequest(String title, String description) {
        this.title       = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}