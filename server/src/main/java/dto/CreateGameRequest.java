package dto;

import com.google.gson.JsonSyntaxException;

public class CreateGameRequest {
    private String gameName;
    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void validate() throws JsonSyntaxException {
        if (gameName == null
                || gameName.isEmpty()
                || authToken == null
                || authToken.isEmpty()) {
            throw new JsonSyntaxException("Missing required value");
        }
    }
}
