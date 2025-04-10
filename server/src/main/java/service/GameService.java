package service;

import chess.ChessGame;
import dataaccess.*;
import dto.*;
import model.AuthData;
import model.GameData;
import server.ResponseException;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResponse create(CreateGameRequest createGameRequest) throws ResponseException {
        try {
            AuthData auth;
            try {
                auth = authDAO.retrieveAuth(createGameRequest.getAuthToken());
            } catch (DataAccessException e) {
                auth = null;
            }
            if (auth == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            GameData game = gameDAO.createGame(null, null, createGameRequest.getGameName(), new ChessGame());
            return new CreateGameResponse(game.gameID());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public ListGamesResponse list(ListGamesRequest listGamesRequest) throws ResponseException {
        try {
            AuthData auth;
            try {
                auth = authDAO.retrieveAuth(listGamesRequest.getAuthToken());
            } catch (DataAccessException e) {
                auth = null;
            }
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
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void join(JoinGameRequest joinGameRequest) throws ResponseException {
        try {
            AuthData auth;
            try { auth = authDAO.retrieveAuth(joinGameRequest.getAuthToken()); }
            catch (DataAccessException e) { auth = null; }
            if (auth == null) { throw new ResponseException(401, "Error: unauthorized"); }

            GameData game;
            try { game = gameDAO.retrieveGame(joinGameRequest.getGameID()); }
            catch (DataAccessException e) {game = null; }
            if (game == null) { throw new ResponseException(400, "Error: bad request"); }

            if ((joinGameRequest.getPlayerColor() == WHITE && game.whiteUsername() != null)
                    || (joinGameRequest.getPlayerColor() == BLACK && game.blackUsername() != null)) {
                throw new ResponseException(403, "Error: already taken");
            }
            gameDAO.updateGame(joinGameRequest.getGameID(),
                    (joinGameRequest.getPlayerColor() == WHITE) ? auth.username() : game.whiteUsername(),
                    (joinGameRequest.getPlayerColor() == BLACK) ? auth.username() : game.blackUsername(),
                    game.gameName(),
                    game.game());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
