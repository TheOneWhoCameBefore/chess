package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTests {
    private static AuthDAO authDAO;
    private static GameDAO gameDAO;
    private static UserDAO userDAO;

    @BeforeAll
    public static void setUp() {
        try {
            MySqlDataAccess dataAccess = new MySqlDataAccess();
            authDAO = dataAccess.authDAO;
            gameDAO = dataAccess.gameDAO;
            userDAO = dataAccess.userDAO;
        } catch (Throwable e) {
            System.out.printf("Unable to connect to database: %s%n", e.getMessage());
            throw new RuntimeException("Failed to set up tests.", e);
        }
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        authDAO.deleteAllAuth();
        gameDAO.deleteAllGames();
        userDAO.deleteAllUsers();
    }

    @Test
    public void testCreateAuthSuccess() throws DataAccessException {
        String authToken = "testAuthToken";
        String username = "testUsername";
        authDAO.createAuth(authToken, username);

        AuthData auth = authDAO.retrieveAuth(authToken);
        assertEquals(authToken, auth.authToken());
    }

    @Test
    public void testCreateAuthFail() throws DataAccessException {
        String authToken = "testAuthToken";
        String username = "testUsername";
        authDAO.createAuth(authToken, username);
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(authToken, username);
        });
    }

    @Test
    public void testRetrieveAuthSuccess() throws DataAccessException {
        String authToken = "testRetrieveAuthToken";
        String username = "testRetrieveUsername";
        authDAO.createAuth(authToken, username);

        AuthData auth = authDAO.retrieveAuth(authToken);
        assertEquals(username, auth.username());
    }

    @Test
    public void testRetrieveAuthFail() throws DataAccessException {
        assertNull(authDAO.retrieveAuth("testRetrieveAuthToken"));
    }

    @Test
    public void testDeleteAuthSuccess() throws DataAccessException {
        String authToken = "testAuthToken";
        String username = "testUsername";
        authDAO.createAuth(authToken, username);

        authDAO.deleteAuth(authToken);
        assertNull(authDAO.retrieveAuth("testAuthToken"));
    }

    @Test
    public void testDeleteAllAuthSuccess() throws DataAccessException {
        authDAO.createAuth("testAuthToken1", "testUsername1");
        authDAO.createAuth("testAuthToken2", "testUsername2");
        authDAO.createAuth("testAuthToken3", "testUsername3");

        authDAO.deleteAllAuth();
        assertNull(authDAO.retrieveAuth("testAuthToken1"));
        assertNull(authDAO.retrieveAuth("testAuthToken2"));
        assertNull(authDAO.retrieveAuth("testAuthToken3"));
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {
        gameDAO.createGame(null, null, "testGame", new ChessGame());

        GameData game = gameDAO.retrieveGame(1);
        assertEquals("testGame", game.gameName());
    }

    @Test
    public void testCreateGameFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null, null, null, null);
        });
    }

    @Test
    public void testRetrieveGameSuccess() throws DataAccessException {
        gameDAO.createGame(null, null, "testGame", new ChessGame());

        GameData game = gameDAO.retrieveGame(1);
        assertEquals("testGame", game.gameName());
    }

    @Test
    public void testRetrieveGameFail() throws DataAccessException {
        assertNull(gameDAO.retrieveGame(1));
    }

    @Test
    public void testRetrieveAllGamesSuccess() throws DataAccessException {
        gameDAO.createGame(null, null, "testGame1", new ChessGame());
        gameDAO.createGame(null, null, "testGame2", new ChessGame());
        gameDAO.createGame(null, null, "testGame3", new ChessGame());

        Collection<GameData> games = gameDAO.retrieveAllGames();
        List<GameData> gameList = games.stream().toList();
        assertEquals("testGame1", gameList.getFirst().gameName());
    }

    @Test
    public void testRetrieveAllGamesFail() throws DataAccessException {
        Collection<GameData> games = gameDAO.retrieveAllGames();
        assertTrue(games.isEmpty());
    }

    @Test
    public void testUpdateGameSuccess() throws DataAccessException {
        GameData oldGame = gameDAO.createGame(null, null, "testGame", new ChessGame());
        gameDAO.updateGame(1, "testUsername1", "testUsername2", "testGame", oldGame.game());

        GameData newGame = gameDAO.retrieveGame(1);
        assertEquals("testGame", newGame.gameName());
        assertEquals("testUsername1", newGame.whiteUsername());
        assertEquals("testUsername2", newGame.blackUsername());
    }

    @Test
    public void testUpdateGameFail() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(1, "testUsername1", "testUsername2", "testGame", new ChessGame());
        });
    }

    @Test
    public void testDeleteAllGamesSuccess() throws DataAccessException {
        gameDAO.createGame(null, null, "testGame1", new ChessGame());
        gameDAO.createGame(null, null, "testGame2", new ChessGame());
        gameDAO.createGame(null, null, "testGame3", new ChessGame());

        gameDAO.deleteAllGames();
        assertNull(gameDAO.retrieveGame(1));
        assertNull(gameDAO.retrieveGame(2));
        assertNull(gameDAO.retrieveGame(3));
    }

    @Test
    public void testCreateUserSuccess() throws DataAccessException {
        userDAO.createUser("testUsername", "testPassword", "testEmail");

        UserData user = userDAO.retrieveUser("testUsername");
        assertEquals("testEmail", user.email());
    }

    @Test
    public void testCreateUserFail() throws DataAccessException {
        String username = "testUsername";
        String password = "testPassword";
        String email = "testEmail";
        userDAO.createUser(username, password, email);
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(username, password, email);
        });
    }

    @Test
    public void testRetrieveUserSuccess() throws DataAccessException {
        userDAO.createUser("testUsername", "testPassword", "testEmail");

        UserData user = userDAO.retrieveUser("testUsername");
        assertEquals("testEmail", user.email());
    }

    @Test
    public void testRetrieveUserFail() throws DataAccessException {
        assertNull(userDAO.retrieveUser("testUsername"));
    }

    @Test
    public void testDeleteAllUsersSuccess() throws DataAccessException {
        userDAO.createUser("testUsername1", "testPassword1", "testEmail1");
        userDAO.createUser("testUsername2", "testPassword2", "testEmail2");
        userDAO.createUser("testUsername3", "testPassword3", "testEmail3");

        userDAO.deleteAllUsers();
        assertNull(userDAO.retrieveUser("testUsername1"));
        assertNull(userDAO.retrieveUser("testUsername2"));
        assertNull(userDAO.retrieveUser("testUsername3"));
    }
}
