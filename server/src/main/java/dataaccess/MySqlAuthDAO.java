package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO {

    @Override
    public AuthData createAuth(String authToken, String username) throws DataAccessException {
        return new AuthData(authToken, "none");
    }

    @Override
    public AuthData retrieveAuth(String authToken) throws DataAccessException {
        return new AuthData(authToken, "none");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {}

    @Override
    public void deleteAllAuth() throws DataAccessException {}
}

