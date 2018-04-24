package controllers;


import com.google.gson.Gson;
import dao.InMemoryGameDataDao;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.rxjava.core.eventbus.Message;
import io.vertx.rxjava.ext.web.RoutingContext;
import rx.Observable;
import rx.schedulers.Schedulers;
import services.GameDataService;
import services.GameDataServiceImpl;
import services.ReplyError;

import java.util.HashMap;
import java.util.Map;

public class GameController extends BaseVerticle {

    private static Map<Long, JsonObject> databases = new HashMap<>();
    private GameDataService gameDataService;
    private Gson gson = new Gson();

//    public GameController(GameDataService gameDataService) {
//        this.gameDataService = gameDataService;
//    }

    @Override
    public void startVerticle(Future<Void> startFuture) {
        gameDataService = new GameDataServiceImpl(new InMemoryGameDataDao());
        setupHandlers();

        DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setWorker(true)
                .setInstances(20)
                .setWorkerPoolSize(20);

        try {
            vertx.deployVerticle(WorkerVerticle.class.getCanonicalName(), deploymentOptions, result -> {
                if (result.succeeded()) {
                    startFuture.complete();
                } else {
                    startFuture.fail(result.cause());
                }
            });
        } catch (Throwable throwable) {
            startFuture.fail(throwable);
        }
    }


//    @Override
//    public void start() {
//        Vertx vertx = Vertx.vertx();
//        Router router = Router.router(vertx);
//        router.route().method(HttpMethod.POST).handler(BodyHandler.create());
//        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
//        setupRouting(router);
//
//    }

    private void setupHandlers() {
        addHandler("post.create", this::postGame);
        addHandler("post.join", this::postJoin);
        addHandler("get.data", this::getData);
        addHandler("post.move", this::postMove);
    }


    private void postGame(Message<JsonObject> message) {
        JsonObject body = message.body();
        int totalCheckpoints = body.getInteger("total_checkpoints");
        gameDataService.create(totalCheckpoints, message).subscribe(gameId ->
                message.reply(new JsonObject().put("game_id", gameId)), error ->
                new ReplyError(message).replyError(500, "Error creating game")
        );
    }

    private void postJoin(Message<JsonObject> message) {
        JsonObject body = message.body();
        if ((body.getLong("game_id") == null)) {
            new ReplyError(message).replyError(400, "No game Id");
        } else if (body.getString("name") == null) {
            new ReplyError(message).replyError(400, "No player name");
        } else {
            long gameId = body.getLong("game_id");
            String name = body.getString("name");
            gameDataService.addPlayer(gameId, name).subscribe(player -> {
                vertx.eventBus().rxSend("get.worker", message.body()).toObservable().flatMap(wr -> {
                    Object wRBody = wr.body();
                    return Observable.just(wr);
                }).subscribe();
                message.reply("player_id: " + player.getId());
            }, error ->
                    new ReplyError(message).replyError(500, "Error adding player"));
        }
    }

    private void getData(Message<JsonObject> message) {
        JsonObject body = message.body();
        long gameId = body.getLong("game_id");
        gameDataService.getGameData(gameId).retryWhen(errors ->
                errors.zipWith(Observable.range(1, 3), (error, retryCount) -> {
                    if (retryCount < 3) {
                        gameDataService.getGameData(gameId);
                    }
                    return Observable.error(error);
                }).flatMap(e -> e)).subscribe(game ->
                message.reply(gson.toJson(game)), error ->
                new ReplyError(message).replyError(500, "Error getting data"));
    }

    private void postMove(Message<JsonObject> message) {
        JsonObject body = message.body();
        if (body.getLong("game_id") == null) {
            new ReplyError(message).replyError(400, "No game id");
        } else if (body.getInteger("player_id") == null) {
            new ReplyError(message).replyError(400, "No player id");
        } else if (body.getInteger("card_id") == null) {
            new ReplyError(message).replyError(400, "No card id");
        } else {
            long gameId = body.getLong("game_id");
            int playerId = body.getInteger("player_id");
            int cardId = body.getInteger("card_id");
            gameDataService.doMove(gameId, playerId, cardId).subscribe(robot ->
                    message.reply(new JsonObject().put("success", true)), error ->
                    new ReplyError(message).replyError(500, "Error getting data"));
        }
    }
}

//    public void setupRouting(Router router) {
//        router.route(HttpMethod.POST, "/create").handler(routingContext -> {
//            JsonObject body = routingContext.getBodyAsJson();
//            int totalCheckpoints = body.getInteger("total_checkpoints");
//            gameDataService.create(totalCheckpoints).subscribe(gameId ->
//                    routingContext.response().end("game_id: " + gameId));
//        });

