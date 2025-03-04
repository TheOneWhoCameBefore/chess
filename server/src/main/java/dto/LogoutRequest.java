package dto;

import com.google.gson.JsonSyntaxException;

public class LogoutRequest {
    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void validate() throws JsonSyntaxException {
        if (authToken == null || authToken.isEmpty()) {
            throw new JsonSyntaxException("Missing required value");
        }
    }
}