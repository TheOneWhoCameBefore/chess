package ui;

import java.util.Arrays;

import chess.ChessGame;
import server.ResponseException;
import server.ServerFacade;

public class ChessClient {
    private final ServerFacade server;
    public State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
//                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws ResponseException {
        server.register(params[0], params[1], params[2]);
        state = State.SIGNEDIN;
        return "Registered " + params[0];
    }

    private String login(String... params) throws ResponseException {
        server.login(params[0], params[1]);
        state = State.SIGNEDIN;
        return "Logged in " + params[0];
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        server.logout();
        state = State.SIGNEDOUT;
        return "Logged out";
    }

    private String create(String... params) throws ResponseException {
        assertSignedIn();
        int gameId = server.createGame(params[0]);
        return "Created game " + params[0] + " with id " + gameId;
    }

    private String list() throws ResponseException {
        assertSignedIn();
        return server.listGames();
    }

    private String join(String... params) throws ResponseException {
        assertSignedIn();
        server.joinGame(Integer.parseInt(params[0]), ChessGame.TeamColor.valueOf(params[1].toUpperCase()));
        state = State.INGAME;
        return new PrintGame(new ChessGame()).printBoard(ChessGame.TeamColor.valueOf(params[1].toUpperCase()));
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