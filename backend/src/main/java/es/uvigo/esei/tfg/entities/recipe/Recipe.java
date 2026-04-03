package es.uvigo.esei.tfg.entities.recipe;

import static java.util.Objects.requireNonNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.uvigo.esei.tfg.entities.user.User;

public class Recipe {

/**
     * Difficulty level of the recipe.
     */
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private long id;
    private int preparationTime;    // in minutes
    private int cookingTime;        // in minutes
    private int servings;
    private Difficulty difficulty;
    private boolean isPublic;       // false by default 
    private boolean isLunchbox;     // for future 
    private String imagePath;
    private User user;
    private Long rootRecipeId;      // nullable, reference to root recipe
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RecipeTranslation> translations = new ArrayList<>();


    // Constructor needed for the JSON conversion
    public Recipe() {}

    public Recipe(long id) {
        this.id = id;
    }
    
    /**
     * Constructs a new instance of {@link Recipe} with mandatory fields only.
     *
     * @param id       identifier of the recipe.
     * @param servings number of servings this recipe is based on.
     * @param user     author of the recipe.
     */
    public Recipe(long id, int servings, User user) {
        this.id = id;
        this.setServings(servings);
        this.setUser(user);
        this.isPublic = false;
        this.isLunchbox = false;
    }

    /**
     * Constructs a new instance of {@link Recipe} with all fields.
     *
     * @param id              identifier of the recipe.
     * @param preparationTime preparation time in minutes.
     * @param cookingTime     cooking time in minutes.
     * @param servings        number of servings this recipe is based on.
     * @param difficulty      difficulty level of the recipe.
     * @param isPublic        whether the recipe is visible to the community.
     * @param isLunchbox      whether the recipe is suitable as a lunchbox meal.
     * @param imagePath       relative path to the recipe image.
     * @param user            author of the recipe.
     * @param rootRecipeId    optional id of the root recipe this derives from.
     * @param createdAt       creation timestamp.
     * @param updatedAt       last modification timestamp.
     * @param translations    list of translations for this recipe.
     */
    public Recipe(
        long id,
        int preparationTime,
        int cookingTime,
        int servings,
        Difficulty difficulty,
        boolean isPublic,
        boolean isLunchbox,
        String imagePath,
        User user,
        Long rootRecipeId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<RecipeTranslation> translations
    ) {
        this.id = id;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.setServings(servings);
        this.difficulty = difficulty;
        this.isPublic = isPublic;
        this.isLunchbox = isLunchbox;
        this.imagePath = imagePath;
        this.setUser(user);
        this.rootRecipeId = rootRecipeId;
        this.setCreatedAt(createdAt);
        this.updatedAt = updatedAt;
        this.translations = translations;
    }

    // Getters and setters

    // Id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // Preparation time
    public int getPreparationTime() { return preparationTime; }
    public void setPreparationTime(int preparationTime) {
        if (preparationTime < 0) throw new IllegalArgumentException("Preparation time can't be negative");
        this.preparationTime = preparationTime;
    }

    // Cooking time
    public int getCookingTime() { return cookingTime; }
    public void setCookingTime(int cookingTime) {
        if (cookingTime < 0) throw new IllegalArgumentException("Cooking time can't be negative");
        this.cookingTime = cookingTime;
    }

    // Servings
    public int getServings() { return servings; }
    public void setServings(int servings) {
        if (servings < 1) throw new IllegalArgumentException("Servings must be at least 1");
        this.servings = servings;
    }

    // Difficulty
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    // Public
    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    // Lunchbox
    public boolean isLunchbox() { return isLunchbox; }
    public void setLunchbox(boolean isLunchbox) { this.isLunchbox = isLunchbox; }

    // Image path
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // User
    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = requireNonNull(user, "User can't be null");
    }

    // Root recipe id
    public Long getRootRecipeId() { return rootRecipeId; }
    public void setRootRecipeId(Long rootRecipeId) { this.rootRecipeId = rootRecipeId; }

    // Created timestamp
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = requireNonNull(createdAt, "Created at can't be null");
    }

    // Updated timestamp
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<RecipeTranslation> getTranslations() { return translations; }
    public void setTranslations(List<RecipeTranslation> translations) { 
        if (translations == null) 
            this.translations = new ArrayList<>();    
        else
            this.translations = translations;
    }

    public void addTranslation(RecipeTranslation translation) {
        this.translations.add(requireNonNull(translation, "Translation can't be null"));
    }

    /**
     * Returns the translation for the specified locale, or the first available translation if not found.
     * 
     * @param code the locale code (e.g. "en", "es") to look for.
     * @return the {@link RecipeTranslation} matching the locale, or null if not found.
     */
    public RecipeTranslation getTranslationByLocaleOrFallback(String code) {
        RecipeTranslation translation = getTranslationByLocale(code);
        if (translation == null && !translations.isEmpty()) {
            return translations.get(0); // Return the first translation as default
        }

        return translation;
    }

    /**
     * Returns the translation for the specified locale, or null if not found.
     * 
     * @param locale the locale code (e.g. "en", "es") to look for.
     * @return the {@link RecipeTranslation} matching the locale, or null if not found.
     */
    public RecipeTranslation getTranslationByLocale(String locale) {
        for (RecipeTranslation translation : translations) {
            if (translation.getLocale().equals(locale)) {
                return translation;
            }
        }
        return null;
    }

    /**
     * Returns a list of all available locale codes for this recipe's translations.
     * 
     * @return a list of locale codes.
     */
    public List<String> getAvailableLocales() {
        List<String> locales = new ArrayList<>();
        for (RecipeTranslation translation : translations) {
            locales.add(translation.getLocale());
        }
        return locales;
    }

    /**
     * Returns the total time (preparation + cooking) in minutes.
     *
     * @return total time in minutes.
     */
    public int getTotalTime() {
        return preparationTime + cookingTime;
    }

    // Override hashCode and equals based on id

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Long.hashCode(id);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Recipe))
            return false;
        Recipe other = (Recipe) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "Recipe{id=" + id + "', isPublic=" + isPublic + "}";
    }
}
