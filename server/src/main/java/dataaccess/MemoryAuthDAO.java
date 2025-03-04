package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public AuthData createAuth(String authToken, String username) throws DataAccessException {
        AuthData auth = new AuthData(authToken, username);
        auths.put(authToken, auth);
        return auth;
    }

    @Override
    public AuthData retrieveAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public AuthData updateAuth(String authToken, String username) throws DataAccessException {
        AuthData auth = new AuthData(authToken, username);
        auths.put(authToken, auth);
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public void deleteAllAuth() throws DataAccessException {
        auths.clear();
    }
}

