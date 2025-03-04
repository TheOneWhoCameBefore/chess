package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dto.*;
import model.AuthData;
import model.GameData;
import server.ResponseException;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private final MemoryAuthDAO authDAO;
    private final MemoryGameDAO gameDAO;

    public GameService(MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResponse create(CreateGameRequest createGameRequest) throws ResponseException {
        try {
            AuthData auth = authDAO.retrieveAuth(createGameRequest.getAuthToken());
            if (auth == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            GameData game = gameDAO.createGame(null, null, createGameRequest.getGameName(), new ChessGame());
            return new CreateGameResponse(game.gameID());
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: Unable to connect to the database");
        }
    }

    public ListGamesResponse list(ListGamesRequest listGamesRequest) throws ResponseException {
        try {
            AuthData auth = authDAO.retrieveAuth(listGamesRequest.getAuthToken());
            if (auth == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            Collection<GameData> games = gameDAO.retrieveAllGames();
            Collection<ListGameData> listGames = new ArrayList<>();
            for (GameData game : games) {
                listGames.add(new ListGameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
            }
            return new ListGamesResponse(listGames);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: Unable to connect to the database");
        }
    }
//    public void join(JoinGameRequest joinGameRequest) {}
}
