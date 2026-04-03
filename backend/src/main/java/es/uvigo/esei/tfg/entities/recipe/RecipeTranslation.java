package es.uvigo.esei.tfg.entities.recipe;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;

public class RecipeTranslation {
    
    private long recipeId;
    private String locale;
    private String title;
    private String description;

    // Constructor needed for the JSON conversion
    public RecipeTranslation() {}

    public RecipeTranslation(long recipeId, String locale, String title, String description) {
        this.recipeId = recipeId;
        this.locale = locale;
        this.title = title;
        this.description = description;
    }

    public long getRecipeId() { return recipeId; }
    public void setRecipeId(long recipeId) { 
        if (recipeId <= 0) throw new IllegalArgumentException("Recipe ID must be positive");
    
        this.recipeId = recipeId; 
    }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = requireNonNull(locale, "Locale cannot be null"); }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = requireNonNull(title, "Title cannot be null"); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Long.hashCode(recipeId);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        if (obj == null) 
            return false;
        if (!(obj instanceof RecipeTranslation)) 
            return false;
        RecipeTranslation other = (RecipeTranslation) obj;
        return recipeId == other.recipeId && locale.equals(other.locale);
    }

    @Override
    public String toString() {
        return "RecipeTranslation{recipeId=" + recipeId + ", locale='" + locale + "', title='" + title + "'}";
    }

}
