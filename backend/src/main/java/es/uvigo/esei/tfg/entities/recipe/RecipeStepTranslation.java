package es.uvigo.esei.tfg.entities.recipe;


import static java.util.Objects.requireNonNull;

public class RecipeStepTranslation {
    
    private long stepId;
    private String locale;
    private String title;
    private String description;

    // Constructor needed for the JSON conversion
    public RecipeStepTranslation() {}

    public RecipeStepTranslation(long stepId, String locale, String title, String description) {
        this.stepId = stepId;
        this.locale = locale;
        this.title = title;
        this.description = description;
    }

    // Getters and setters

    public long getStepId() { return stepId; }
    public void setStepId(long stepId) { this.stepId = stepId; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = requireNonNull(locale, "Locale cannot be null"); }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = requireNonNull(description, "Description cannot be null"); }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Long.hashCode(stepId);
        result = prime * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RecipeStepTranslation)) 
            return false;
        RecipeStepTranslation other = (RecipeStepTranslation) obj;
        return stepId == other.stepId && locale.equals(other.locale);
    }

    @Override
    public String toString() {
        return "RecipeStepTranslation{stepId=" + stepId + ", locale='" + locale + "', title='" + title + "'}";
    }

}