//        router.route(HttpMethod.POST, "/join").handler(routingContext -> {
//            JsonObject body = routingContext.getBodyAsJson();
//            long gameId = body.getLong("game_id");
//            String name = body.getString("name");
//            gameDataService.addPlayer(gameId, name).subscribe(player ->
//                    routingContext.response().end("player_id: " + player.getId()));
//        });

//        router.route(HttpMethod.GET, "/data").handler(routingContext -> {
//            MultiMap queryParams = routingContext.queryParams();
//            long gameId = Long.valueOf(queryParams.get("game_id"));
//            gameDataService.getGameData(gameId).subscribe(game ->
//                    routingContext.response().end(gson.toJson(game))); //TODO - convert data to json
//        });

//        router.route(HttpMethod.POST, "/move").handler(routingContext -> {
//            JsonObject body = routingContext.getBodyAsJson();
//            long gameId = body.getLong("game_id");
//            int playerId = body.getInteger("player_id");
//            int cardId = body.getInteger("card_id");
//            gameDataService.doMove(gameId, playerId, cardId).subscribe(robot ->
//                    routingContext.response().end());
//        });
//    }

//    private static JsonObject buildCheckpointsBody(Set<BoardLocation> checkpoints) {
//        String checkpointId = "checkpoint_";
//        int checkpointCount = 1;
//
//        JsonObject checkpointsInJson = mapJ();
//        for (BoardLocation checkpoint : checkpoints) {
//            checkpointsInJson.put(checkpointId + checkpointCount, mapJ(
//                    "x-coordinate", checkpoint.getX_coordinate(),
//                    "y-coordinate", checkpoint.getY_coordinate()));
//            checkpointCount++;
//        }
//
//        return checkpointsInJson;
//    }

//    private static JsonArray buildPlayersBody(List<Player> players) {
//
//        JsonArray playerList = listJ();
//
//        for (Player player : players) {
//            JsonObject playersInJson = mapJ();
//            int id = player.getId();
//            String name = player.getName();
//
//            Deck deck = player.getDeck();
//            deck.shuffle();
//            JsonArray cards = new JsonArray();
//            deck.getCards().forEach(card -> {
//                JsonObject temp = mapJ(
//                        "type", card.getCardType().name(),
//                        "location", card.getCardLocation().name());
//                cards.add(temp);
//            });
//
//            Robot robot = player.getRobot();
//            BoardLocation robotLocation = robot.getBoardLocation();
//            RobotDirection robotDirection = robot.getRobotDirection();
//
//            playersInJson.put("id", id);
//            playersInJson.put("name", name);
//            playersInJson.put("cards", cards);
//            playersInJson.put("robot", mapJ(
//                    "direction", robotDirection.name(),
//                    "location", mapJ(
//                            "x_coordinate", robotLocation.getX_coordinate(),
//                            "y_coordinate", robotLocation.getY_coordinate())));
//
//            playerList.add(playersInJson);
//        }
//
//        return playerList;
//    }

//    private static void postGameData(RoutingContext routingContext) {
//        JsonObject body = routingContext.getBodyAsJson();
//        long gameId = body.getLong("game_id");
//        GameData gameData = games.get(gameId);
//
//        BoardLocation satellite = gameData.getSatelliteLocation();
//        BoardLocation reboot = gameData.getRebootLocation();
//        Set<BoardLocation> checkpoints = gameData.getCheckpointLocations();
//        List<Player> players = gameData.getPlayers();
//
//        JsonObject database = mapJ();
//        database.put("satellite", mapJ("x_coordinate", satellite.getX_coordinate(), "y_coordinate", satellite.getY_coordinate()));
//        database.put("reboot", mapJ("x_coordinate", reboot.getX_coordinate(), "y_coordinate", reboot.getY_coordinate()));
//        JsonObject checkpointsInJson = buildCheckpointsBody(checkpoints);
//
//        JsonArray checkpointList = listJ();
//        checkpointsInJson.forEach(checkpoint -> {
//            JsonObject value = (JsonObject) checkpoint.getValue();
//            checkpointList.add(mapJ(
//                    checkpoint.getKey(), mapJ(
//                            "x_coordinate", value.getInteger("x-coordinate"),
//                            "y_coordinate", value.getInteger("y-coordinate")
//                    )));
//        });
//
//        database.put("checkpoints", checkpointList);
//        database.put("players", buildPlayersBody(players));
//
//        databases.put(gameId, database);
//    }


