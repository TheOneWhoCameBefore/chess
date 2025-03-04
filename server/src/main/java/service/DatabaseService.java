package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.ResponseException;

public class DatabaseService {
    private final MemoryAuthDAO authDAO;
    private final MemoryGameDAO gameDAO;
    private final MemoryUserDAO userDAO;

    public DatabaseService(MemoryAuthDAO authDAO, MemoryGameDAO gameDAO, MemoryUserDAO userDAO) {
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
            throw new ResponseException(500, "Error: Unable to connect to the database");
        }
    }
}
