package services;

import dao.GameDataDao;
import models.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class GameDataServiceImpl implements GameDataService {

    private GameDataDao gameDataDao;

    public GameDataServiceImpl(GameDataDao gameDataDao) {
        this.gameDataDao = gameDataDao;
    }

    @Override
    public long create(int totalCheckpoints) {

        if (totalCheckpoints < 0) {
            totalCheckpoints = 1;
        }

        if (totalCheckpoints > 3) {
            totalCheckpoints = 3;
        }

        BoardLocation satalite = new BoardLocation(0, 4);
        BoardLocation reboot = new BoardLocation(4, 4);
        HashSet<BoardLocation> checkpoints = new HashSet<>();

        while (checkpoints.size() < totalCheckpoints) {
            BoardLocation randomCheckpointBoardLocation = getRandomCheckpointLocation();
            checkpoints.add(randomCheckpointBoardLocation);
        }

        return gameDataDao.addGame(new GameData(satalite, reboot, checkpoints));
    }

    private BoardLocation getRandomCheckpointLocation() {
        int x = ThreadLocalRandom.current().nextInt(5, 10);
        int y = ThreadLocalRandom.current().nextInt(0, 10);
        return new BoardLocation(x, y);
    }

    @Override
    public void addPlayer(long gameId, String name) {
        GameData gameData = getGameData(gameId);
        BoardLocation proposedLocation = getRandomRobotLocation();
        Set<BoardLocation> robotLocations = gameData.getLocationsOfAllPlayers();

        while (robotLocations.contains(proposedLocation)) {
            proposedLocation = getRandomRobotLocation();
        }
        gameData.addPlayer(name, new Robot(proposedLocation, RobotDirection.DOWN));
    }

    @Override
    public GameData getGameData(long gameId) {
        return gameDataDao.getGameById(gameId);
    }

    @Override
    public void doMove(long gameId, long playerId, long cardId) {
        GameData gameData = getGameData(gameId);
        Player player = gameData.getPlayerByPlayerId(playerId);

        Card card = player.getCardByCardId(cardId);
        if (!card.getCardLocation().equals(CardLocation.HAND)) {
            throw new IllegalArgumentException("Card is not in hand");
        }

        Robot robot = player.getRobot();
        CardType cardType = card.getCardType();
        if (card.getCardType().isMovement()) {
            moveRobot(robot, cardType.getUnit());
        } else {
            rotateRobot(robot, cardType.getUnit());
        }
    }


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
