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
}
