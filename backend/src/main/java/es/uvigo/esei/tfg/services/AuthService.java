package es.uvigo.esei.tfg.services;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ValidationException;

import org.mindrot.jbcrypt.BCrypt;

import es.uvigo.esei.tfg.dao.PeopleDAO;
import es.uvigo.esei.tfg.dao.TokenDAO;
import es.uvigo.esei.tfg.dao.UsersDAO;
import es.uvigo.esei.tfg.dto.auth.LoginRequest;
import es.uvigo.esei.tfg.dto.TokenResponse;
import es.uvigo.esei.tfg.dto.UserResponse;
import es.uvigo.esei.tfg.dto.auth.AuthResponse;
import es.uvigo.esei.tfg.dto.auth.RegisterRequest;
import es.uvigo.esei.tfg.entities.Person;
import es.uvigo.esei.tfg.entities.Token;
import es.uvigo.esei.tfg.entities.User;
import es.uvigo.esei.tfg.exceptions.AccountBlockedException;
import es.uvigo.esei.tfg.exceptions.AuthenticationException;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.util.JwtUtil;

public class AuthService {

    private final static long JWT_EXPIRATION_TIME =  15 * 60 * 1000; // 15 minutes
    private final static long JWT_REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days

    private final static Logger LOG = Logger.getLogger(AuthService.class.getName());
    private final static int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final JwtUtil jwtUtil;

    private final PeopleDAO peopleDAO;
    private final UsersDAO usersDAO;
    private final TokenDAO tokenDAO;
    
    public AuthService() {
        this.jwtUtil = new JwtUtil();
        this.peopleDAO = new PeopleDAO();
        this.usersDAO = new UsersDAO();
        this.tokenDAO = new TokenDAO();
    }

    /**
    * Registers a new user with the provided registration credentials.
     * It validates the registration data, checks for existing username and email,
     * creates the user and associated person record in the database, 
     * and generates an authentication response with tokens and user info.
     * 
    * @param credentials the registration credentials containing the user information to register.
    * @return a {@link AuthResponse} containing the authentication tokens and user info if the registration is successful.
    * @throws ValidationException if the registration data is invalid (e.g., missing required fields, invalid formats).
    * @throws DAOException if an error occurs while accessing the data source or if the transaction fails during user creation.
    */
    public AuthResponse register(RegisterRequest credentials) 
    throws ValidationException, DAOException {
        
        // Validate registration data
        validateRegisterData(credentials);
        
        String username = credentials.getUsername();
        if (usersDAO.existsByUsername(username)) {
            LOG.warning("Attempt to register with existing username: " + username);
            throw new ValidationException("Username already exists");
        }
        
        String email = credentials.getEmail();
        if (usersDAO.existsByEmail(email)) {
            LOG.warning("Attempt to register with existing email: " + email);
            throw new ValidationException("Email already exists");
        }

        // Create user and associated person record in a transaction
        return generateAuthResponse(createUserTransaction(credentials), "registration");
    }

    /**
     * Authenticates a user with the provided credentials and 
     * returns a JWT token if the authentication is successful.
     * 
     * @param credentials the login credentials of the user to authenticate.
     * @return a {@link AuthResponse} containing the JWT token if the authentication is successful.
     * @throws AccountBlockedException if the user account is blocked.
     * @throws AuthenticationException if the authentication fails.
     * @throws DAOException if an error occurs while accessing the data source.
     */
    public AuthResponse login(LoginRequest credentials)
    throws AccountBlockedException, AuthenticationException, DAOException {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // Authenticate the user using the provided credentials
        User user = authenticate(username, password);

        // Generate and return the authentication response with tokens and user info
        return generateAuthResponse(user, "login");
    }

