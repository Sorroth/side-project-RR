package services;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import models.GameData;
import models.Player;
import models.Robot;
import rx.Observable;

public interface GameDataService {

    Observable<Long> create(int totalCheckpoints, Message<JsonObject> message);

    Observable<Player> addPlayer(long gameId, String name);

    Observable<GameData> getGameData(long gameId);

    Observable<Robot> doMove(long gameId, int playerId, int cardId);
}
