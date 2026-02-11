package es.uvigo.esei.tfg.entities;

import static java.util.Objects.requireNonNull;

/**
 * An entity that represents a person.
 * 
 * @author DRM
 */
public class Person {
	private long id;
	private String name;
	private String surname;
	
	// Constructor needed for the JSON conversion
	public Person() {}
	
	public Person(long id) {
		this.id = id;
	}
	
	public Person(String name, String surname) {
		this.setName(name);
		this.setSurname(surname);
	}

	/**
	 * Constructs a new instance of {@link Person}.
	 *
	 * @param id identifier of the person.
	 * @param name name of the person.
	 * @param surname surname of the person.
	 */
	public Person(long id, String name, String surname) {
		this.id = id;
		this.setName(name);
		this.setSurname(surname);
	}
	
	/**
	 * Returns the identifier of the person.
	 * 
	 * @return the identifier of the person.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the identifier of this person.
	 * 
	 * @param id the new identifier of the person.
	 */
	public void setId(long id) {
		this.id = requireNonNull(id, "Id can't be null");
	}

	/**
	 * Returns the name of the person.
	 * 
	 * @return the name of the person.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this person.
	 * 
	 * @param name the new name of the person.
	 * @throws NullPointerException if the {@code name} is {@code null}.
	 */
	public void setName(String name) {
		this.name = requireNonNull(name, "Name can't be null");
	}

	/**
	 * Returns the surname of the person.
	 * 
	 * @return the surname of the person.
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Set the surname of this person.
	 * 
	 * @param surname the new surname of the person.
	 * @throws NullPointerException if the {@code surname} is {@code null}.
	 */
	public void setSurname(String surname) {
		this.surname = requireNonNull(surname, "Surname can't be null");
	}

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
		if (!(obj instanceof Person))
			return false;
		Person other = (Person) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
