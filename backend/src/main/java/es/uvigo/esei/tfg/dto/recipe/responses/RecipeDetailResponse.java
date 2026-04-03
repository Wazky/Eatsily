package es.uvigo.esei.tfg.dto.recipe.responses;

import java.util.List;
import java.time.LocalDate;

/**
 * A DTO that represents the detailed information of a recipe, including ingredients and steps.
 * 
 * Used in endpoints that return a single recipe with all its details.
 */
public class RecipeDetailResponse {
    
    private long id;
    private String title;
    private String description;
    private String locale; // The locale of the returned translation
    private List<String> availableLocales; // List of available locales for this recipe
    private int preparationTime;
    private int cookingTime;
    private String difficulty;
    private int servings;
    private boolean isPublic;
    private boolean isLunchbox;
    private String imagePath;
    private long authorId;
    private String authorUsername;
    private long rootRecipeId; // For variations, null if it's not a variation
    private LocalDate creationDate;
    private LocalDate lastUpdateDate;
    private List<RecipeIngredientResponse> ingredients;
    private List<RecipeStepResponse> steps;

    // Constructor needed for JSON conversion
    public RecipeDetailResponse() {}

    public RecipeDetailResponse(
        long id,
        String title,
        String description,
        String locale,
        List<String> availableLocales,
        int preparationTime,
        int cookingTime,
        String difficulty,
        int servings,
        boolean isPublic,
        boolean isLunchbox,
        String imagePath,
        long authorId,
        String authorUsername,
        long rootRecipeId,
        LocalDate creationDate,
        LocalDate lastUpdateDate,
        List<RecipeIngredientResponse> ingredients,
        List<RecipeStepResponse> steps
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.locale = locale;
        this.availableLocales = availableLocales;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.difficulty = difficulty;
        this.servings = servings;
        this.isPublic = isPublic;
        this.isLunchbox = isLunchbox;
        this.imagePath = imagePath;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.rootRecipeId = rootRecipeId;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // Getters and setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public List<String> getAvailableLocales() { return availableLocales; }
    public void setAvailableLocales(List<String> availableLocales) { this.availableLocales = availableLocales; }

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

    public long getAuthorId() { return authorId; }
    public void setAuthorId(long authorId) { this.authorId = authorId; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public long getRootRecipeId() { return rootRecipeId; }
    public void setRootRecipeId(long rootRecipeId) { this.rootRecipeId = rootRecipeId; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public LocalDate getLastUpdateDate() { return lastUpdateDate; }
    public void setLastUpdateDate(LocalDate lastUpdateDate) { this.lastUpdateDate = lastUpdateDate; }

    public List<RecipeIngredientResponse> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredientResponse> ingredients) { this.ingredients = ingredients; }

    public List<RecipeStepResponse> getSteps() { return steps; }
    public void setSteps(List<RecipeStepResponse> steps) { this.steps = steps; }

}
