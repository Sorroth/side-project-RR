package models;

public class Card {

    private final CardType cardType;
    private CardLocation cardLocation;

    public Card(CardType cardType, CardLocation cardLocation) {
        this.cardType = cardType;
        this.cardLocation = cardLocation;
    }

    public CardType getCardType() {
        return cardType;
    }

    public CardLocation getCardLocation() {
        return cardLocation;
    }

    public void setCardLocation(CardLocation cardLocation) {
        this.cardLocation = cardLocation;
    }

//    @Override
//    public String toString() {
//        return "Card{" +
//                "cardType=" + cardType +
//                ", cardLocation=" + cardLocation +
//                '}';
//    }
}
