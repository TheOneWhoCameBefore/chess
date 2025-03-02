package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username, password, email);
        users.put(username, user);
        return user;
    }

    @Override
    public UserData retrieveUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public UserData updateUser(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username, password, email);
        users.put(username, user);
        return user;
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        users.remove(username);
    }
}
