package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import server.ResponseException;
import server.ServerFacade;

import javax.xml.stream.events.StartElement;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register() throws ResponseException {
        return "";
    }

    private String login(String[] params) throws ResponseException {
        return "";
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email> - create and log in to an account
                    - login <username> <password> - log in to an existing account
                    - quit - exit the application
                    - help - display this help text
                    """;
        } else if (state == State.SIGNEDIN) {
            return """
                    - create <name> - create a new game
                    - list - list all existing games
                    - join <gameId> <WHITE|BLACK> - join an existing game as the specified color
                    - observe <gameId> - join an existing game as an observer
                    - logout - log out of current account
                    - quit - exit the application
                    - help - display this help text
                    """;
        }
        return "";
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}