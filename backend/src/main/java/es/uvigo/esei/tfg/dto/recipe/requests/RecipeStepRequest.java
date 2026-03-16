package es.uvigo.esei.tfg.dto.recipe.requests;

public class RecipeStepRequest {
    
    private Integer stepNumber;
    private String title;
    private String description;

    // Constructor needed for JSON conversion
    public RecipeStepRequest() {}

    public RecipeStepRequest(
        Integer stepNumber,
        String title,
        String description
    ) {
        this.stepNumber = stepNumber;
        this.title = title;
        this.description = description;
    }
    
    // Getters and setters

    public Integer getStepNumber() { return stepNumber; }
    public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

}
