package run;

import controllers.GameController;
import controllers.Scheduler;
import controllers.Webgateway;
import dao.InMemoryGameDataDao;
import io.vertx.core.Vertx;
import services.GameDataServiceImpl;

public class RunGame {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(GameController.class.getCanonicalName());
        vertx.deployVerticle(Webgateway.class.getCanonicalName());
        vertx.deployVerticle(Scheduler.class.getCanonicalName());
//        new Webgateway().start();
//        new GameController(new GameDataServiceImpl(new InMemoryGameDataDao())).start();
    }
}
