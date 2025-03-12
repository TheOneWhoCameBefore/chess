package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {
    private int nextID = 1;

    @Override
    public GameData createGame(String whiteUsername, String blackUsername, String gameName, ChessGame gameObject) throws DataAccessException {
        return new GameData(nextID, null, null, null, null);
    }

    @Override
    public GameData retrieveGame(int gameID) throws DataAccessException {
        return new GameData(nextID, null, null, null, null);
    }

    public Collection<GameData> retrieveAllGames() throws DataAccessException {
        return new ArrayList<>();
    }

    @Override
    public GameData updateGame(int gameID, String whiteUsername, String blackUsername) throws DataAccessException {
        return new GameData(nextID, null, null, null, null);
    }

    @Override
    public void deleteAllGames() throws DataAccessException {}

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
