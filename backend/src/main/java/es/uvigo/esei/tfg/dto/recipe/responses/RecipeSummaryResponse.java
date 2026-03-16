package es.uvigo.esei.tfg.dto.recipe.responses;

import java.time.LocalDate;

/**
 * A DTO that represents a summary of a recipe.
 * 
 * Used in list endpoints to avoid sending ingredients 
 * and steps data when not needed.
 */
public class RecipeSummaryResponse {
    
    private long id;
    private String title;
    private String description;
    private int preparationTime;
    private int cookingTime;
    private String difficulty;
    private int servings;
    private boolean isPublic;
    private boolean isLunchbox;
    private String imagePath;
    private String authorUsername;
    private LocalDate creationDate;

    // Constructor needed for JSON conversion
    public RecipeSummaryResponse() {}

    public RecipeSummaryResponse(
        long id,
        String title,
        String description,
        int preparationTime,
        int cookingTime,
        String difficulty,
        int servings,
        boolean isPublic,
        boolean isLunchbox,
        String imagePath,
        String authorUsername,
        LocalDate creationDate
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.difficulty = difficulty;
        this.servings = servings;
        this.isPublic = isPublic;
        this.isLunchbox = isLunchbox;
        this.imagePath = imagePath;
        this.authorUsername = authorUsername;
        this.creationDate = creationDate;
    }

    // Getters and setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPreparationTime() { return preparationTime; }
    public void setPreparationTime(int preparationTime) { this.preparationTime = preparationTime; }

    public int getCookingTime() { return cookingTime; }
    public void setCookingTime(int cookingTime) { this.cookingTime = cookingTime; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public boolean isLunchbox() { return isLunchbox; }
    public void setLunchbox(boolean isLunchbox) { this.isLunchbox = isLunchbox; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

}
