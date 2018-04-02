package run;

import controllers.GameController;
import dao.DatabaseGameDataDao;
import dao.InMemoryGameDataDao;
import services.GameDataServiceImpl;

public class RunGame {
    public static void main(String[] args) {
        new GameController(new GameDataServiceImpl(new DatabaseGameDataDao())).start();
    }
}
