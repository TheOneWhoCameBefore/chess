package ui;

import java.util.Arrays;
import java.util.List;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.websocket.WebSocketFacade;
import dto.ListGameData;
import dto.ListGamesResponse;
import server.ResponseException;
import server.ServerFacade;

public class ChessClient {
    private final String serverUrl;
    private final ServerFacade server;
    private final Repl repl;
    private WebSocketFacade webSocketFacade;
    public State state = State.SIGNEDOUT;
    private List<ListGameData> mostRecentGamesList;
    private String authToken;
    private int gameId;
    private ChessGame game;

    public ChessClient(String serverUrl, Repl messageHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = messageHandler;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "leave" -> leave();
                case "reprint" -> reprintGame();
                case "resign" -> resign();
                case "quit" -> "quit";
                default -> defaultHandler(cmd);
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String defaultHandler(String cmd) throws ResponseException {
        if (state != State.INGAME) {
            return help();
        }
        if (cmd.length() == 2) {
            return showMoves(cmd);
        } else if (cmd.length() == 4) {
            return makeMove(cmd);
        }

        throw new ResponseException(500, "Unknown command");
    }

    private String register(String... params) throws ResponseException {
        try {
            this.authToken = server.register(params[0], params[1], params[2]);
            state = State.SIGNEDIN;
            return "Registered " + params[0];
        } catch (IndexOutOfBoundsException ex) {
            throw new ResponseException(500, "Error: missing username, password, or email");
        }
    }

    private String login(String... params) throws ResponseException {
        try {
            this.authToken = server.login(params[0], params[1]);
            state = State.SIGNEDIN;
            return "Logged in " + params[0];
        } catch (IndexOutOfBoundsException ex) {
            throw new ResponseException(500, "Error: missing username or password");
        }
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        server.logout();
        state = State.SIGNEDOUT;
        return "Logged out";
    }

    private String create(String... params) throws ResponseException {
        assertSignedIn();
        try {
            int gameId = server.createGame(authToken, params[0]);
            return "Created game " + params[0] + " with id " + gameId + "\n" + list();
        } catch (IndexOutOfBoundsException ex) {
            throw new ResponseException(500, "Error: missing game name");
        }
    }

    private String list() throws ResponseException {
        assertSignedIn();
        ListGamesResponse listGamesResponse = server.listGames(authToken);
        mostRecentGamesList = List.copyOf(listGamesResponse.games());
        return listGamesResponse.toString();
    }

    private String join(String... params) throws ResponseException {
        assertSignedIn();
        try {
            gameId = mostRecentGamesList.get(Integer.parseInt(params[0]) - 1).gameID();
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
            server.joinGame(authToken, gameId, color);
            state = State.INGAME;
            webSocketFacade = new WebSocketFacade(serverUrl, repl, authToken);
            webSocketFacade.connect(authToken, gameId, color == ChessGame.TeamColor.WHITE ? "white" : "black");
            return String.format("Joined game %s as %s", gameId, color);
        } catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException ex) {
            throw new ResponseException(500, "Error: unknown game or color. Try listing all games with \"list\"");
        }
    }

    private String observe(String... params) throws ResponseException {
        assertSignedIn();
        try {
            int gameId = mostRecentGamesList.get(Integer.parseInt(params[0]) - 1).gameID();
            state = State.INGAME;
            webSocketFacade = new WebSocketFacade(serverUrl, repl, authToken);
            webSocketFacade.connect(authToken, gameId, "an observer");
            return String.format("Joined game %s as an observer", gameId);
        } catch (IndexOutOfBoundsException | IllegalArgumentException | NullPointerException ex) {
            throw new ResponseException(500, "Error: unknown game. Try listing all games with \"list\"");
        }
    }


    private String leave() throws ResponseException {
        assertInGame();
        webSocketFacade.leave(authToken, gameId);
        state = State.SIGNEDIN;
        return "Left game";
    }

    private String reprintGame() throws ResponseException {
        assertInGame();
        webSocketFacade.reprintGame(game);
        return "Reprinted game";
    }

    private String resign() throws ResponseException {
        assertInGame();
        webSocketFacade.resign(authToken, gameId);
        return "Resigned";
    }

    private String showMoves(String algebraicPosition) throws ResponseException {
        int column = algebraicPosition.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(algebraicPosition.charAt(1));
        ChessPosition position = new ChessPosition(row, column);
        webSocketFacade.showMoves(game, position);
        return "Showed moves";
    }

    private String makeMove(String algebraicNotation) throws ResponseException {
        ChessMove move = ChessMove.fromAlgebraicNotation(algebraicNotation);
        webSocketFacade.makeMove(authToken, gameId, move);
        return "Made move";
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
        if (state != State.SIGNEDIN) {
            String message = "";
            if (state == State.SIGNEDOUT) {
                message = "You must sign in";
            } else if (state == State.INGAME) {
                message = "You cannot do that while in game";
            }
            throw new ResponseException(400, message);
        }
    }

    private void assertInGame() throws ResponseException {
        if (state != State.INGAME) {
            String message = "";
            if (state == State.SIGNEDOUT) {
                message = "You must sign in";
            } else if (state == State.SIGNEDIN) {
                message = "You must join a game to do that";
            }
            throw new ResponseException(400, message);
        }
    }


}