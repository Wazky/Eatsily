package es.uvigo.esei.tfg.dto.recipe.requests;

import java.math.BigDecimal;

/**
 * DTO representing an ingredient entry within a recipe request.
 * 
 * The client must provide the ingredient's id from the catalog.
 * If the ingredient does not exist in the catalog, the request will be rejected.
 */
public class RecipeIngredientRequest {
    
    private Long ingredientId;
    private String ingredientName;  // Just for reference, not needed for processing
    private BigDecimal quantity;
    private Long unitId;
    private String notes;
    
    // Default constructor needed for JSON deserialization
    public RecipeIngredientRequest() {}
    
    public RecipeIngredientRequest(
        Long ingredientId,
        String ingredientName,
        BigDecimal quantity,
        Long unitId,
        String notes
    ) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unitId = unitId;
        this.notes = notes;
    }

    // Getters and setters

    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

}
