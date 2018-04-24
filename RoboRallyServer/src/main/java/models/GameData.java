package models;


import io.vertx.core.json.JsonArray;
import rx.Observable;

import java.util.*;

public class GameData implements Cloneable {

    private int id;
    private long playerCounter = 0;
    private BoardLocation satellite;
    private BoardLocation reboot;
    private Set<BoardLocation> checkpoints;
    private List<Player> players = new ArrayList<>();
    private Set<BoardLocation> robots = new HashSet<>();

    public GameData(BoardLocation satellite, BoardLocation reboot, Set<BoardLocation> checkpoints) {
        this.satellite = satellite;
        this.reboot = reboot;
        this.checkpoints = checkpoints;
    }

    public void addPlayer(Player player) {
        player.setId(++playerCounter);
        players.add(player);
    }

    public Observable<Set<BoardLocation>> getLocationsOfAllPlayers() {
        if (players.isEmpty()) {
            return Observable.just(robots);
        }
        return Observable.from(players).flatMap(player ->
                player.getRobot().map(robot -> {
                    robots.add(robot.getBoardLocation());
                    return robots;
                }));
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
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
        return players;
    }

    public Observable<Player> getPlayerByPlayerId(int playerId) {
        return Observable.just(players.get(playerId - 1));
    }
}
