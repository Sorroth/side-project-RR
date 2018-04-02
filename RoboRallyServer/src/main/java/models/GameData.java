package models;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameData implements Cloneable {

    private long id;
    private long playerCounter = 0;
    private BoardLocation satellite;
    private BoardLocation reboot;
    private Set<BoardLocation> checkpoints;
    private Map<Long, Player> players = new HashMap<>();
    private Set<BoardLocation> robots = new HashSet<>();

    public GameData(BoardLocation satellite, BoardLocation reboot, Set<BoardLocation> checkpoints) {
        this.satellite = satellite;
        this.reboot = reboot;
        this.checkpoints = checkpoints;
    }

    public void addPlayer(String name, Robot robot) {
        players.put(playerCounter++, new Player(name, robot, playerCounter++));
    }

    public Set<BoardLocation> getLocationsOfAllPlayers() {
        Set<BoardLocation> currentRobotLocations = new HashSet<>();
        for (Player player : players.values()) {
            currentRobotLocations.add(player.getRobot().getBoardLocation());
        }
        return currentRobotLocations;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public BoardLocation getSatelliteLocation() {
        return satellite;
    }

    public BoardLocation getRebootLocation() {
        return reboot;
    }

    public Set<BoardLocation> getCheckpointLocations() {
        return checkpoints;
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public Player getPlayerByPlayerId(long playerId) {
        return players.get(playerId);
    }
}
