package dto;

import java.util.Collection;

public record ListGamesResponse(Collection<ListGameData> games) {}

