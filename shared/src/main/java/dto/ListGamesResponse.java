package dto;

import java.util.Collection;

public record ListGamesResponse(Collection<ListGameData> games) {
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ListGameData game : games) {
            stringBuilder.append(game.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}

