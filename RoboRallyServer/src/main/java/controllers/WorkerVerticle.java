package controllers;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import services.GameDataService;

public class WorkerVerticle extends BaseVerticle{

    GameDataService gameDataService;

    @Override
    public void startVerticle(Future<Void> startFuture) {
        addHandler("get.worker", this::getWorker);
        startFuture.complete();
    }

    private void getWorker(Message<JsonObject> message) {
        try {
            System.out.println("Start sleep");
            Thread.sleep(5000);
            System.out.println("End sleep");
            message.reply(message.body());
        } catch (InterruptedException e) {
            message.fail(500, "error sleep");
        }

    }
}
