package service;

import java.util.UUID;
import dataaccess.*;
import dto.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.ResponseException;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws ResponseException {
        try {
            UserData user = userDAO.retrieveUser(registerRequest.getUsername());
            if (user != null) {
                throw new ResponseException(403, "Error: already taken");
            }
            user = userDAO.createUser(
                    registerRequest.getUsername(),
                    BCrypt.hashpw(registerRequest.getPassword(),
                    BCrypt.gensalt()),
                    registerRequest.getEmail());
            AuthData auth = authDAO.createAuth(generateToken(), user.username());
            return new RegisterResponse(auth.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public LoginResponse login(LoginRequest loginRequest) throws ResponseException {
        try {
            UserData user = userDAO.retrieveUser(loginRequest.getUsername());
            if (user == null || !BCrypt.checkpw(loginRequest.getPassword(), user.password())) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            AuthData auth = authDAO.createAuth(generateToken(), user.username());
            return new LoginResponse(auth.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void logout(LogoutRequest logoutRequest) throws ResponseException {
        try {
            AuthData auth = authDAO.retrieveAuth(logoutRequest.getAuthToken());
            if (auth == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            authDAO.deleteAuth(logoutRequest.getAuthToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
