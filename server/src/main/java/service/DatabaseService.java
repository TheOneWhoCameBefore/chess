package service;

import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.ResponseException;

public class DatabaseService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public DatabaseService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clear() throws ResponseException {
        try {
            authDAO.deleteAllAuth();
            gameDAO.deleteAllGames();
            userDAO.deleteAllUsers();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
