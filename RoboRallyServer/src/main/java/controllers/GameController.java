package controllers;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController extends AbstractVerticle {

    static InvalidLocation invalidLocation;
    static SatelliteLocation satelliteLocation;
    static RebootLocation rebootLocation;
    static ArrayList<CheckpointLocation> checkpointLocations;


    static ArrayList<Robot> robots;
    static char[][] gameBoard;
    static LinkedHashMap<Integer, String> deck;
    static HashMap<Integer, String> drawPile;
    static HashMap<Integer, String> hand;
//    static LinkedHashMap<Integer, String> drawPile;

    public static void main(String[] args) {

        new GameController().start();

//        initGameBoard(randomNum);
//        getGameData();
//        chooseCardOrder();

//        int charCount = 0;
//        for (int i = 0; i < 100; i++) {
//            System.out.println((charCount) + " " + ((char)charCount++));
//        }
    }

    @Override
    public void start() {
//        initStaticBoardPieces(3, robots);

        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);
        router.route().method(HttpMethod.POST).handler(BodyHandler.create());
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        setupRouting(router);

    }

    public void setupRouting(Router router) {

        router.route(HttpMethod.POST, "/create").handler(routingContext -> {
//            initGameBoard(routingContext.getBodyAsJson().getInteger("num_of_players"));

            initStaticBoardPieces(routingContext);
            routingContext.response().end("create board, with number of players in body, and a deck of cards for each player");
        });

        router.route(HttpMethod.GET, "/data").handler(routingContext -> {
//            getGameData();
//            routingContext.response().end("get satellite location, respawn location, flag locations, robot locations, 9 drawn cards");
            routingContext.response().end(getGameData().toString());
        });

        router.route(HttpMethod.POST, "/move").handler(routingContext -> {
            chooseCardOrder();
            routingContext.response().end("user submits 5 of the 9 cards and the rest are discarded. Robot location updated");
        });
    }

    private static void initStaticBoardPieces(RoutingContext routingContext) {
        JsonObject body = routingContext.getBodyAsJson();
        int numberOfCheckpoints = body.getInteger("num_of_checkpoints");

        satelliteLocation = new SatelliteLocation(0, 4);
        rebootLocation = new RebootLocation(4, 4);
        checkpointLocations = new ArrayList<>();

        for (int i = 0; i < numberOfCheckpoints; i++) {

            int x = ThreadLocalRandom.current().nextInt(5, 10);
            int y = ThreadLocalRandom.current().nextInt(0, 10);

            checkpointLocations.add(new CheckpointLocation(x, y));
        }

        robots = new ArrayList<>();

        JsonArray robotInit = body.getJsonArray("robot_init");

        robotInit.forEach(direction -> {

            int x = ThreadLocalRandom.current().nextInt(0, 2);
            int y = ThreadLocalRandom.current().nextInt(0, 10);

            Location location = new Location(x, y);

            if (location.getY_coordinate() != satelliteLocation.getY_coordinate()) {
                RobotDirection robotDirection = null;
                if (direction.equals("UP")) {
                    robotDirection = RobotDirection.UP;
                } else if (direction.equals("DOWN")) {
                    robotDirection = RobotDirection.DOWN;
                } else if (direction.equals("LEFT")) {
                    robotDirection = RobotDirection.LEFT;
                } else if (direction.equals("RIGHT")) {
                    robotDirection = RobotDirection.RIGHT;
                }
                if (robotDirection != null) {
                    robots.add(new Robot(robotDirection, x, y));
                }
            }

        });

        String[][] board = new String[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = "-";
            }
        }

        board[satelliteLocation.getX_coordinate()][satelliteLocation.getY_coordinate()] = "S";

        int count = 1;
        for (int i = 0; i < checkpointLocations.size(); i++) {
            int x = checkpointLocations.get(i).getX_coordinate();
            int y = checkpointLocations.get(i).getY_coordinate();
            board[x][y] = String.valueOf(count);
            count++;
        }

        robots.forEach(robot -> {
            int x = robot.getX_coordinate();
            int y = robot.getY_coordinate();
            board[x][y] = "R";
        });

        StringBuilder checkRobot = new StringBuilder();
        StringBuilder checkFlag = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (board[i][j].equals("R")) {
                    checkRobot.append("R");
                }
                if (board[i][j].equals("1")) {
                    checkFlag.append("1");
                }
                if (board[i][j].equals("2")) {
                    checkFlag.append("2");
                }
                if (board[i][j].equals("3")) {
                    checkFlag.append("3");
                }
            }
        }

            robots.forEach(robot -> {
                String x = String.valueOf(robot.getX_coordinate());
                String y = String.valueOf(robot.getY_coordinate());
                System.out.println("Robot Locations: " + x + " " +  y);
            });

            checkpointLocations.forEach(checkpoint -> {
                String x = String.valueOf(checkpoint.getX_coordinate());
                String y = String.valueOf(checkpoint.getY_coordinate());

                System.out.println("Checkpoint Locations: " + x + " " +  y);
            });

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
        System.out.println();

    }

    private static void initGameBoard(int numberOfPlayers) {
        //region init board
        gameBoard = new char[10][10];

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard.length; j++) {

                if (gameBoard[i][j] == '\u0000') {
                    gameBoard[i][j] = '-';
                }
            }
        }

        gameBoard[0][5] = 's';
        gameBoard[4][4] = 'r';
        gameBoard[9][5] = 'f';

        int count = 49;
        for (int i = count - numberOfPlayers; i < count; i++) {
            int j = ThreadLocalRandom.current().nextInt(0, 2);
            int k = ThreadLocalRandom.current().nextInt(0, 10);
            if (gameBoard[j][k] == '-') {
                if (count <= numberOfPlayers + 49) {
                    gameBoard[j][k] = (char) count++;
                }
            }
        }

        System.out.println(numberOfPlayers + 1);

        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard.length; j++) {
                System.out.print(gameBoard[i][j] + " ");
            }
            System.out.println();
        }
        //endregion

        //region init deck
        deck = new LinkedHashMap<>();

        for (int i = 10; i < 70; i += 10) {
            deck.put(i, "U Turn");
        }

        for (int i = 70; i < 430; i += 10) {
            if ((i / 10) % 2 == 0) {
                deck.put(i, "Rotate Right");
            } else {
                deck.put(i, "Rotate Left");
            }
        }

        for (int i = 430; i < 490; i += 10) {
            deck.put(i, "Back Up");
        }

        for (int i = 490; i < 670; i += 10) {
            deck.put(i, "Move 1");
        }

        for (int i = 670; i < 790; i += 10) {
            deck.put(i, "Move 2");
        }

        for (int i = 790; i < 850; i += 10) {
            deck.put(i, "Move 3");
        }

        drawPile = new HashMap<>();
