package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dto.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null);
    }

    public String register(String username, String password, String email) throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest(email, password, username);
        RegisterResponse registerResponse = makeRequest("POST", "/user", registerRequest, RegisterResponse.class);
        authToken = registerResponse.authToken();
        return authToken;
    }

    public String login(String username, String password) throws ResponseException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResponse loginResponse = makeRequest("POST", "/session", loginRequest, LoginResponse.class);
        authToken = loginResponse.authToken();
        return authToken;
    }

    public void logout() throws ResponseException {
        makeRequest("DELETE", "/session", null, null);
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        return makeRequest("GET", "/game", null, ListGamesResponse.class);
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName, authToken);
        CreateGameResponse createGameResponse = makeRequest("POST", "/game", createGameRequest, CreateGameResponse.class);
        return createGameResponse.gameID();
    }

    public void joinGame(String authToken, int gameId, ChessGame.TeamColor color) throws ResponseException {
        JoinGameRequest joinGameRequest = new JoinGameRequest(gameId, color, authToken);
        makeRequest("PUT", "/game", joinGameRequest, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {http.setRequestProperty("authorization", authToken);}
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readResponse(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readResponse(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status == 200;
    }
}
