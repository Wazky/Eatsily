package es.uvigo.esei.tfg.entities.catalog;

import static java.util.Objects.requireNonNull;

/**
 * An entity that represents a measurement unit for ingredients in recipes.
 */
public class MeasurementUnit {
    
    public enum UnitType {
        WEIGHT, // g, kg, lb, oz
        VOLUME, // ml, l, cup, tbsp, tsp
        UNIT,   // pcs, slices, pinches, etc.
        OTHER   // Any other type of unit that doesn't fit in the previous categories
    }

    private long id;
    private String name;
    private String abbreviation;
    private UnitType type;

    // Constructor needed for the JSON conversion
    public MeasurementUnit() {}

    public MeasurementUnit(long id) {
        this.id = id;
    }

    public MeasurementUnit(long id, String name, String abbreviation, UnitType type) {
        this.id = id;
        this.setName(name);
        this.setAbbreviation(abbreviation);
        this.setType(type);
    }

    // Getters and Setters

    // Id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // Name
    public String getName() { return name; }
    public void setName(String name) { this.name = requireNonNull(name, "Name cannot be null"); }

    // Abbreviation
    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = requireNonNull(abbreviation, "Abbreviation cannot be null"); }

    // Unit Type
    public UnitType getType() { return type; }
    public void setType(UnitType type) { this.type = requireNonNull(type, "Type cannot be null"); }


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
        if (!(obj instanceof MeasurementUnit))
            return false;
        MeasurementUnit other = (MeasurementUnit) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return "MeasurementUnit {id=" + id + ", name='" + name + "', abbreviation='" + abbreviation + "', type=" + type + "}";
    }
}
