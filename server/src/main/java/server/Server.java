package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.*;
import dto.*;
import spark.*;
import service.*;

public class Server {
    private static final Gson SERIALIZER = new Gson();
    private DatabaseService databaseService;
    private GameService gameService;
    private UserService userService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        try {
            MySqlDataAccess dataAccess = new MySqlDataAccess();
            databaseService = new DatabaseService(dataAccess.authDAO, dataAccess.gameDAO, dataAccess.userDAO);
            gameService = new GameService(dataAccess.authDAO, dataAccess.gameDAO);
            userService = new UserService(dataAccess.authDAO, dataAccess.userDAO);
        } catch (Throwable e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
            return -1;
        }

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(ex.toJson());
    }

    private Object clear(Request req, Response res) throws ResponseException {
        databaseService.clear();
        res.status(200);
        return "";
    }

    private Object register(Request req, Response res) throws ResponseException {
        try {
            RegisterRequest registerRequest = SERIALIZER.fromJson(req.body(), RegisterRequest.class);
            registerRequest.validate();
            RegisterResponse registerResponse = userService.register(registerRequest);
            return SERIALIZER.toJson(registerResponse);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }
    }

    private Object login(Request req, Response res) throws ResponseException {
        try {
            LoginRequest loginRequest = SERIALIZER.fromJson(req.body(), LoginRequest.class);
            loginRequest.validate();
            LoginResponse registerResponse = userService.login(loginRequest);
            return SERIALIZER.toJson(registerResponse);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }
    }

    private Object logout(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            logoutRequest.validate();
            userService.logout(logoutRequest);
            return "";
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            listGamesRequest.validate();
            ListGamesResponse listGamesResponse = gameService.list(listGamesRequest);
            return SERIALIZER.toJson(listGamesResponse);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }
    }

    private Object createGame(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            CreateGameRequest createGameRequest = SERIALIZER.fromJson(req.body(), CreateGameRequest.class);
            createGameRequest.setAuthToken(authToken);
            createGameRequest.validate();
            CreateGameResponse createGameResponse = gameService.create(createGameRequest);
            return SERIALIZER.toJson(createGameResponse);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        try {
            String authToken = req.headers("authorization");
            JoinGameRequest joinGameRequest = SERIALIZER.fromJson(req.body(), JoinGameRequest.class);
            joinGameRequest.setAuthToken(authToken);
            joinGameRequest.validate();
            gameService.join(joinGameRequest);
            return "";
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }
    }
}
