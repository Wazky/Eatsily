package es.uvigo.esei.tfg.entities;

public class Recipe {
    private int id;
    private String name;
    private String description;
    private int preparationTime; // in minutes
    private int cookingTime; // in minutes
    private int servings;

    // Empty constructor
    public Recipe() {}

    // Constructor with parameters
    public Recipe(int id, String name, String description, int preparationTime, int cookingTime, int servings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.servings = servings;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPreparationTime() { return preparationTime; }
    public void setPreparationTime(int preparationTime) { this.preparationTime = preparationTime; }

    public int getCookingTime() { return cookingTime; }
    public void setCookingTime(int cookingTime) { this.cookingTime = cookingTime; }

    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }
}