    public TokenResponse refreshToken(String authHeader) 
    throws IllegalArgumentException, DAOException {
        // Extract the token from the Authorization header
        String token = jwtUtil.extractTokenFromHeader(authHeader);

        if (token == null) {
            LOG.warning("Missing or invalid Authorization header for token refresh");
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        
        // Extract username from the token 
        String username = jwtUtil.getUsernameFromToken(token);

        if (username == null) {
            LOG.warning("Invalid token: unable to extract username for token refresh");
            throw new IllegalArgumentException("Invalid token: unable to extract username");
        }

        final User user = usersDAO.getByUsername(username);  

        // Validate the token against the user details
        if (!jwtUtil.isTokenValid(token, user)) {
            LOG.warning("Invalid token provided for refresh for user: " + username);
            throw new IllegalArgumentException("Invalid  token");
        }

        // Generate new tokens for the user
        final TokenResponse newTokenResponse = generateTokenResponse(username);

        // Save the new refresh token in the database and revoke the old one
        saveUserToken(user, newTokenResponse.getRefreshToken());
        this.tokenDAO.revokeAllUserTokens(user);

        return newTokenResponse;
    }

    /**
     * Creates a new user and the associated person record in the database within a transaction.
     * This method ensures that both the user and person records are created successfully, 
     * and if any error occurs, the transaction is rolled back to maintain data integrity.
     * 
     * @param credentials the registration credentials containing the user information to be stored in the database.
     * @return a {@link User} entity representing the newly created user.
     * @throws DAOException if an error occurs while accessing the data source or if the transaction fails.
     */
    private User createUserTransaction(RegisterRequest credentials) 
    throws DAOException {
        try {
            // Obtain a connection and start a transaction
            Connection conn = peopleDAO.getConnection();
            conn.setAutoCommit(false); 

            // Create person record
            final Person person = peopleDAO.create(conn, credentials.getName(), credentials.getSurname());

            // Convert registration credentials to user entity
            User user = credentialsToUser(credentials);
            
            // Associate the created person with the user
            user.setPerson(person);

            // Create user record
            user = usersDAO.create(conn, user);
        
            conn.commit(); // Commit the transaction if everything is successful
            LOG.info("User registered successfully: " + user.getUsername());

            return user;

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error creating user", e);
            throw new DAOException(e);
        }
    }

    /**
     * Authenticates the user with the provided username and password.
     * It checks if the user exists, if the account is blocked, and if the password is correct. 
     * It also handles failed login attempts and account locking.
     * 
     * @param username the username of the user to authenticate.
     * @param password the password of the user to authenticate.
     * @return a {@link User} entity representing the authenticated user if the authentication is successful.
     * @throws AccountBlockedException if the user account is blocked due to multiple failed login attempts.
     * @throws AuthenticationException if the authentication fails due to invalid credentials.
     * @throws DAOException if an error occurs while accessing the data source.
     */
    private User authenticate(String username, String password)
    throws AccountBlockedException, AuthenticationException, DAOException {

        // Validate that username and password are provided
        if (!validateRequiredString(username, "Username") || 
            !validateRequiredString(password, "Password")) {
            throw new AuthenticationException("Username and password must be provided");
        }

        User user;
        try {
            // Retrieve user by username
            user = this.usersDAO.getByUsername(username);
        
        } catch (IllegalArgumentException e) {
            LOG.log(Level.FINE, "Authentication failed for non-existing user: " + username);
            throw new AuthenticationException("Invalid user credentials");
        }

        // Check if user account is blocked
        if (user.isBlocked()) {
            LOG.log(Level.WARNING, "Blocked user attempted to login: " + username);
            throw new AccountBlockedException("User account is blocked due to multiple failed login attempts");
        }

        // Verify password
        if(!validatePassword(password, user)) {
            throw new AuthenticationException("Invalid user credentials");
        }

        // Password is correct, reset failed login attempts if any
        if (user.getFailedLoginAttempts() > 0) {
            usersDAO.resetFailedLoginAttempts(user.getId());
        }

        // Update last login timestamp
        usersDAO.updateLastLogin(user.getId());

        return user;
    }

    /**
     * Validates the registration data provided in the {@link RegisterRequest}.
     * It checks that all required fields are present and not empty, 
     * and that they conform to expected formats (e.g., email format, password strength).
     * If any validation fails, a {@link ValidationException} is thrown with an appropriate message.
     * 
     * @param credentials the registration credentials to validate.
     * @throws ValidationException if any validation check fails.
     */
    private void validateRegisterData(RegisterRequest credentials) 
    throws ValidationException {

        // Validate that all required fields are provided
        if (!validateRequiredRegisterData(credentials)) {
            LOG.warning("Missing required registration fields");
            throw new ValidationException("All required fields must be provided and not empty");
        }

        // Validate field formats
        if (!validateRegisterDataFormats(credentials)) {
            LOG.warning("Invalid registration data formats for user: " + credentials.getUsername());
            throw new ValidationException("One or more fields have invalid format");
        }

    }   

    /**
     * Validates the provided password against the stored password hash of the user.
     * 
     * @param password the password to validate.
     * @param user the user whose password hash to validate against.
     * @return true if the password is valid, false otherwise.
     */
    private boolean validatePassword(String password, User user) 
    throws AccountBlockedException, DAOException {
        // Invalid password
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            
            // Record failed login attempt
            if (user.getFailedLoginAttempts() < MAX_FAILED_LOGIN_ATTEMPTS) {
                usersDAO.recordFailedLoginAttempt(user.getId());
            
            // Lock account if max attempts reached
            } else {
                usersDAO.lockUserAccount(user.getId());
                LOG.log(Level.WARNING, "User account locked due to multiple failed login attempts: " + user.getUsername());
                
                throw new AccountBlockedException("User account is blocked due to multiple failed login attempts");
            }

            return false;
        }

        // Valid password
        return true;
    }

