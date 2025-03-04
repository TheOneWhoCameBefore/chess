package dto;

import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public RegisterRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void validate() throws JsonSyntaxException {
        if (username == null
                || username.isEmpty()
                || password == null
                || password.isEmpty()
                || email == null
                || email.isEmpty()) {
            throw new JsonSyntaxException("Missing required value");
        }
    }
}
