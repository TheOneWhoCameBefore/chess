package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextID = 1;
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public GameData createGame(String whiteUsername, String blackUsername, String gameName, ChessGame gameObject) throws DataAccessException {
        GameData game = new GameData(nextID++, whiteUsername, blackUsername, gameName, gameObject);
        games.put(nextID++, game);
        return game;
    }

    @Override
    public GameData retrieveGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public GameData updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame gameObject) throws DataAccessException {
        GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, gameObject);
        games.put(gameID, game);
        return game;
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        games.remove(gameID);
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        games.clear();
    }
}
