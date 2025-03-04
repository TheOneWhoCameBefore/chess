package dto;

import model.GameData;

import java.util.Collection;

public record ListGamesResponse(Collection<ListGameData> games) {}

