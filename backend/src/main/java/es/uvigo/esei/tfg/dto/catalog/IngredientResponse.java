package es.uvigo.esei.tfg.dto.catalog;

/**
 * DTO representing an ingredient response.
 */
public class IngredientResponse {

    private long id;
    private String name;
    private Long categoryId;
    private String categoryName;

    // Constructor needed for JSON serialization
    public IngredientResponse() {}

    public IngredientResponse(long id, String name, Long categoryId, String categoryName) {
        this.id           = id;
        this.name         = name;
        this.categoryId   = categoryId;
        this.categoryName = categoryName;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}