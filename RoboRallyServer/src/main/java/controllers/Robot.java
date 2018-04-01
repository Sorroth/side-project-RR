package controllers;

public class Robot extends Location{

    RobotDirection robotDirection;

    public Robot(RobotDirection robotDirection,int x_coordinate, int y_coordinate) {
        super(x_coordinate, y_coordinate);
        this.robotDirection = robotDirection;
    }
}
