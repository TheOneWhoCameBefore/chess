package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    @Override
    public GameData createGame(String whiteUsername, String blackUsername, String gameName, ChessGame gameObject) throws DataAccessException {
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        int gameID = MySqlDataAccess.executeUpdate(statement, whiteUsername, blackUsername, gameName, new Gson().toJson(gameObject));
        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
    }

    @Override
    public GameData retrieveGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame gameObject = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public Collection<GameData> retrieveAllGames() throws DataAccessException {
        Collection<GameData> result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame gameObject = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        result.add(new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public GameData updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame gameObject) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, whiteUsername);
                ps.setString(2, blackUsername);
                ps.setString(3, gameName);
                ps.setString(4, new Gson().toJson(gameObject));
                ps.setInt(5, gameID);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
                } else {
                    throw new DataAccessException("No game found with the provided gameID.");
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        String statement = "TRUNCATE games";
        MySqlDataAccess.executeUpdate(statement);
    }

    public final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(whiteUsername),
              INDEX(blackUsername)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """;
}
