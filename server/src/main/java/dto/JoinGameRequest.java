package dto;

import chess.ChessGame;
import com.google.gson.JsonSyntaxException;

public class JoinGameRequest {
    private int gameID = -1;
    private ChessGame.TeamColor playerColor;
    private String authToken;

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void validate() throws JsonSyntaxException {
        if (gameID == -1
                || playerColor == null
                || authToken == null
                || authToken.isEmpty()) {
            throw new JsonSyntaxException("Missing required value");
        }
    }
}
