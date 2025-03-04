package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.ResponseException;

public class DatabaseService {
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final MemoryUserDAO userDAO = new MemoryUserDAO();

    public void clear() throws ResponseException {
        try {
            authDAO.deleteAllAuth();
            gameDAO.deleteAllGames();
            userDAO.deleteAllUsers();
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: Unable to connect to the database.");
        }
    }
}
