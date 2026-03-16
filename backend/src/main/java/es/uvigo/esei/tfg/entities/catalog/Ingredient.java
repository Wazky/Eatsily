package es.uvigo.esei.tfg.entities.catalog;

import static java.util.Objects.requireNonNull;

public class Ingredient {
    
    private long id;
    private String name;
    private IngredientCategory category;

    // Constructor needed for JSON conversion
    public Ingredient() {}

    public Ingredient(long id) {
        this.id = id;
    }

    /**
     * Constructs a new instance of {@link Ingredient}.
     * Without category.
     * 
     * @param id identifier of the ingredient.
     * @param name name of the ingredient.
     */
    public Ingredient(long id, String name) {
        this.id = id;
        this.setName(name);
    }    

    /**
     * Constructs a new instance of {@link Ingredient}.
     * 
     * @param id identifier of the ingredient.
     * @param name name of the ingredient.
     * @param category category of the ingredient.
     */
    public Ingredient(long id, String name, IngredientCategory category) {
        this.id = id;
        this.setName(name);
        this.setCategory(category);
    }

    // Getters and setters

    // Id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // Name
    public String getName() { return name; }
    public void setName(String name) { this.name = requireNonNull(name, "Name cannot be null"); }

    // Category
    public IngredientCategory getCategory() { return category; }
    public void setCategory(IngredientCategory category) { this.category = category; }

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
        if (!(obj instanceof Ingredient))
            return false;
        Ingredient other = (Ingredient) obj;
        return id == other.id;
    }
    
    // toString method
    @Override
    public String toString() {
        return "Ingredient{id=" + id + ", name='" + name + "'}";
    }
}
