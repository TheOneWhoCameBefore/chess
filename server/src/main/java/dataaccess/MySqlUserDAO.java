package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MySqlUserDAO implements UserDAO {

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

    public final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;
}
