package controllers;

import com.google.gson.Gson;
import dao.InMemoryGameDataDao;
import io.vertx.core.Future;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import rx.Observable;
import services.GameDataService;
import services.GameDataServiceImpl;

public class Webgateway extends BaseVerticle {

    private GameDataService gameDataService;
    private Gson gson = new Gson();

    @Override
    public void startVerticle(Future<Void> startFuture) {
        gameDataService = new GameDataServiceImpl(new InMemoryGameDataDao());
        Router router = Router.router(vertx);

        BodyHandler bodyHandler = BodyHandler.create();
        router.route().method(HttpMethod.PATCH).handler(bodyHandler);
        router.route().method(HttpMethod.POST).handler(bodyHandler);
        router.route().method(HttpMethod.PUT).handler(bodyHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        setupRouting(router);
        startFuture.complete();
    }

    private void setupRouting(Router router) {
        router.routeWithRegex(".*").handler(routingContext -> {
            HttpServerRequest request = routingContext.request();
            String httpMethod = request.method().toString().toLowerCase();
            String path = request.path().substring(1);
            String eventBusAddress = httpMethod + "." + path;
            boolean hasError = false;
            JsonObject messageBody = new JsonObject();
            if (httpMethod.equals("post")) {
                if (routingContext.getBodyAsString().isEmpty()) {
                    hasError = true;
                    routingContext.response().setStatusCode(400).end("Invalid request: POST body missing");
                } else {
                    messageBody = routingContext.getBodyAsJson();
                }
            } else if (httpMethod.equals("get")) {
                MultiMap queryParams = routingContext.queryParams();
                if (!queryParams.isEmpty()) {
                    long gameId = Long.parseLong(queryParams.get("game_id"));
                    messageBody.put("game_id", gameId);
                }
            }
            if (!hasError) {
                sendRequest(eventBusAddress, messageBody).subscribe(reply ->
                        routingContext.response().end(String.valueOf(reply.body())), error -> {
                    routingContext.response().setStatusCode(((ReplyException) error).failureCode());
                    routingContext.response().end(error.getMessage());
                });
            }
        });
    }
}
