package es.uvigo.esei.tfg.entities;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * An entity that represents a pet
 * 
 * @author Ismael
 */
@JsonPropertyOrder({ "id", "name", "specie", "breed", "owner_id" })
public class Pet {
    private int id;    
    private String name;
    private Species specie;
    private String breed;
    private int owner_id;

    //Constructor needed for the JSON conversion
    Pet() {}

    /**
     * Constructs a new instance of {@link Pet}
     * 
     * @param id identifier of the pet
     * @param name name of the pet
     * @param specie specie of the pet
     * @param breed breed of the pet
     * @param owner_id id of the pet owner
     */      
    public Pet(int id, String name, Species specie, String breed, int owner_id) {
        this.id = id;
        this.setName(name);
        this.setSpecies(specie);
        this.setBreed(breed);
        this.setOwnerId(owner_id);
    }

    /**
     * Returns the identifier of the pet
     * 
     * @return the identifier of the pet
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the pet
     * 
     * @return the name of the pet
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this name
     * 
     * @param name the new name of the pet
     * @throws NullPointerException if the {@code name} is {@code null}
     */
    public void setName(String name) {
        this.name = requireNonNull(name, "Name can't be null");
    }

    /**
     * Return the specie of the pet
     * 
     * @return the specie of the pet
     */
    public Species getSpecies() {
        return specie;
    }

    /**
     * Set the specie of this pet
     * 
     * @param specie the new specie of the pet
     * @throws NullPointerException if the {@code specie} is {@code null}
     */
    public void setSpecies(Species specie) {
        this.specie = requireNonNull(specie, "Specie can't be null");
    }

    /**
     * Returns the breed of the pet
     * 
     * @return the breed of the pet
     */
    public String getBreed() {
        return breed;
    }

    /**
     * Set the breed of this pet
     * 
     * @param breed the new breed ot the pet
     */
    public void setBreed(String breed) {
        this.breed = breed;
    }

    /**
     * Returns the id of the pet owner
     * 
     * @return the id of the pet owner
     */
    public int getOwnerId() {
        return owner_id;
    }

    /**
     * Set the id of the pet owner
     * 
     * @param owner_id the new id of the pet owner
     * @throws NullPointerException if the {@code owner_id} is {@code null}
     */
    public void setOwnerId(int owner_id) {
        this.owner_id = requireNonNull(owner_id, "Owner id can't be null");
    }

}
