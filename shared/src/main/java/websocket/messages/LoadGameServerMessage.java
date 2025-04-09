package websocket.messages;

import chess.ChessGame;

public class LoadGameServerMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameServerMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() { return game; }
}
