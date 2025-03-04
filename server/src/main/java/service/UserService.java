package service;

import java.util.UUID;
import dataaccess.*;
import dto.*;
import model.AuthData;
import model.UserData;
import server.ResponseException;

public class UserService {
    private final MemoryAuthDAO authDAO;
    private final MemoryGameDAO gameDAO;
    private final MemoryUserDAO userDAO;

    public UserService(MemoryAuthDAO authDAO, MemoryGameDAO gameDAO, MemoryUserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
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
//    public LoginResponse login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