//    private static JsonArray getHand(RoutingContext routingContext) {
//        MultiMap queryParams = routingContext.queryParams();
//        long gameId = Long.valueOf(queryParams.get("game_id"));
//        int playerId = Integer.parseInt(queryParams.get("player_id"));
//        JsonObject game = databases.get(gameId);
//
//        JsonObject playerInfo = game.getJsonArray("players").getJsonObject(playerId);
//        JsonArray cards = playerInfo.getJsonArray("cards");
//
//        int cardPointer = 0;
//
//        while (!cards.getJsonObject(cardPointer).getString("location").equals("DECK")) {
//            cardPointer++;
//        }
//
//        JsonArray hand = new JsonArray();
//        if (cardPointer < 75) {
//            for (int i = cardPointer; i < cardPointer + 9; i++) {
//                String type = cards.getJsonObject(i).getString("type");
//                String location = cards.getJsonObject(i).put("location", "HAND").getString("location");
//                hand.add(mapJ("type", type, "location", location));
//            }
//        } else {
//            cards.forEach(card -> {
//                ((JsonObject) card).put("location", "DECK");
//            });
////            Collections.shuffle(cards);
//        }
//
//        return hand;
//    }

//    private static void postHand(RoutingContext routingContext) {
//
//    }

//    private static void moveRobot() {
//
//    }


//    public static JsonObject mapJ(Object... values) {
//        JsonObject json = new JsonObject();
//        if (values.length > 0 && values.length % 2 == 0) {
//            for (int idx = 0; idx < values.length; idx += 2) {
//                String name = (String) values[idx];
//                Object value = values[idx + 1];
//                json.put(name, value);
//            }
//        }
//        return json;
//    }

//    public static JsonArray listJ(Object... values) {
//        JsonArray list = new JsonArray();
//        if (values != null) {
//            for (Object object : values)
//                list.getList().add(object);
//        }
//        return list;
//    }


//        robots = new ArrayList<>();
//
//        JsonArray robotInit = body.getJsonArray("robots");
//
//        robotInit.forEach(direction -> {
//
//            int x = ThreadLocalRandom.current().nextInt(0, 2);
//            int y = ThreadLocalRandom.current().nextInt(0, 10);
//
//            BoardLocation boardLocation = new BoardLocation(x, y);
//
//            if (boardLocation.getY_coordinate() != satelliteBoardLocation.getY_coordinate()) {
//                RobotDirection robotDirection = null;
//                if (direction.equals("UP")) {
//                    robotDirection = RobotDirection.UP;
//                } else if (direction.equals("DOWN")) {
//                    robotDirection = RobotDirection.DOWN;
//                } else if (direction.equals("LEFT")) {
//                    robotDirection = RobotDirection.LEFT;
//                } else if (direction.equals("RIGHT")) {
//                    robotDirection = RobotDirection.RIGHT;
//                }
//                if (robotDirection != null) {
//                    robots.add(new Robot(robotDirection, x, y));
//                }
//            }
//
//        });

//        String[][] board = new String[10][10];
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                board[i][j] = "-";
//            }
//        }

//        board[satelliteBoardLocation.getX_coordinate()][satelliteBoardLocation.getY_coordinate()] = "S";
//
//        int count = 1;
//        for (int i = 0; i < checkpointBoardLocations.size(); i++) {
//            int x = checkpointBoardLocations.get(i).getX_coordinate();
//            int y = checkpointBoardLocations.get(i).getY_coordinate();
//            board[x][y] = String.valueOf(count);
//            count++;
//        }
//
//        robots.forEach(robot -> {
//            int x = robot.getX_coordinate();
//            int y = robot.getY_coordinate();
//            board[x][y] = "R";
//        });
//
//        StringBuilder checkRobot = new StringBuilder();
//        StringBuilder checkFlag = new StringBuilder();
//
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                if (board[i][j].equals("R")) {
//                    checkRobot.append("R");
//                }
//                if (board[i][j].equals("1")) {
//                    checkFlag.append("1");
//                }
//                if (board[i][j].equals("2")) {
//                    checkFlag.append("2");
//                }
//                if (board[i][j].equals("3")) {
//                    checkFlag.append("3");
//                }
//            }
//        }

//            robots.forEach(robot -> {
//                String x = String.valueOf(robot.getX_coordinate());
//                String y = String.valueOf(robot.getY_coordinate());
//                System.out.println("Robot Locations: " + x + " " +  y);
//            });

//            checkpointBoardLocations.forEach(checkpoint -> {
//                String x = String.valueOf(checkpoint.getX_coordinate());
//                String y = String.valueOf(checkpoint.getY_coordinate());
//
//                System.out.println("Checkpoint Locations: " + x + " " +  y);
//            });

//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                System.out.print(board[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println();
