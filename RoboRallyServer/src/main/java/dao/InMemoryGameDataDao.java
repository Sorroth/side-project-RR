package dao;

import models.Card;
import models.GameData;
import models.Player;

import java.util.HashMap;
import java.util.Map;

public class InMemoryGameDataDao implements GameDataDao{

    private static Long idCount = 0L;
    private Map<Long, GameData> games = new HashMap<>();

    @Override
    public long addGame(GameData gameData) {
        gameData.setId(getNewId());
        games.put(gameData.getId(), gameData);
        return gameData.getId();
    }

    @Override
    public GameData getGameById(long gameId) {
        return games.get(gameId);
    }

    public void updateGame(long gameId, GameData gameData) {
        games.put(gameId, gameData);
    }

//    @Override
//    public Player getPlayerByPlayerIdOfGame(long gameId, long playerId) {
//        return games.get(gameId).getPlayerByPlayerId(playerId);
//    }
//
//    @Override
//    public Card getCardByCardIdOfPlayerAndGame(long gameId, long playerId, long cardId) {
//        return games.get(gameId).getPlayerByPlayerId(playerId).getCardByCardId(cardId);
//    }
//
    //    public long addPlayerToGame(GameData data, Player player) {
//        player.setId(getNewId());
//        player.getGame().addPlayer(player);
//        data.addPlayer(player);
//        return player.getId();
//    }

//    }
//
//    @Override
//    public Player getPlayerByIndexOfGame(long gameId, int playerIndex) {
//        return games.get(gameId).getPlayers().get(playerIndex);
//    }
//
//    @Override
//    public Player geCardBytPlayerByIndexOfGame(long gameId, int playerIndex) {
//        return games.get(gameId).getPlayers().get(playerIndex);
//
//    @Override
//    public long savePlayerToGame(GameData gameData, Player player) {
//        playerCount++;
//        gameData.
//        players.put(playerCount, player);
//        return playerCount;
//    }
//
//    @Override
//    public long getCardById(long cardId) {
//        return 0;
//    }

    private long getNewId() {
        idCount++;
        return idCount;
    }
}
