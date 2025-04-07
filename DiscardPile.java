package Playing_Cards_Game;

import java.util.*;

public class DiscardPile {
    private Stack<Card> pile;

    public DiscardPile() {
        pile = new Stack<>();
    }

    public synchronized void addCard(Card card) {
        pile.push(card);
    }

    public synchronized Card getTopCard() {
        return pile.isEmpty() ? null : pile.peek();
    }
}
