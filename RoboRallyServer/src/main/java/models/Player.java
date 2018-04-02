package models;

import java.util.Objects;

public class Player {

    private String name;
    private Robot robot;
    private Deck deck;

    public Player(String name, Robot robot, long playerId) {
        this.name = name;
        this.robot = robot;
        this.deck = new Deck();
    }

    public String getName() {
        return name;
    }

    public Robot getRobot() {
        return robot;
    }

    public Deck getDeck() {
        return deck;
    }

    public Card getCardByCardId(long cardId) {
        return deck.getCardByCardId(cardId);
    }
}
