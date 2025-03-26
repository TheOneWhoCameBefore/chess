package dto;

public record ListGameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
    @Override
    public String toString() {
        return String.format("ID: %s    Name: %s    White: %s    Black: %s", gameID, gameName, whiteUsername, blackUsername);
    }
};
