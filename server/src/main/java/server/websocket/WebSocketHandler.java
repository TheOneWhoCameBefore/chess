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
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;
import java.io.IOException;


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
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        UserGameCommand.CommandType commandType =  UserGameCommand.CommandType.valueOf(jsonObject.get("commandType").getAsString());
        switch (commandType) {
            case CONNECT -> connect(jsonObject, session);
            case MAKE_MOVE -> makeMove(jsonObject, session);
            case LEAVE -> leave(jsonObject, session);
            case RESIGN -> resign(jsonObject, session);
        }
    }

    private void connect(JsonObject jsonGameCommand, Session session) throws IOException {
        ConnectUserGameCommand connectUserGameCommand  = new Gson().fromJson(jsonGameCommand, ConnectUserGameCommand.class);
        try {
            String username = authDAO.retrieveAuth(connectUserGameCommand.getAuthToken()).username();
            GameData game = gameDAO.retrieveGame(connectUserGameCommand.getGameID());
            String role;
            if (game.whiteUsername().equals(username)) {
                role = "white";
            } else if (game.blackUsername().equals(username)) {
                role = "black";
            } else {
                role = "an observer";
            }

            connections.add(username, session);

            NotificationServerMessage notificationServerMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s joined the game as %s", username, role));
            connections.broadcast(username, notificationServerMessage);

            LoadGameServerMessage loadGameServerMessage = new LoadGameServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
            connections.sendMessage(username, loadGameServerMessage);
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void makeMove(JsonObject jsonGameCommand, Session session) throws IOException {
        MakeMoveUserGameCommand makeMoveUserGameCommand = new Gson().fromJson(jsonGameCommand, MakeMoveUserGameCommand.class);
        try {
            String username = authDAO.retrieveAuth(makeMoveUserGameCommand.getAuthToken()).username();
            GameData game = gameDAO.retrieveGame(makeMoveUserGameCommand.getGameID());
            String role;
            if (game.whiteUsername().equals(username)) {
                role = "white";
            } else if (game.blackUsername().equals(username)) {
                role = "black";
            } else {
                role = "an observer";
            }

            NotificationServerMessage serverMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s made move %s", username, makeMoveUserGameCommand.getMove().toAlgebraicNotation(game.game())));
            connections.broadcast(username, serverMessage);

            game.game().makeMove(makeMoveUserGameCommand.getMove());
            gameDAO.updateGame(
                    makeMoveUserGameCommand.getGameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            );

            LoadGameServerMessage loadGameServerMessage = new LoadGameServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game.game());
            connections.broadcast("", loadGameServerMessage);
        } catch (DataAccessException | InvalidMoveException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void leave(JsonObject jsonGameCommand, Session session) throws IOException {
        LeaveUserGameCommand leaveUserGameCommand = new Gson().fromJson(jsonGameCommand, LeaveUserGameCommand.class);
        try {
            String username = authDAO.retrieveAuth(leaveUserGameCommand.getAuthToken()).username();
            GameData game = gameDAO.retrieveGame(leaveUserGameCommand.getGameID());
            String role;
            if (game.whiteUsername().equals(username)) {
                role = "white";
                gameDAO.updateGame(
                        leaveUserGameCommand.getGameID(),
                        null,
                        game.blackUsername(),
                        game.gameName(),
                        game.game()
                );
            } else if (game.blackUsername().equals(username)) {
                role = "black";
                gameDAO.updateGame(
                        leaveUserGameCommand.getGameID(),
                        game.whiteUsername(),
                        null,
                        game.gameName(),
                        game.game()
                );
            } else {
                role = "an observer";
            }

            NotificationServerMessage serverMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s (%s) left the game.", username, role));
            connections.broadcast(username, serverMessage);
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void resign(JsonObject jsonGameCommand, Session session) throws IOException {
        LeaveUserGameCommand leaveUserGameCommand = new Gson().fromJson(jsonGameCommand, LeaveUserGameCommand.class);
        try {
            String username = authDAO.retrieveAuth(leaveUserGameCommand.getAuthToken()).username();
            GameData game = gameDAO.retrieveGame(leaveUserGameCommand.getGameID());
            if (game.whiteUsername().equals(username) || game.blackUsername().equals(username)) {
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
                        String.format("%s (%s) resigned.", username, game.whiteUsername().equals(username) ? "white" : "black"));
                connections.broadcast(null, serverMessage);
            } else {
                // Tell the observer they bein silly
            }
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }
}