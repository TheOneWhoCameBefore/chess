package service;

import static org.junit.jupiter.api.Assertions.*;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dto.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ResponseException;

public class ServiceTests {
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private MemoryUserDAO userDAO;
    private DatabaseService databaseService;
    private GameService gameService;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        databaseService = new DatabaseService(authDAO, gameDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);
    }

    @Test
    public void testRegisterSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
        RegisterResponse registerResponse = userService.register(registerRequest);

        assertEquals("user1", registerResponse.username());
    }

    @Test
    public void testRegisterFail() throws ResponseException {
        try {
            RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
            userService.register((registerRequest));
            userService.register(registerRequest);
        } catch (ResponseException e) {
            assertEquals(403, e.statusCode());
        }
    }

    @Test
    public void testLoginSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "password1");
        LoginResponse loginResponse = userService.login(loginRequest);

        assertEquals("user1", loginResponse.username());
    }

    @Test
    public void testLoginFail() throws ResponseException {
        try {
            LoginRequest loginRequest = new LoginRequest("user1", "password1");
            userService.login((loginRequest));
        } catch (ResponseException e) {
            assertEquals(401, e.statusCode());
        }
    }

    @Test
    public void testLogoutSuccess() throws ResponseException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
        RegisterResponse registerResponse = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResponse.authToken());
        userService.logout(logoutRequest);
        AuthData auth = authDAO.retrieveAuth(registerResponse.authToken());
        assertNull(auth);
    }

    @Test
    public void testLogoutFail() throws ResponseException {
        try {
            LogoutRequest logoutRequest = new LogoutRequest("12345");
            userService.logout(logoutRequest);
        } catch (ResponseException e) {
            assertEquals(401, e.statusCode());
        }
    }

    @Test
    public void testListGamesSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
        RegisterResponse registerResponse = userService.register(registerRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResponse.authToken());
        ListGamesResponse listGamesResponse = gameService.list(listGamesRequest);

        assertTrue(listGamesResponse.games().isEmpty());
    }

    @Test
    public void testListGamesFail() throws ResponseException {
        try {
            ListGamesRequest listGamesRequest = new ListGamesRequest("12345");
            gameService.list(listGamesRequest);
        } catch (ResponseException e) {
            assertEquals(401, e.statusCode());
        }
    }

    @Test
    public void testCreateGameSuccess() throws ResponseException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
        RegisterResponse registerResponse = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game1", registerResponse.authToken());
        CreateGameResponse createGameResponse = gameService.create(createGameRequest);
        assertEquals(1, createGameResponse.gameID());
    }

    @Test
    public void testCreateGameFail() throws ResponseException {
        try {
            CreateGameRequest createGameRequest = new CreateGameRequest("game1", "1234");
            gameService.create(createGameRequest);
        } catch (ResponseException e) {
            assertEquals(401, e.statusCode());
        }
    }

    @Test
    public void testJoinGameSuccess() throws ResponseException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
        RegisterResponse registerResponse = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game1", registerResponse.authToken());
        CreateGameResponse createGameResponse = gameService.create(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(createGameResponse.gameID(),
                ChessGame.TeamColor.WHITE,
                registerResponse.authToken());
        gameService.join(joinGameRequest);
        GameData game = gameDAO.retrieveGame(createGameResponse.gameID());
        assertEquals("user1", game.whiteUsername());
    }

    @Test
    public void testJoinGameFail() throws ResponseException {
        try {
            RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
            RegisterResponse registerResponse = userService.register(registerRequest);
            CreateGameRequest createGameRequest = new CreateGameRequest("game1", registerResponse.authToken());
            CreateGameResponse createGameResponse = gameService.create(createGameRequest);
            JoinGameRequest joinGameRequest = new JoinGameRequest(createGameResponse.gameID(),
                    ChessGame.TeamColor.WHITE,
                    registerResponse.authToken());
            gameService.join(joinGameRequest);
            gameService.join(joinGameRequest);
        } catch (ResponseException e) {
            assertEquals(403, e.statusCode());
        }
    }

    @Test
    public void testClear() throws ResponseException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("email1", "password1", "user1");
        RegisterResponse registerResponse = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game1", registerResponse.authToken());
        CreateGameResponse createGameResponse = gameService.create(createGameRequest);
        databaseService.clear();
        GameData game = gameDAO.retrieveGame(createGameResponse.gameID());
        AuthData auth = authDAO.retrieveAuth(registerResponse.authToken());
        UserData user = userDAO.retrieveUser("user1");
        assertNull(game);
        assertNull(auth);
        assertNull(user);
    }
}
