package dto;

import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void validate() throws JsonSyntaxException {
        if (username == null
                || username.isEmpty()
                || password == null
                || password.isEmpty()) {
            throw new JsonSyntaxException("Missing required value");
        }
    }
}
