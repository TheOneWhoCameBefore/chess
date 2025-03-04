package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dto.CreateGameResponse;
import dto.JoinGameRequest;
import dto.ListGamesRequest;
import dto.ListGamesResponse;
import model.AuthData;
import model.GameData;
import server.ResponseException;

import java.util.Collection;

public class GameService {
    private final MemoryAuthDAO authDAO;
    private final MemoryGameDAO gameDAO;

    public GameService(MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
//    public CreateGameResponse create(CreateGameResponse) {}
    public ListGamesResponse list(ListGamesRequest listGamesRequest) throws ResponseException {
        try {
            AuthData auth = authDAO.retrieveAuth(listGamesRequest.getAuthToken());
            if (auth == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            Collection<GameData> games = gameDAO.retrieveAllGames();
            return new ListGamesResponse(games);
        } catch (DataAccessException e) {
            throw new ResponseException(500, "Error: Unable to connect to the database");
        }
    }
//    public void join(JoinGameRequest joinGameRequest) {}
}
