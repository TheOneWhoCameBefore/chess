package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MySqlUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        return new UserData(null, null, null);
    }

    @Override
    public UserData retrieveUser(String username) throws DataAccessException {
        return new UserData(null, null, null);
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {}
}
