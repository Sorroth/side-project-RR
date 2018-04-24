package services;

import dao.GameDataDao;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.eventbus.Message;
import models.*;
import rx.Observable;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class GameDataServiceImpl implements GameDataService {

    private GameDataDao gameDataDao;
    private ReplyError replyError;

    public GameDataServiceImpl(GameDataDao gameDataDao) {
        this.gameDataDao = gameDataDao;
    }

    @Override
    public Observable<Long> create(int totalCheckpoints, Message<JsonObject> message) {

        if (totalCheckpoints < 0 || totalCheckpoints > 3) {
            new ReplyError(message).replyError(400, "Invalid number of checkpoints");
        }

        BoardLocation satellite = new BoardLocation(0, 4);
        BoardLocation reboot = new BoardLocation(4, 4);
        HashSet<BoardLocation> checkpoints = new HashSet<>();

        while (checkpoints.size() < totalCheckpoints) {
            BoardLocation randomCheckpointBoardLocation = getRandomCheckpointLocation();
            checkpoints.add(randomCheckpointBoardLocation);
        }

        return Observable.just(gameDataDao.addGame(new GameData(satellite, reboot, checkpoints)));
    }

    private BoardLocation getRandomCheckpointLocation() {
        int x = ThreadLocalRandom.current().nextInt(5, 10);
        int y = ThreadLocalRandom.current().nextInt(0, 10);
        return new BoardLocation(x, y);
    }

    @Override
    public Observable<Player> addPlayer(long gameId, String name) {
        return getGameData(gameId).flatMap(game ->
                game.getLocationsOfAllPlayers().flatMap(playerSet -> {
                    BoardLocation proposedLocation = getRandomRobotLocation();
                    while (playerSet.contains(proposedLocation)) {
                        proposedLocation = getRandomRobotLocation();
                    }
                    BoardLocation finalProposedLocation = proposedLocation;
                    Player newPlayer = new Player(name, new Robot(finalProposedLocation, RobotDirection.DOWN));
                    game.addPlayer(newPlayer);
                    return Observable.just(newPlayer);
                }));
    }

//        Observable<GameData> gameData = getGameData(gameId);
//
//        Observable<Set<BoardLocation>> ObservableSetOfRobotLocations = gameData.flatMap(data ->
//                Observable.just(data.getLocationsOfAllPlayers()));
//
//        ObservableSetOfRobotLocations.flatMap(setOfRobotLocations -> {
//            BoardLocation proposedLocation = getRandomRobotLocation();
//            while (setOfRobotLocations.contains(proposedLocation)) {
//                proposedLocation = getRandomRobotLocation();
//            }
//            BoardLocation finalProposedLocation = proposedLocation;
//            return gameData.flatMap(data -> {
//                data.addPlayer(name, new Robot(finalProposedLocation, RobotDirection.DOWN));
//                return Observable.just(data);
//            });
//        });
//        return gameData;

    @Override
    public Observable<GameData> getGameData(long gameId) {
        return gameDataDao.getGameById(gameId);
    }

    @Override
    public Observable<Robot> doMove(long gameId, int playerId, int cardId) {
        return getGameData(gameId).flatMap(game ->
                game.getPlayerByPlayerId(playerId).flatMap(player ->
                        player.getCardByCardId(cardId).flatMap(card -> {
//                            if (card.getCardLocation() != CardLocation.HAND) {
//                                throw new IllegalArgumentException("Card is not in hand");
//                            }
                            return player.getRobot().flatMap(robot -> {
                                if (card.getCardType().isMovement()) {
                                    moveRobot(robot, card.getCardType().getUnit());
                                } else {
                                    rotateRobot(robot, card.getCardType().getUnit());
                                }
                                return Observable.just(robot);
                            });
                        })));

    }
//        Observable<GameData> gameData = getGameData(gameId);
//
//        Observable<Player> player = gameData.flatMap(playerData ->
//                Observable.just(playerData.getPlayerByPlayerId(playerId)));
//
//        Observable<Card> card = player.flatMap(cardData ->
//                Observable.just(cardData.getCardByCardId(cardId)));
//
//        card.flatMap(cardData -> {
//            if (cardData.getCardLocation() != CardLocation.HAND) {
//                throw new IllegalArgumentException("Card is not in hand");
//            }
//            return Observable.empty();
//        });
//
//        Observable<Robot> robot = player.flatMap(robotData ->
//                Observable.just(robotData.getRobot()));
//
//        Observable<CardType> cardType = card.flatMap(cardData -> {
//
//            if (cardData.getCardType().isMovement()) {
//                moveRobot(robot, cardData.getCardType().getUnit());
//            } else {
//                rotateRobot(robot, cardData.getCardType().getUnit());
//            }
//            return Observable.empty();
//        });


    private BoardLocation getRandomRobotLocation() {
        int x = ThreadLocalRandom.current().nextInt(0, 2);
        int y = ThreadLocalRandom.current().nextInt(0, 10);
        return new BoardLocation(x, y);
    }

    private void rotateRobot(Robot robot, int units) {
        robot.setRobotDirection(robot.getRobotDirection().rotate(units));
    }

    private void moveRobot(Robot robot, int distance) {
        RobotDirection robotDirection = robot.getRobotDirection();
        BoardLocation robotLocation = robot.getBoardLocation();
        int x = robotLocation.getX_coordinate();
        int y = robotLocation.getY_coordinate();

        if (robotDirection == RobotDirection.DOWN) {
            robot.setBoardLocation(new BoardLocation(x + distance, y));
        } else if (robotDirection == RobotDirection.LEFT) {
            robot.setBoardLocation(new BoardLocation(x, y - distance));
        } else if (robotDirection == RobotDirection.RIGHT) {
            robot.setBoardLocation(new BoardLocation(x, y + distance));
        } else if (robotDirection == RobotDirection.UP) {
            robot.setBoardLocation(new BoardLocation(x - distance, y));
        }
    }

//    public GameData getGameDataById(long gameId) {
//        gameDataDao.getGameById(gameId);
//    }
}
