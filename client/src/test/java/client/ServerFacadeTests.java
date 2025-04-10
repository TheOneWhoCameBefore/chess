package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {facade.clear();}

    @Test
    public void testRegisterSuccess() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> {facade.register("testUsername", "testPassword", "testEmail");});
    }

    @Test
    public void testRegisterFail() throws ResponseException {
        facade.register("testUsername", "testPassword", "testEmail");
        Assertions.assertThrows(ResponseException.class, () -> {facade.register("testUsername", "testPassword", "testEmail");});
    }

    @Test
    public void testLoginSuccess() throws ResponseException {
        facade.register("testUsername", "testPassword", "testEmail");
        Assertions.assertDoesNotThrow(() -> {facade.login("testUsername", "testPassword");});
    }

    @Test
    public void testLoginFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {facade.login("testUsername", "testPassword");});
    }

    @Test
    public void testLogoutSuccess() throws ResponseException {
        facade.register("testUsername", "testPassword", "testEmail");
        facade.login("testUsername", "testPassword");
        Assertions.assertDoesNotThrow(() -> {facade.logout();});
    }

    @Test
    public void testLogoutFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {facade.logout();});
    }

    @Test
    public void testListGamesSuccess() throws ResponseException {
        facade.register("testUsername", "testPassword", "testEmail");
        String authToken = facade.login("testUsername", "testPassword");
        Assertions.assertDoesNotThrow(() -> {facade.listGames(authToken);});
    }

    @Test
    public void testListGamesFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {facade.listGames("fakeAuthToken");});
    }

    @Test
    public void testCreateGameSuccess() throws ResponseException {
        facade.register("testUsername", "testPassword", "testEmail");
        String authToken = facade.login("testUsername", "testPassword");
        Assertions.assertDoesNotThrow(() -> {facade.createGame(authToken, "testGame");});
    }

    @Test
    public void testCreateGameFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {facade.createGame("fakeAuthToken", "testGame");});
    }

    @Test
    public void testJoinGameSuccess() throws ResponseException {
        facade.register("testUsername", "testPassword", "testEmail");
        String authToken = facade.login("testUsername", "testPassword");
        facade.createGame(authToken, "testGame");
        Assertions.assertDoesNotThrow(() -> {facade.joinGame(authToken, 1, ChessGame.TeamColor.WHITE);});
    }

    @Test
    public void testJoinGameFail() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {facade.joinGame("fakeAuthToken", 1, ChessGame.TeamColor.WHITE);});
    }
}
