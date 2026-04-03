package es.uvigo.esei.tfg.dto.recipe.requests;

public class RecipeStepTranslationRequest {
    
    private long stepId;
    private String title;
    private String description;

    public RecipeStepTranslationRequest() {}

    public RecipeStepTranslationRequest(
        long stepId,
        String title,
        String description
    ) {
        this.stepId = stepId;
        this.title = title;
        this.description = description;
    }

    public long getStepId() { return stepId; }
    public void setStepId(long stepId) { this.stepId = stepId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
