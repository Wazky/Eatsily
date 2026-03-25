package es.uvigo.esei.tfg.services.user;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import es.uvigo.esei.tfg.dao.user.UsersDAO;
import es.uvigo.esei.tfg.dto.auth.LoginRequest;
import es.uvigo.esei.tfg.dto.ErrorResponse;
import es.uvigo.esei.tfg.dto.TokenResponse;
import es.uvigo.esei.tfg.dto.UserResponse;
import es.uvigo.esei.tfg.dto.auth.AuthResponse;
import es.uvigo.esei.tfg.dto.auth.RegisterRequest;
import es.uvigo.esei.tfg.entities.user.Token;
import es.uvigo.esei.tfg.entities.user.User;
import es.uvigo.esei.tfg.exceptions.AccountBlockedException;
import es.uvigo.esei.tfg.exceptions.AuthenticationException;
import es.uvigo.esei.tfg.exceptions.ValidationException;
import es.uvigo.esei.tfg.exceptions.DAOException;
import es.uvigo.esei.tfg.util.JwtUtil;

public class AuthService {

    private final static Logger LOG = Logger.getLogger(AuthService.class.getName());
    private final static int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final JwtUtil jwtUtil;

    private final UserPersonService userPersonService;
    private final TokenManagmentService tokenManagmentService;

    private final UsersDAO usersDAO;
    
    public AuthService() {
        this.jwtUtil = new JwtUtil();
        this.userPersonService = new UserPersonService();
        this.tokenManagmentService = new TokenManagmentService();
        this.usersDAO = new UsersDAO();
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
        
        // Check for existing username 
        String username = credentials.getUsername();
        if (usersDAO.existsByUsername(username)) {
            LOG.warning("Attempt to register with existing username: " + username);
            
            Map<String, Object> details = new HashMap<>();
            details.put("field", "username");

            throw new ValidationException(
                new ErrorResponse(
                    "REG_003",
                    "Username already exists",
                    details
                )
            );
        }
        
        // Check for existing email
        String email = credentials.getEmail();
        if (usersDAO.existsByEmail(email)) {
            LOG.warning("Attempt to register with existing email: " + email);

            Map<String, Object> details = new HashMap<>();
            details.put("field", "email");

            throw new ValidationException(
                new ErrorResponse(
                    "REG_004",
                    "Email already exists",
                    details
                )
            );
        }

        User user = userPersonService.createUser(credentials);

        // Generate and return authentication response with tokens and user info
        return generateAuthResponse(user, "registration");
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

    /**
     * Logs out the user by revoking their active tokens in the database.
     * - (IT REVOKES ALL TOKENS FOR USER STORED
     *  Needs adjustement to just revoke the refresh token associated 
     *  with the current session if we want to allow multiple sessions)
     * @throws DAOException
     */
    public void logout(String authHeader) 
    throws IllegalArgumentException, DAOException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
            
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null) {
            LOG.warning("Missing or invalid Authorization header for logout");
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            LOG.warning("Invalid token: unable to extract username for logout");
            throw new IllegalArgumentException("Invalid token: unable to extract username");
        }

        final User user = usersDAO.getByUsername(username);
        tokenManagmentService.revokeUserTokens(user.getId());
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

        // Verify token existence and revocation status in the database
        Token storedToken = tokenManagmentService.getByToken(token);

        // Generate and return new tokens, and revoke the old refresh token in the database
        return tokenManagmentService.refreshTokens(user);
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
            
            throw new AuthenticationException(
                new ErrorResponse(
                    "UNAUTH_001",
                    "Username and password must be provided"     
                )
            );
        }

        User user;
        try {
            // Retrieve user by username
            user = this.usersDAO.getByUsername(username);
        
        } catch (IllegalArgumentException e) {
            LOG.log(Level.FINE, "Authentication failed for non-existing user: " + username);

            throw new AuthenticationException(
                new ErrorResponse(
                    "UNAUTH_002",
                    "Invalid credentials"
                )
            );
        }

        // Check if user account is blocked
        if (user.isBlocked()) {
            LOG.log(Level.WARNING, "Blocked user attempted to login: " + username);
            
            throw new AccountBlockedException(
                new ErrorResponse(
                    "UNAUTH_003",
                    "User account is blocked due to multiple failed login attempts"
                )
            );
        }

        // Verify password
        if(!validatePassword(password, user)) {
            throw new AuthenticationException(
                new ErrorResponse(
                    "UNAUTH_002",
                    "Invalid credentials"
                )
            );
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

            throw new ValidationException(
                new ErrorResponse(
                    "REG_001",
                    "All required fields must be provided and not empty"
                )
            );
        }
        
        // Validate field formats
        if (!validateRegisterDataFormats(credentials)) {
            LOG.warning("Invalid registration data formats for user: " + credentials.getUsername());
            throw new ValidationException(
                new ErrorResponse(
                    "REG_002",
                    "One or more fields have invalid format"
                )
            );
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
                
                throw new AccountBlockedException(
                    new ErrorResponse(
                        "UNAUTH_003",
                        "User account is blocked due to multiple failed login attempts"
                    )
                );
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

            Map<String, Object> details = new HashMap<>();
            details.put("field", "email");

            throw new ValidationException(
                new ErrorResponse(
                    "VALIDATION.INVALID_EMAIL_FORMAT",
                    "Invalid email format",
                    details
                )
            );            
        }

        // Validate password strength (at least 8 characters, including letters and numbers)
        if (!credentials.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            LOG.warning("Weak password provided for user: " + credentials.getUsername());

            Map<String, Object> details = new HashMap<>();
            details.put("field", "password");

            throw new ValidationException(
                new ErrorResponse(
                    "VALIDATION.INVALID_PASSWORD_FORMAT",
                    "Password must be at least 8 characters long and include both letters and numbers",
                    details
                )
            );
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
     * Generates an authentication response containing the user information and generated tokens.
     * 
     * @param user the authenticated user for which to generate the authentication response.
     * @param type the type of authentication response to generate (e.g., "registration" or "login").
     * @return an authentication response containing the user information and generated tokens.
     * @throws IllegalArgumentException if the type is not "registration" or "login".
     * @throws DAOException if an error occurs while generating the tokens.
     */
    private AuthResponse generateAuthResponse(User user, String type) 
    throws IllegalArgumentException, DAOException {

        // Generate user response
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole()
        );
        
        // Generate tokens and save refresh token in the database
        TokenResponse tokenResponse = tokenManagmentService.generateNewTokens(user);

        switch (type) {
            case "registration":
                return AuthResponse.registrationSuccess(tokenResponse, userResponse);
            case "login":
                return AuthResponse.loginSuccess(tokenResponse, userResponse);
            default:
                throw new IllegalArgumentException("Invalid auth response type: " + type);
        }

    }

}
