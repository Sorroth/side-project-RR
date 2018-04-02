package models;

import java.util.Objects;

public class BoardLocation {

    private int x_coordinate;
    private int y_coordinate;

    public BoardLocation(int x_coordinate, int y_coordinate) {
        this.x_coordinate = x_coordinate;
        this.y_coordinate = y_coordinate;
    }

    public int getX_coordinate() {
        return x_coordinate;
    }

    public void setX_coordinate(int x_coordinate) {
        this.x_coordinate = x_coordinate;
    }

    public int getY_coordinate() {
        return y_coordinate;
    }

    public void setY_coordinate(int y_coordinate) {
        this.y_coordinate = y_coordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardLocation boardLocation = (BoardLocation) o;
        return x_coordinate == boardLocation.x_coordinate &&
                y_coordinate == boardLocation.y_coordinate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x_coordinate, y_coordinate);
    }
}
