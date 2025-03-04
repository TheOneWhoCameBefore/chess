package service;

import java.util.UUID;
import dataaccess.*;
import dto.*;
import model.AuthData;
import model.UserData;
import server.ResponseException;

public class UserService {
    private final MemoryAuthDAO authDAO;
    private final MemoryUserDAO userDAO;

    public UserService(MemoryAuthDAO authDAO, MemoryUserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws ResponseException {
        try {
            UserData user = userDAO.retrieveUser(registerRequest.getUsername());
            if (user != null) {
                throw new ResponseException(403, "Error: already taken");
            }
            user = userDAO.createUser(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
            AuthData auth = authDAO.createAuth(generateToken(), user.username());
            return new RegisterResponse(auth.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: Unable to connect to the database");
        }
    }

    public LoginResponse login(LoginRequest loginRequest) throws ResponseException {
        try {
            UserData user = userDAO.retrieveUser(loginRequest.getUsername());
            if (user == null || !loginRequest.getPassword().equals(user.password())) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            AuthData auth = authDAO.createAuth(generateToken(), user.username());
            return new LoginResponse(auth.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: Unable to connect to the database");
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
            throw new ResponseException(500, "Error: Unable to connect to the database");
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