    /**
     * Validates that all required fields for registration are provided and not empty.
     * 
     * @param credentials the registration credentials to validate.
     * @return true if all required fields are valid, false otherwise.
     */
    private boolean validateRequiredRegisterData(RegisterRequest credentials) {
        boolean valid = true;

        valid &= validateRequiredString(credentials.getUsername(), "Username");
        valid &= validateRequiredString(credentials.getPassword(), "Password");
        valid &= validateRequiredString(credentials.getEmail(), "Email");
        valid &= validateRequiredString(credentials.getName(), "Name");
        valid &= validateRequiredString(credentials.getSurname(), "Surname");

        return valid;
    }

    /**
     * Validates the formats of the registration data fields, 
     * such as email format and password strength.
     * If any field has an invalid format, 
     * a {@link ValidationException} is thrown with an appropriate message.
     * 
     * @param credentials the registration credentials to validate.
     * @return true if all fields have valid formats, false otherwise.
     * @throws ValidationException if any field has an invalid format.
     */
    private boolean validateRegisterDataFormats(RegisterRequest credentials) 
    throws ValidationException {
        boolean valid = true;

        // Validate email format
        if (!credentials.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            LOG.warning("Invalid email format: " + credentials.getEmail());
            throw new ValidationException("Invalid email format");            
        }

        // Validate password strength (at least 8 characters, including letters and numbers)
        if (!credentials.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            LOG.warning("Weak password provided for user: " + credentials.getUsername());
            throw new ValidationException("Password must be at least 8 characters long and include both letters and numbers");
        }

        // Additional format validations can be added here (e.g., username format, name/surname format)

        return valid;
    }

    /**
     * Validates that a required string field is not null or empty.
     * 
     * @param field the string field to validate.
     * @param message the message to log if the field is invalid.
     * @return true if the field is valid, false otherwise.
     */
    private boolean validateRequiredString(String field, String message) {

        if (field == null || field.isEmpty()) {
            LOG.log(Level.WARNING, message + " is missing");
            return false;
        }
        return true;
    }

    /**
     * Converts the provided registration credentials into a {@link User} entity.
     * 
     * @param credentials the registration credentials to convert.
     * @return a {@link User} entity with the data from the registration credentials.
     */
    private User credentialsToUser(RegisterRequest credentials) {
        User user = new User();

        user.setUsername(credentials.getUsername());
        user.setEmail(credentials.getEmail());

        // Hash the password before storing
        String passwordHash = BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt());
        user.setPasswordHash(passwordHash);

        return user;
    }

    /**
     * Generates a {@link TokenResponse} containing an access token 
     * and a refresh token for the given username.
     * 
     * @param username the username for which to generate the tokens.
     * @return a {@link TokenResponse} containing the generated access 
     * and refresh tokens along with their expiration times.
     */
    private TokenResponse generateTokenResponse(String username) {
        String accessToken = jwtUtil.generateToken(username, JWT_EXPIRATION_TIME);
        String refreshToken = jwtUtil.generateToken(username, JWT_REFRESH_TOKEN_EXPIRATION_TIME);


        return new TokenResponse(
            accessToken, 
            refreshToken, 
            JWT_EXPIRATION_TIME, 
            JWT_REFRESH_TOKEN_EXPIRATION_TIME
        );        
    }

    private AuthResponse generateAuthResponse(User user, String type) 
    throws DAOException {

        // Generate user response
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole()
        );
        
        // Generate token response
        TokenResponse tokenResponse = generateTokenResponse(userResponse.getUsername());

        // Save refresh token in the database
        saveUserToken(user, tokenResponse.getRefreshToken());

        if ("registration".equals(type)) {
            return AuthResponse.registrationSuccess(tokenResponse, userResponse);

        } else if ("login".equals(type)) {
            return AuthResponse.loginSuccess(tokenResponse, userResponse);
        
        } else {
            throw new IllegalArgumentException("Invalid auth response type: " + type);
        }
    }

    private void saveUserToken(User user, String refreshToken) 
    throws DAOException {
        Token token = new Token();
        token.setToken(refreshToken);
        token.setToken_type("refresh");
        token.setIdUser(user.getId());

        this.tokenDAO.create(token);
    }
}
