package es.uvigo.esei.tfg.dto.recipe.requests;

public class AddRecipeStepRequest {

    private Integer stepNumber;
    private String locale;       
    private String title;      
    private String description; 

    // Constructor needed for JSON conversion
    public AddRecipeStepRequest() {}

    public AddRecipeStepRequest(Integer stepNumber, String locale, String title, String description) {
        this.stepNumber  = stepNumber;
        this.locale      = locale;
        this.title       = title;
        this.description = description;
    }

    public Integer getStepNumber() { return stepNumber; }
    public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
}