package websocket.messages;

public class NotificationServerMessage extends ServerMessage {
    private final String message;

    public NotificationServerMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public String getMessage() { return message; }

    @Override
    public String toString() {
        return message;
    }
}
