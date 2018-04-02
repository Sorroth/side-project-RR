package dao;

import models.GameData;
import models.Player;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public class DatabaseGameDataDao implements GameDataDao{

    //conneciton info

    @Override
    public long addGame(GameData gameData) {
        throw new NotImplementedException();
    }

    @Override
    public GameData getGameById(long gameId) {
        throw new NotImplementedException();
    }

    public void updateGame(long gameId, GameData gameData) {
        throw new NotImplementedException();
    }
}
