package dao;

import models.Card;
import models.GameData;
import models.Player;
import rx.Observable;

public interface GameDataDao {

    /**
     * Adds new game
     * @param gameData
     * @return new game id
     */
    long addGame(GameData gameData);

    Observable<GameData> getGameById(long id);

    void updateGame(long gameId, GameData gameData);

//    Player getPlayerByPlayerIdOfGame(long gameId, long playerId);
//
//    Card getCardByCardIdOfPlayerAndGame(long gameId, long playerId, long cardId);

//    long savePlayerToGame(long gameId, Player player);

//    long getCardById(long cardId);
//    long addPlayerToGame(GameData data, Player player);
//    List<Player> getPlayersInGame(long gameId);

}
