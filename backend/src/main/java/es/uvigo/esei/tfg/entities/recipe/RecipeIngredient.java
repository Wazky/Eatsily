package es.uvigo.esei.tfg.entities.recipe;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

import es.uvigo.esei.tfg.entities.catalog.Ingredient;
import es.uvigo.esei.tfg.entities.catalog.MeasurementUnit;

/**
 * An entity that represents an ingredient in a recipe, including the quantity and measurement unit.
 */
public class RecipeIngredient {
    
    private long id;
    private BigDecimal quantity;
    private String notes;
    private Ingredient ingredient;
    private MeasurementUnit unit;
    private Recipe recipe;

    // Constructor needed for the JSON conversion
    public RecipeIngredient() {}

    public RecipeIngredient(long id, BigDecimal quantity, Ingredient ingredient, MeasurementUnit unit, Recipe recipe) {
        this.id = id;
        this.setQuantity(quantity);
        this.setIngredient(ingredient);
        this.setUnit(unit);
        this.setRecipe(recipe);
    }

    public RecipeIngredient(long id, BigDecimal quantity, String notes, Ingredient ingredient, MeasurementUnit unit, Recipe recipe) {
        this.id = id;
        this.setQuantity(quantity);
        this.setNotes(notes);
        this.setIngredient(ingredient);
        this.setUnit(unit);
        this.setRecipe(recipe);
    }

    // Getters and setters

    // Id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // Quantity
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { 
        requireNonNull(quantity, "Quantity cannot be null");

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }

    // Notes
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Ingredient
    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = requireNonNull(ingredient, "Ingredient cannot be null"); }

    // Measurement Unit
    public MeasurementUnit getUnit() { return unit; }
    public void setUnit(MeasurementUnit unit) { this.unit = requireNonNull(unit, "Measurement unit cannot be null"); }

    // Recipe
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = requireNonNull(recipe, "Recipe cannot be null"); }

    /**
     * Calculates the scaled quantity for a given number of servings.
     *
     * Example: if the recipe is for 4 servings and the ingredient
     * quantity is 200g, calling this method with 2 will return 100g.
     *
     * @param targetServings  the desired number of servings.
     * @param originalServings the original number of servings the recipe is based on.
     * @return the scaled quantity rounded to 2 decimal places.
     * @throws IllegalArgumentException if either servings value is less than 1.
     */
    public BigDecimal getScaledQuantity(int targetServings, int originalServings) {
        if (targetServings < 1) throw new IllegalArgumentException("Target servings must be at least 1");
        if (originalServings < 1) throw new IllegalArgumentException("Original servings must be at least 1");
        return quantity
            .multiply(BigDecimal.valueOf(targetServings))
            .divide(BigDecimal.valueOf(originalServings), 2, RoundingMode.HALF_UP);
    }
    
    // Override equals and hashCode based on id

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
        if (!(obj instanceof RecipeIngredient))
            return false;
        RecipeIngredient other = (RecipeIngredient) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "RecipeIngredient{id=" + id + ", ingredient=" + ingredient.getName()
            + ", quantity=" + quantity + " " + unit.getAbbreviation() + "}";
    }

}
