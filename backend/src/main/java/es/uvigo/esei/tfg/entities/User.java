package es.uvigo.esei.tfg.entities;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;

/**
 * An entity that represents a user.
 * 
 * @author DRM
 */
public class User {
	
	private long id;
	private String username;
	private String passwordHash;
	private String email;
	private String role;
	private boolean active;
	private boolean blocked;
	private int failedLoginAttempts;
	private LocalDateTime creationDate;
	private LocalDateTime lastLogin;
	private Person person;

	// Constructor needed for the JSON conversion
	public User() {}

	/**
	 * Constructs a new instance of {@link User}.
	 *
	 * @param id identifier of the user.
	 * @param username username of the user.
	 * @param passwordHash password hash of the user.
	 * @param email email of the user.
	 * @param role role of the user.
	 * @param person associated person entity.
	 */
	public User(
		long id,
		String username,
		String passwordHash,
		String email,
		String role,
		Person person
	) {
		this.id = id;
		this.setUsername(username);
		this.setPasswordHash(passwordHash);
		this.setEmail(email);
		this.setRole(role);
		this.setPerson(person);
	}

	public long getId() { return id; }

	public String getUsername() { return username; }
	public void setUsername(String username) {
		this.username = requireNonNull(username, "Username can't be null");
	}

	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = requireNonNull(passwordHash, "Password hash can't be null");
	}

	public String getEmail() { return email; }
	public void setEmail(String email) {
		this.email = requireNonNull(email, "Email can't be null");
	}

	public String getRole() { return role; }
	public void setRole(String role) {
		this.role = requireNonNull(role, "Role can't be null");
	}

	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }

	public boolean isBlocked() { return blocked; }
	public void setBlocked(boolean blocked) { this.blocked = blocked; }

	public int getFailedLoginAttempts() { return failedLoginAttempts; }
	public void setFailedLoginAttempts(int failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	public LocalDateTime getCreationDate() { return creationDate; }
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = requireNonNull(creationDate, "Creation date can't be null");
	}

	public LocalDateTime getLastLogin() { return lastLogin; }
	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Person getPerson() { return person; }
	public void setPerson(Person person) {
		this.person = requireNonNull(person, "Person can't be null");
	}

	// Convenience methods for failed login attempts management

	public void incrementFailedLoginAttempts() {
		this.failedLoginAttempts++;
	}

	public void resetFailedLoginAttempts() {
		this.failedLoginAttempts = 0;
	}
}
