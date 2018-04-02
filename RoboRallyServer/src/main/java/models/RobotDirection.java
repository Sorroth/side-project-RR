package models;

public enum RobotDirection {
    UP(0),
    RIGHT(1),
    DOWN(2),
    LEFT(3);

    private int direction;

    RobotDirection(int direction) {
        this.direction = direction;
    }

    public RobotDirection rotate(int unit) {
        return getByDirection(direction + unit);
    }

    private RobotDirection getByDirection(int direction) {
        direction = direction % 4;
        if (direction == 0) {
            return UP;
        } else if (direction == 1) {
            return RIGHT;
        } else if (direction == 2) {
            return DOWN;
        } else {
            return LEFT;
        }
    }

}
