package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dto.LoginRequest;
import dto.LoginResponse;
import dto.RegisterRequest;
import dto.RegisterResponse;
import spark.*;
import service.*;

import java.lang.annotation.Repeatable;

public class Server {
    private static final Gson serializer = new Gson();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final DatabaseService databaseService = new DatabaseService(authDAO, gameDAO, userDAO);
//    private final GameService gameService = new GameService(authDAO, gameDAO, userDAO);
    private final UserService userService = new UserService(authDAO, gameDAO, userDAO);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
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
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    private Object clear(Request req, Response res) throws ResponseException {
        databaseService.clear();
        res.status(200);
        return "";
    }

    private Object register(Request req, Response res) throws ResponseException {
        try {
            RegisterRequest registerRequest = serializer.fromJson(req.body(), RegisterRequest.class);
            registerRequest.validate();
            RegisterResponse registerResponse = userService.register(registerRequest);
            return serializer.toJson(registerResponse);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }
    }

    private Object login(Request req, Response res) throws ResponseException {
        try {
            LoginRequest loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
            loginRequest.validate();
            LoginResponse registerResponse = userService.login(loginRequest);
            return serializer.toJson(registerResponse);
        } catch (JsonSyntaxException e) {
            throw new ResponseException(400, "Error: Bad Request");
        }
    }
}
