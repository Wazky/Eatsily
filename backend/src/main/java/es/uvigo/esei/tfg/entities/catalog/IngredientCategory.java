package es.uvigo.esei.tfg.entities.catalog;

import static java.util.Objects.requireNonNull;

/**
 * An entity that represents a category of ingredients.
 */
public class IngredientCategory {
    
    private long id;
    private String name;
    private String description;

    // Constructor needed for JSON conversion    
    public IngredientCategory() {}

    /**
     * Constructs a new instance of {@link IngredientCategory}.
     * without description.
     * 
     * @param id identifier of the ingredient category.
     * @param name name of the ingredient category.
     */
    public IngredientCategory(long id, String name) {
        this.id = id;
        this.setName(name);
    }

    /**
     * Constructs a new instance of {@link IngredientCategory}.
     * 
     * @param id identifier of the ingredient category.
     * @param name name of the ingredient category.
     * @param description description of the ingredient category.
     */
    public IngredientCategory(long id, String name, String description) {
        this.id = id;
        this.setName(name);
        this.setDescription(description);
    }

    // Getters and setters

    // Id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // Name
    public String getName() { return name; }
    public void setName(String name) { this.name = requireNonNull(name, "Name can't be null"); }

    // Description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    
    // hashCode and equals based on id

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
        if (!(obj instanceof IngredientCategory))
            return false;
        IngredientCategory other = (IngredientCategory) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "IngredientCategory {id=" + id + ", name='" + name + "'}";
    }

}
