package models;

import java.util.*;

public class Deck {

    private Map<Integer, Card> cards = new LinkedHashMap<>();


    public Deck() {
        int cardCount = 0;
        for (CardType type : CardType.values()) {
            for (int i = 0; i < type.getMaxNumInDeck(); i++) {
                cards.put(cardCount, new Card(type, CardLocation.DECK));
                cardCount++;
            }
        }
    }



    public void shuffle() {
//        Collections.shuffle(cards); //TODO - reimplement
    }

    public Collection<Card> getCards() {
        return cards.values();
    }

    public Card getCardByCardId(int cardId) {
        return cards.get(cardId);
    }
}