//        drawPile = new LinkedHashMap<>();
        deck.forEach((id, move) -> drawPile.put(id, move));
        //endregion
    }

    private static JsonObject getGameData() {
//        boolean isSecondPass = false;
        JsonObject jsonGameBoard = mapJ("game_board", listJ(gameBoard));

        hand = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            int card = 11;
            while (card % 10 != 0) {
                card = ThreadLocalRandom.current().nextInt(10, 850);

                if (drawPile.isEmpty()) {
                    deck.forEach((id, move) -> drawPile.put(id, move));
                    hand.forEach((id, move) -> drawPile.remove(id));
//                    isSecondPass = true;
                }

                if (drawPile.get(card) == null) {
                    card = 11;
                }
            }

            hand.put(card, drawPile.get(card));
            drawPile.remove(card);

            System.out.println((i + 1) + ". " + card + ", " + hand.get(card));
        }
//        if (!isSecondPass) {
//            getGameData();
//        }
        System.out.println();

        JsonObject jsonHand = mapJ("hand", listJ(hand));
//        JsonObject jsonGameData = mapJ("game_data", listJ(jsonGameBoard, jsonHand));
        return mapJ("game_data", listJ(jsonGameBoard, jsonHand));
//        System.out.println();
    }

    private static void chooseCardOrder() {
//        user selects 5 of the 9 cards
        ArrayList<String> moveSet = new ArrayList<>();
        hand.forEach((id, move) -> moveSet.add(move));
        String[] submittedMoves = new String[5];
        System.out.print("Your robots orders are to: ");
        for (int i = 0; i < 5; i++) {
            submittedMoves[i] = moveSet.get(i);
            String spacer = ", ";
            if (i == submittedMoves.length - 1) {
                spacer = "";
            }
            System.out.print(submittedMoves[i] + spacer);

        }
        System.out.println();
        moveRobot();
    }


    private static void moveRobot() {

    }


    public static JsonObject mapJ(Object... values) {
        JsonObject json = new JsonObject();
        if (values.length > 0 && values.length % 2 == 0) {
            for (int idx = 0; idx < values.length; idx += 2) {
                String name = (String) values[idx];
                Object value = values[idx + 1];
                json.put(name, value);
            }
        }
        return json;
    }

    public static JsonArray listJ(Object... values) {
        JsonArray list = new JsonArray();
        if (values != null) {
            for (Object object : values)
                list.getList().add(object);
        }
        return list;
    }
}
