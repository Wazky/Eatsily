package es.uvigo.esei.tfg.services;

import java.util.List;
import java.util.logging.Logger;

import es.uvigo.esei.tfg.dao.DAOException;
import es.uvigo.esei.tfg.dao.PeopleDAO;
import es.uvigo.esei.tfg.dao.PetDAO;
import es.uvigo.esei.tfg.entities.Pet;
import es.uvigo.esei.tfg.entities.Species;

public class PetService {
    private final static Logger LOG = Logger.getLogger(PetService.class.getName());
    private final PetDAO petDAO;
    private final PeopleDAO peopleDAO;

    public PetService() {
        this.petDAO = new PetDAO();
        this.peopleDAO = new PeopleDAO();
    }

    public Pet getPet(int id) 
    throws DAOException, IllegalArgumentException {
        if(id <= 0) {
            throw new IllegalArgumentException("Invalid pet id");
        }   
        
        Pet pet = this.petDAO.get(id);
        if(pet == null) {
            throw new IllegalArgumentException("Pet does not exist");
        }

        return pet;
    }

    public List<Pet> listPets() throws DAOException {
        return this.petDAO.list();
    }
    
    public List<Pet> listByOwner(int ownerId) 
    throws DAOException, IllegalArgumentException {
        // Check if the owner exists
        if (!this.peopleDAO.exists(ownerId)) {
            throw new IllegalArgumentException("Owner does not exist");
        }

        return this.petDAO.listByOwner(ownerId);
    }

    public Pet addPet(String name, Species specie, String breed, int ownerId) 
    throws DAOException, IllegalArgumentException {
        if (name == null || specie == null) {
            throw new IllegalArgumentException("Invalid pet data, name and specie are required");
        }    

        if (!this.peopleDAO.exists(ownerId)) {
            throw new IllegalArgumentException("Invalid owner id");
        }

        return this.petDAO.add(name, specie, breed, ownerId);

    }

    public Pet updatePet(int id, String name, Species specie, String breed, int owner_id)
    throws DAOException, IllegalArgumentException {
        //Check valid attributes for the pet
        if (name == null || specie == null) {
            throw new IllegalArgumentException("Invalid pet data, name and specie are required");
        }
        //Check if the owner exists
        if (!this.peopleDAO.exists(owner_id)) {
            throw new IllegalArgumentException("Invalid owner id");
        }

        Pet modifiedPet = new Pet(id, name, specie, breed, owner_id);
        this.petDAO.modify(modifiedPet);

        return modifiedPet;
    }

    public void deletePet(int id) 
    throws DAOException, IllegalArgumentException {
        this.petDAO.delete(id);
    }


}
