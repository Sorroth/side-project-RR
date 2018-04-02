package models;

import java.util.*;

public class Deck {

    private Map<Long, Card> cards = new LinkedHashMap<>();


    public Deck() {
        for (CardType type : CardType.values()) {
            for (long i = 0; i < type.getMaxNumInDeck(); i++) {
                cards.put(i, new Card(type, CardLocation.DECK));
            }
        }
    }



    public void shuffle() {
//        Collections.shuffle(cards); //TODO - reimplement
    }

    public Collection<Card> getCards() {
        return cards.values();
    }

    public Card getCardByCardId(long cardId) {
        return cards.get(cardId);
    }
}
