package models;

public enum CardType {
    U_TURN(6, false, 2),
    ROTATE_RIGHT(18, false, 1),
    ROTATE_LEFT(18, false, 3),
    BACK_UP(6, true, -1),
    MOVE_1(18, true, 1),
    MOVE_2(12, true, 2),
    MOVE_3(6, true, 3);

    private int maxNumInDeck;
    private boolean isMovement;
    private int unit;

    CardType(int maxInDeck, boolean isMovement, int moveOrRotateUnit) {
        this.maxNumInDeck = maxInDeck;
        this.isMovement = isMovement;
        this.unit = moveOrRotateUnit;
    }

    public int getMaxNumInDeck() {
        return maxNumInDeck;
    }

    public boolean isMovement() {
        return isMovement;
    }

    public int getUnit() {
        return unit;
    }
}
