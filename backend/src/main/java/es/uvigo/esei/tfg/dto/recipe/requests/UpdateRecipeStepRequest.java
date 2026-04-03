package es.uvigo.esei.tfg.dto.recipe.requests;

public class UpdateRecipeStepRequest {

    private Integer stepNumber;
    private String imagePath;  

    // Constructor needed for JSON conversion
    public UpdateRecipeStepRequest() {}

    public UpdateRecipeStepRequest(Integer stepNumber, String imagePath) {
        this.stepNumber = stepNumber;
        this.imagePath  = imagePath;
    }

    public Integer getStepNumber() { return stepNumber; }
    public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}