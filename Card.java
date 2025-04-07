package Playing_Cards_Game;

public class Card {
    private String suit;
    private String rank; // Ace to King

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getRank() {
        return rank;
    }

    public String toString() {
        return rank + " of " + suit;
    }
}
