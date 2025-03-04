package server;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import spark.*;
import service.*;

public class Server {
    private final DatabaseService databaseService = new DatabaseService();
    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) {
        databaseService.clear();
        res.status(200);
        return "";
    }
}
