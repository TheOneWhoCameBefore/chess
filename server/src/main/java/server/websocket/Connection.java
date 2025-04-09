package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;

import java.io.IOException;

public class Connection {
    public String username;
    public UserType userType;
    public Session session;

    public enum UserType {
        WHITE,
        BLACK,
        OBSERVER
    }

    public Connection(String username, Session session) {
        this.username = username;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg, new WriteCallback() {
            @Override
            public void writeFailed(Throwable x) {
                System.err.println("Write failed: " + x.getMessage());
            }

            @Override
            public void writeSuccess() {
                System.out.println("Message sent successfully!");
            }
        });
    }


}