package dto;

public record ListGameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
    @Override
    public String toString() {
        return String.format("Name: %s    White: %s    Black: %s", gameName, whiteUsername, blackUsername);
    }
};
