package es.uvigo.esei.tfg.dto.recipe.responses;

import java.math.BigDecimal;

/**
 * DTO representing an ingredient entry within a recipe response.
 */
public class RecipeIngredientResponse {
    
    private long id;
    private long ingredientId;
    private String ingredientName;
    private String categoryName;
    private BigDecimal quantity;
    private long unitId;
    private String unitName;
    private String unitAbbreviation;
    private String notes;

    // Constructor needed for JSON conversion
    public RecipeIngredientResponse() {}

    public RecipeIngredientResponse(
        long id,
        long ingredientId,
        String ingredientName,
        String categoryName,
        BigDecimal quantity,
        long unitId,
        String unitName,
        String unitAbbreviation,
        String notes
    ) {
        this.id = id;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.unitId = unitId;
        this.unitName = unitName;
        this.unitAbbreviation = unitAbbreviation;
        this.notes = notes;
    }

    // Getters and setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getIngredientId() { return ingredientId; }
    public void setIngredientId(long ingredientId) { this.ingredientId = ingredientId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public long getUnitId() { return unitId; }
    public void setUnitId(long unitId) { this.unitId = unitId; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public String getUnitAbbreviation() { return unitAbbreviation; }
    public void setUnitAbbreviation(String unitAbbreviation) { this.unitAbbreviation = unitAbbreviation; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

}
