package es.uvigo.esei.tfg.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Generated;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.tfg.dao.catalog.IngredientCategoryDAO;
import es.uvigo.esei.tfg.dao.catalog.IngredientDAO;
import es.uvigo.esei.tfg.dao.catalog.MeasurementUnitDAO;
import es.uvigo.esei.tfg.dto.catalog.IngredientCategoryResponse;
import es.uvigo.esei.tfg.dto.catalog.IngredientResponse;
import es.uvigo.esei.tfg.dto.catalog.MeasurementUnitResponse;
import es.uvigo.esei.tfg.entities.catalog.Ingredient;
import es.uvigo.esei.tfg.entities.catalog.IngredientCategory;
import es.uvigo.esei.tfg.entities.catalog.MeasurementUnit;
import es.uvigo.esei.tfg.entities.catalog.MeasurementUnit.UnitType;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.security.Secured;

@Secured
@Path("/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogResource extends BaseResource {
    private final static Logger LOG = Logger.getLogger(CatalogResource.class.getName());

    private final IngredientCategoryDAO ingredientCategoryDAO;
    private final IngredientDAO ingredientDAO;
    private final MeasurementUnitDAO measurementUnitDAO;

    public CatalogResource() {
        this.ingredientCategoryDAO = new IngredientCategoryDAO();
        this.ingredientDAO = new IngredientDAO();
        this.measurementUnitDAO = new MeasurementUnitDAO();
    }

    CatalogResource(IngredientCategoryDAO ingredientCategoryDAO, IngredientDAO ingredientDAO, MeasurementUnitDAO measurementUnitDAO) {
        this.ingredientCategoryDAO = ingredientCategoryDAO;
        this.ingredientDAO = ingredientDAO;
        this.measurementUnitDAO = measurementUnitDAO;
    }

    @GET
    @Path("/categories")
    public Response listCategories() {
        try {
            List<IngredientCategory> categories = ingredientCategoryDAO.list();

            if (categories == null || categories.isEmpty()) {
                return notFound("No ingredient categories found");
            }

            List<IngredientCategoryResponse> response = new LinkedList<>();
            for ( IngredientCategory category : categories) {
                response.add(new IngredientCategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getDescription()
                ));
            }        

            return ok(response);

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error fetching ingredient categories", e);
            return internalServerError("Failed to fetch ingredient categories");
        }
    }
    

    @GET
    @Path("/units")
    public Response listUnits(
        @QueryParam("type") String type
    ) {
        try {
            List<MeasurementUnit> units;

            if (type != null && !type.trim().isEmpty()) {
                try {
                    UnitType unitType = UnitType.valueOf(type.toUpperCase());
                    units = measurementUnitDAO.getByType(unitType);
                } catch (IllegalArgumentException iae) {
                    LOG.log(Level.FINE, "Invalid unit type provided: " + type, iae);
                    return badRequest("Invalid unit type: " + type + ". Valid types are: MASS, VOLUME, COUNT, OTHER");
                }
            } else {
                units = measurementUnitDAO.list();
            }

            List<MeasurementUnitResponse> response = new LinkedList<>();
            for (MeasurementUnit unit : units) {
                response.add(new MeasurementUnitResponse(
                    unit.getId(),
                    unit.getName(),
                    unit.getAbbreviation(),
                    unit.getType().name()
                ));
            }   

            return ok(response);
        
        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error fetching measurement units", e);
            return internalServerError("Failed to fetch measurement units");
        }
    }

    @GET
    @Path("/ingredients")
    public Response listIngredients(
        @QueryParam("search") String search
    ) {
        try {
            List<Ingredient> ingredients;

            if (search != null && !search.trim().isEmpty()) {
                ingredients = ingredientDAO.search(search.trim());
            } else {
                ingredients = ingredientDAO.list();
            }

            List<IngredientResponse> response = new LinkedList<>();
            for (Ingredient ingredient : ingredients) {
                response.add(new IngredientResponse(
                    ingredient.getId(),
                    ingredient.getName(),
                    ingredient.getCategory() != null ? ingredient.getCategory().getId() : null,
                    ingredient.getCategory() != null ? ingredient.getCategory().getName() : null
                ));
            }

            return ok(response);

        } catch (DAOException e) {
            LOG.log(Level.SEVERE, "Error fetching ingredients", e);
            return internalServerError("Failed to fetch ingredients");
        }
    }

}
