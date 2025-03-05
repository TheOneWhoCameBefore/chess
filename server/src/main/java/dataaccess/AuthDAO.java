package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String authToken, String username) throws DataAccessException;
    AuthData retrieveAuth(String authToken) throws DataAccessException;
//    AuthData updateAuth(String authToken, String username) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void deleteAllAuth() throws DataAccessException;
}
