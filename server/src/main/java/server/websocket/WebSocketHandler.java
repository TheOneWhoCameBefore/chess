package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.LeaveUserGameCommand;
import websocket.commands.MakeMoveUserGameCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        UserGameCommand.CommandType commandType = command.getCommandType();
        String username;
        GameData game;
        String role;
        try {
            username = authDAO.retrieveAuth(command.getAuthToken()).username();
            game = gameDAO.retrieveGame(command.getGameID());
            if (Objects.equals(game.whiteUsername(), username)) {
                role = "white";
            } else if (Objects.equals(game.blackUsername(), username)) {
                role = "black";
            } else {
                role = "an observer";
            }


            switch (commandType) {
                case CONNECT -> connect(username, game, role, session);
                case MAKE_MOVE -> makeMove(username, game, role, message);
                case LEAVE -> leave(username, game, role, message);
                case RESIGN -> resign(username, game, role, message);
            }
        } catch (IOException | DataAccessException e) {
            ErrorServerMessage errorServerMessage = new ErrorServerMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    String.format("Error: %s", e.getMessage()));
            session.getRemote().sendString(errorServerMessage.toString());
        }
    }

    private void connect(String username, GameData game, String role, Session session) throws IOException {
        int gameId = game.gameID();
        connections.add(username, gameId, session);

        NotificationServerMessage notificationServerMessage = new NotificationServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s joined the game as %s", username, role));
        connections.broadcast(username, gameId, notificationServerMessage);

        LoadGameServerMessage loadGameServerMessage = new LoadGameServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
        connections.sendMessage(username, gameId, loadGameServerMessage);
    }

    private void makeMove(String username, GameData game, String role, String message) throws IOException {
        MakeMoveUserGameCommand makeMoveUserGameCommand = new Gson().fromJson(message, MakeMoveUserGameCommand.class);
        int gameId = makeMoveUserGameCommand.getGameID();

        ChessGame.TeamColor color;
        switch (role) {
            case "white" -> color = ChessGame.TeamColor.WHITE;
            case "black" -> color = ChessGame.TeamColor.BLACK;
            default -> throw new IOException("Observers cannot make moves");
        }

        try {
            if (color == game.game().getTeamTurn()) { game.game().makeMove(makeMoveUserGameCommand.getMove()); }
            else { throw new IOException("It is not your turn"); }
            gameDAO.updateGame(
                    makeMoveUserGameCommand.getGameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            );
        } catch (InvalidMoveException | DataAccessException e) {
            throw new IOException(e.getMessage());
        }

        LoadGameServerMessage loadGameServerMessage = new LoadGameServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
        connections.broadcast("", gameId, loadGameServerMessage);

        NotificationServerMessage moveMessage = new NotificationServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s made move %s", username, makeMoveUserGameCommand.getMove().toAlgebraicNotation()));
        connections.broadcast(username, gameId, moveMessage);

        String endMessage = null;
        if (game.game().isInCheckmate(game.game().getTeamTurn())) { endMessage = "Checkmate!"; }
        if (game.game().isInStalemate(game.game().getTeamTurn())) { endMessage = "Stalemate!"; }

        if (endMessage != null ){
            game.game().setFinished();
            NotificationServerMessage checkMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    endMessage);
            connections.broadcast("", gameId, checkMessage);
        } else if (game.game().isInCheck(game.game().getTeamTurn())) {
            NotificationServerMessage checkMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "Check!");
            connections.broadcast("", gameId, checkMessage);
        }

    }

    private void leave(String username, GameData game, String role, String message) throws IOException {
        LeaveUserGameCommand leaveUserGameCommand = new Gson().fromJson(message, LeaveUserGameCommand.class);
        int gameId = leaveUserGameCommand.getGameID();

        try {
            if (Objects.equals(role, "white")) {
                connections.remove(username);
                gameDAO.updateGame(
                        leaveUserGameCommand.getGameID(),
                        null,
                        game.blackUsername(),
                        game.gameName(),
                        game.game()
                );
            } else if (Objects.equals(role, "black")) {
                connections.remove(username);
                gameDAO.updateGame(
                        leaveUserGameCommand.getGameID(),
                        game.whiteUsername(),
                        null,
                        game.gameName(),
                        game.game()
                );
            }

            NotificationServerMessage serverMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s (%s) left the game.", username, role));
            connections.broadcast(username, gameId, serverMessage);
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void resign(String username, GameData game, String role, String message) throws IOException {
        LeaveUserGameCommand leaveUserGameCommand = new Gson().fromJson(message, LeaveUserGameCommand.class);
        int gameId = leaveUserGameCommand.getGameID();

        try {
            if (Objects.equals(role, "white") || Objects.equals(role, "black")) {
                if (game.game().inProgress) {
                    game.game().setFinished();
                } else {
                    throw new IOException("Game is already over");
                }
                gameDAO.updateGame(
                        leaveUserGameCommand.getGameID(),
                        game.whiteUsername(),
                        game.blackUsername(),
                        game.gameName(),
                        game.game()
                );

                NotificationServerMessage serverMessage = new NotificationServerMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        String.format("%s (%s) resigned.", username, role));
                connections.broadcast(null, gameId, serverMessage);
            } else {
                throw new IOException("Observers can't resign.");
            }
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }
}