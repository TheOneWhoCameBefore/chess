package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(String whiteUsername, String blackUsername, String gameName, ChessGame gameObject) throws DataAccessException;
    GameData retrieveGame(int gameID) throws DataAccessException;
    Collection<GameData> retrieveAllGames() throws DataAccessException;
    GameData updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame gameObject) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    void deleteAllGames() throws DataAccessException;
}
