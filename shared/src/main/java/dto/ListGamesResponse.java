package dto;

import java.util.Collection;

public record ListGamesResponse(Collection<ListGameData> games) {
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int counter = 1;
        for (ListGameData game : games) {
            stringBuilder.append(counter).append(". ").append(game.toString()).append("\n");
            counter++;
        }
        return stringBuilder.toString();
    }
}

