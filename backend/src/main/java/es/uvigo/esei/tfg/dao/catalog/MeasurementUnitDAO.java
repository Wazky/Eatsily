package es.uvigo.esei.tfg.dao.catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.dao.DAO;
import es.uvigo.esei.tfg.entities.catalog.MeasurementUnit;
import es.uvigo.esei.tfg.entities.catalog.MeasurementUnit.UnitType;
import es.uvigo.esei.tfg.exceptions.DAOException;

/**
 * DAO class for the {@link MeasurementUnit} entities.
 * Measurement units are used to specify the quantity of an ingredient in a recipe, 
 * and they can be of different types (e.g., weight, volume, countable).
 */
public class MeasurementUnitDAO extends DAO {
    private final static Logger LOG = Logger.getLogger(MeasurementUnitDAO.class.getName());

    //============     CREATE     ============

    /**
     * Persists a new measurement unit in the system. An identifier will be assigned
     * automatically to the new unit.
     * 
     * @param unit the MeasurementUnit entity containing the information of the new unit to be persisted.
     *             The id field will be ignored, as it will be generated automatically.
     * @return a {@link MeasurementUnit} entity representing the persisted unit, including the generated identifier.
     * @throws DAOException if an error happens while persisting the new unit.
     * @throws IllegalArgumentException if any of the required fields of the unit is null or invalid.
     */
    public MeasurementUnit create(MeasurementUnit unit) 
    throws DAOException, IllegalArgumentException {
        ensureMeasurementUnitDataIntegrity(unit);

        try (final Connection conn = this.getConnection(null)) {
            final String query = "INSERT INTO measurement_units" +
            " (name, abbreviation, type)" +
            " VALUES(?, ?, ?)";
            
            try (final PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, unit.getName());
                statement.setString(2, unit.getAbbreviation());
                statement.setString(3, unit.getType().toString());

                if (statement.executeUpdate() == 1) {
                    try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            unit.setId(generatedKeys.getInt(1));
                            return unit;
                        } else {
                            throw new DAOException("Failed to retrieve generated id for the new measurement unit");
                        }
                    }
                } else {
                    throw new DAOException("Failed to create the new measurement unit");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error creating a new measurement unit", e);
            throw new DAOException(e);
        }
    }

    //============     READ     ============

    /**
     * Retrieves a measurement unit from the system by its identifier.
     * 
     * @param id the identifier of the measurement unit to be retrieved.
     * @return a {@link MeasurementUnit} entity representing the retrieved unit.
     * @throws DAOException if an error happens while retrieving the unit.
     * @throws IllegalArgumentException if no unit is found with the provided identifier.
     */
    public MeasurementUnit get(long id) 
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM measurement_units WHERE id_measurement_unit = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);

                try (final ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return rowToEntity(result);
                    } else {
                        throw new IllegalArgumentException("Invalid id");
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting a measurement unit", e);
            throw new DAOException(e);
        }
    }

    public List<MeasurementUnit> getByType(UnitType type) 
    throws DAOException, IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("UnitType cannot be null");
        }

        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM measurements_units" +
                " WHERE type=?" + 
                " ORDER BY name ASC";
            
            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, type.name());

                try (final ResultSet result = statement.executeQuery()) {
                    final List<MeasurementUnit> units = new LinkedList<>();
                    while (result.next()) {
                        units.add(rowToEntity(result));
                    }
                    return units;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting measurements units by type", e);
            throw new DAOException(e);
        }
    }

    /**
     * Retrieves all the measurement units stored in the system.
     * 
     * @return a list of {@link MeasurementUnit} entities representing all the measurement units stored in the system.
     * @throws DAOException if an error happens while retrieving the units.
     */
    public List<MeasurementUnit> list()
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "SELECT * FROM measurement_units ORDER BY type ASC, name ASC";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                try (final ResultSet result = statement.executeQuery()) {
                    final List<MeasurementUnit> units = new LinkedList<>();
                    while (result.next()) {
                        units.add(rowToEntity(result));
                    }
                    return units;
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error getting measurement units", e);
            throw new DAOException(e);
        }
    }

    //============     UPDATE     ============

    /**
     * Updates the information of a measurement unit stored in the system. The unit to be updated is identified by the id field
     * of the provided entity, and the rest of fields will be updated with the values of the provided entity.
     * 
     * @param unit a {@link MeasurementUnit} entity containing the updated information of the unit. The id field must correspond
     *             to an existing unit in the system, and the rest of fields will be used to update that unit.
     * @throws DAOException if an error happens while updating the unit.
     * @throws IllegalArgumentException if any of the required fields of the unit is null or invalid, or if no unit is found with the provided id.
     */
    public void update(MeasurementUnit unit)
    throws DAOException, IllegalArgumentException {
        ensureMeasurementUnitDataIntegrity(unit);

        try (final Connection conn = this.getConnection(null)) {
            final String query = "UPDATE measurement_units SET" +
            " name = ?, abbreviation = ?, type = ?" +
            " WHERE id_measurement_unit = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, unit.getName());
                statement.setString(2, unit.getAbbreviation());
                statement.setString(3, unit.getType().name());
                statement.setLong(4, unit.getId());

                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No measurement unit found with the provided id");
                }
            }

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error updating a measurement unit", e);
            throw new DAOException(e);
        }
    }

    //============     DELETE     ============

    /**
     * Deletes a measurement unit from the system by its identifier.
     * 
     * @param id the identifier of the measurement unit to be deleted.
     * @throws DAOException if an error happens while deleting the unit.
     * @throws IllegalArgumentException if no unit is found with the provided identifier.
     */
    public void delete(long id)
    throws DAOException, IllegalArgumentException {
        try (final Connection conn = this.getConnection(null)) {
            final String query = "DELETE FROM measurement_units WHERE id_measurement_unit = ?";

            try (final PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, id);

                if (statement.executeUpdate() == 0) {
                    throw new IllegalArgumentException("No measurement unit found with the provided id");
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error deleting a measurement unit", e);
            throw new DAOException(e);
        }
    }

    //============     AUXILIARY METHODS     ============
    
    /**
     * Converts a database row to a {@link MeasurementUnit} entity.
     * 
     * @param result the ResultSet containing the row data.
     * @return a {@link MeasurementUnit} entity representing the converted row.
     * @throws SQLException if an error happens while converting the row.
     */
    private MeasurementUnit rowToEntity(ResultSet result)
    throws SQLException {
        return new MeasurementUnit(
            result.getLong("id_measurement_unit"),
            result.getString("name"),
            result.getString("abbreviation"),
            UnitType.valueOf(result.getString("type"))
        );
    }
    
    /**
     * Ensures that the provided MeasurementUnit entity has all the required data to be persisted in the system.
     * 
     * @param unit the MeasurementUnit entity to be validated.
     * @throws IllegalArgumentException if any of the required fields of the unit is null or invalid.
     */
    private void ensureMeasurementUnitDataIntegrity(MeasurementUnit unit) 
    throws IllegalArgumentException {
        if (unit == null) {
            throw new IllegalArgumentException("Measurement unit cannot be null");
        }

        if (unit.getName() == null || unit.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Measurement unit name cannot be null or blank");
        }

        if (unit.getAbbreviation() == null || unit.getAbbreviation().trim().isEmpty()) {
            throw new IllegalArgumentException("Measurement unit abbreviation cannot be null or blank");
        }

        if (unit.getType() == null) {
            throw new IllegalArgumentException("Measurement unit type cannot be null");
        }
    }

}
