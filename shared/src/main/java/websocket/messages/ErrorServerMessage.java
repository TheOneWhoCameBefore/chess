package websocket.messages;

import java.security.PrivateKey;
import java.sql.PreparedStatement;

public class ErrorServerMessage extends ServerMessage {
    private final String message;

    public ErrorServerMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public String getMessage() { return message; }
}
