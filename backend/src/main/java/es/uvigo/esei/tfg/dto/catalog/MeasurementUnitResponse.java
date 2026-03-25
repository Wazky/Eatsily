package es.uvigo.esei.tfg.dto.catalog;

/**
 * DTO representing a measurement unit response.
 */
public class MeasurementUnitResponse {

    private long id;
    private String name;
    private String abbreviation;
    private String type;

    // Constructor needed for JSON serialization
    public MeasurementUnitResponse() {}

    public MeasurementUnitResponse(long id, String name, String abbreviation, String type) {
        this.id           = id;
        this.name         = name;
        this.abbreviation = abbreviation;
        this.type         = type;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}