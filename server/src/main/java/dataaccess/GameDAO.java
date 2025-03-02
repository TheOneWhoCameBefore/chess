package dataaccess;

import chess.ChessGame;
import model.GameData;

public interface GameDAO {
    GameData createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException;
    GameData retrieveGame(int gameID) throws DataAccessException;
    GameData updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
}
