package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData createUser(String username, String password, String email) throws DataAccessException;
    UserData retrieveUser(String username) throws DataAccessException;
    UserData updateUser(String username, String password, String email) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    void deleteAllUsers() throws DataAccessException;
}
