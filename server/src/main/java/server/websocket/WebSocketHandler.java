package server.websocket;

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
import websocket.commands.MakeMoveUserGameCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;
import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

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
//            case LEAVE -> leave(jsonObject, session);
//            case RESIGN -> resign(jsonObject, session);
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
            NotificationServerMessage serverMessage = new NotificationServerMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s joined the game as %s", username, role));
            connections.broadcast(null, serverMessage);
        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void makeMove(JsonObject jsonGameCommand, Session session) throws IOException {
        MakeMoveUserGameCommand makeMoveUserGameCommand = new Gson().fromJson(jsonGameCommand, MakeMoveUserGameCommand.class);
        try {
            String username = authDAO.retrieveAuth(makeMoveUserGameCommand.getAuthToken()).username();

        } catch (DataAccessException e) {
            throw new IOException(e.getMessage());
        }
    }

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new ServerMessage(ServerMessage.ServerMessageType.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new ServerMessage(ServerMessage.ServerMessageType.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}