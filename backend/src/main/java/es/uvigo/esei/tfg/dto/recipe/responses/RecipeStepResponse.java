package es.uvigo.esei.tfg.dto.recipe.responses;

public class RecipeStepResponse {
    
    private long id;
    private int stepNumber;
    private String title;
    private String description;
    private String imagePath;

    // Constructor needed for JSON conversion
    public RecipeStepResponse() {}

    public RecipeStepResponse(
        long id,
        int stepNumber,
        String title,
        String description,
        String imagePath
    ) {
        this.id = id;
        this.stepNumber = stepNumber;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
    }

    // Getters and setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

}
