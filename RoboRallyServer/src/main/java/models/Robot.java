package models;

public class Robot {

    RobotDirection robotDirection;
    BoardLocation boardLocation;

    public Robot(BoardLocation boardLocation, RobotDirection robotDirection) {
        this.boardLocation = boardLocation;
        this.robotDirection = robotDirection;
    }

    public RobotDirection getRobotDirection() {
        return robotDirection;
    }

    public void setRobotDirection(RobotDirection robotDirection) {
        this.robotDirection = robotDirection;
    }

    public BoardLocation getBoardLocation() {
        return boardLocation;
    }

    public void setBoardLocation(BoardLocation boardLocation) {
        this.boardLocation = boardLocation;
    }

}
