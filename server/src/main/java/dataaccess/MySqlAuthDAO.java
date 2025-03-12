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

    public final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS auths (
              `authToken` int NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;
}

