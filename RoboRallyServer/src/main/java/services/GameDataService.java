package services;

import models.GameData;

public interface GameDataService {

    long create(int totalCheckpoints);

    void addPlayer(long gameId, String name);

    GameData getGameData(long gameId);

    void doMove(long gameId, long playerId, long cardId);
}
