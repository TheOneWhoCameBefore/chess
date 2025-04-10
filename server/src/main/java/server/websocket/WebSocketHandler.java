package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectUserGameCommand;
import websocket.commands.LeaveUserGameCommand;
import websocket.commands.MakeMoveUserGameCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
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
            if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
                role = "white";
            } else if (game.blackUsername() != null && game.blackUsername().equals(username)) {
                role = "black";
            } else {
                role = "an observer";
            }


            switch (commandType) {
                case CONNECT -> connect(username, game, role, message, session);
                case MAKE_MOVE -> makeMove(username, game, role, message, session);
                case LEAVE -> leave(username, game, role, message, session);
                case RESIGN -> resign(username, game, role, message, session);
            }
        } catch (IOException | DataAccessException e) {
            ErrorServerMessage errorServerMessage = new ErrorServerMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    String.format("Error: %s", e.getMessage()));
            session.getRemote().sendString(errorServerMessage.toString());
        }
    }

    private void connect(String username, GameData game, String role, String message, Session session) throws IOException {
        ConnectUserGameCommand connectUserGameCommand  = new Gson().fromJson(message, ConnectUserGameCommand.class);
        connections.add(username, session);

        NotificationServerMessage notificationServerMessage = new NotificationServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s joined the game as %s", username, role));
        connections.broadcast(username, notificationServerMessage);

        LoadGameServerMessage loadGameServerMessage = new LoadGameServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
        connections.sendMessage(username, loadGameServerMessage);
    }

    private void makeMove(String username, GameData game, String role, String message, Session session) throws IOException {
        MakeMoveUserGameCommand makeMoveUserGameCommand = new Gson().fromJson(message, MakeMoveUserGameCommand.class);

        try {
            game.game().makeMove(makeMoveUserGameCommand.getMove());
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
        connections.broadcast("", loadGameServerMessage);

        NotificationServerMessage moveMessage = new NotificationServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s made move %s", username, makeMoveUserGameCommand.getMove().toAlgebraicNotation()));
        connections.broadcast(username, moveMessage);

        if (game.game().isInCheck(game.game().getTeamTurn())) {
            NotificationServerMessage checkMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "Check!");
            connections.broadcast("", checkMessage);
        }

        String endMessage = null;
        if (game.game().isInCheckmate(game.game().getTeamTurn())) { endMessage = "Checkmate!"; }
        if (game.game().isInStalemate(game.game().getTeamTurn())) { endMessage = "Stalemate!"; }

        if (endMessage != null ){
            NotificationServerMessage checkMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    endMessage);
            connections.broadcast("", checkMessage);
        }

    }

    private void leave(String username, GameData game, String role, String message, Session session) throws IOException {
        LeaveUserGameCommand leaveUserGameCommand = new Gson().fromJson(message, LeaveUserGameCommand.class);
        try {
            if (Objects.equals(role, "white")) {
                gameDAO.updateGame(
                        leaveUserGameCommand.getGameID(),
                        null,
                        game.blackUsername(),
                        game.gameName(),
                        game.game()
                );
            } else if (Objects.equals(role, "black")) {
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
            connections.broadcast(username, serverMessage);
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void resign(String username, GameData game, String role, String message, Session session) throws IOException {
        LeaveUserGameCommand leaveUserGameCommand = new Gson().fromJson(message, LeaveUserGameCommand.class);
        try {
            if (Objects.equals(role, "white") || Objects.equals(role, "black")) {
                game.game().setFinished();
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
                connections.broadcast(null, serverMessage);
            } else {
                throw new IOException("Observers can't resign.");
            }
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }
}