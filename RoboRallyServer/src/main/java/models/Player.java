package models;


import rx.Observable;

import java.util.Objects;

public class Player {


    private long id;
    private String name;
    private Robot robot;
    private Deck deck;

    public Player(String name, Robot robot) {
        this.name = name;
        this.robot = robot;
        this.deck = new Deck();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public Observable<Robot> getRobot() {
        return Observable.just(robot);
    }

    public Deck getDeck() {
        return deck;
    }

    public Observable<Card> getCardByCardId(int cardId) {
        return Observable.just(deck.getCardByCardId(cardId - 1));
    }
}
