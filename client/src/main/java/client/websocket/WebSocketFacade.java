package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import server.ResponseException;
import ui.PrintGame;
import ui.Repl;
import websocket.commands.*;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;


public class WebSocketFacade extends Endpoint {

    private final Session session;
    private Repl repl;
    private String role;


    public WebSocketFacade(String url, Repl repl, String authToken) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.repl = repl;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage.ServerMessageType messageType = serverMessage.getServerMessageType();

                    switch (messageType) {
                        case NOTIFICATION -> repl.notify(new Gson().fromJson(message, NotificationServerMessage.class).getMessage());
                        case LOAD_GAME -> {
                            ChessGame game = new Gson().fromJson(message, LoadGameServerMessage.class).getGame();
                            repl.getClient().setGame(game);
                            repl.loadGame(game, role);
                        }
                        case ERROR -> repl.error(new Gson().fromJson(message, ErrorServerMessage.class).getMessage());
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID, String role) throws ResponseException {
        try {
            ConnectUserGameCommand connectUserGameCommand = new ConnectUserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectUserGameCommand));
            this.role = role;
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void reprintGame(ChessGame game) throws ResponseException {
        System.out.println("\n" + new PrintGame(game).printBoard(Objects.equals(role, "black") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE, null));
        repl.printPrompt();
    }

    public void showMoves(ChessGame game, ChessPosition position) throws ResponseException {
        System.out.println("\n" + new PrintGame(game).printBoard(Objects.equals(role, "black") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE, position));
        repl.printPrompt();
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            MakeMoveUserGameCommand makeMoveUserGameCommand = new MakeMoveUserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveUserGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        try {
            ResignUserGameCommand resignUserGameCommand = new ResignUserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resignUserGameCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }


    public void leave(String authToken, int gameID) throws ResponseException {
        try {
            LeaveUserGameCommand leaveUserGameCommand = new LeaveUserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveUserGameCommand));
            this.session.close();
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

}