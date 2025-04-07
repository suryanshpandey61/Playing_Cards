package Playing_Cards_Game;

import java.util.*;

public class Player extends Thread {
    List<Card> hand;
    String name;
    Deck deck;
    DiscardPile discardPile;
    boolean isPlayer1;
    int pairsCount;
    List<List<Card>> pairs;
    Map<String, List<Card>> rankMap;
    Card discardedCard;
    boolean isMyTurn;
    boolean isFirstTurn;

    public Player(String name, Deck deck, DiscardPile discardPile, boolean isPlayer1) {
        this.name = name;
        this.deck = deck;
        this.discardPile = discardPile;
        this.isPlayer1 = isPlayer1;
        this.hand = new ArrayList<>();
        this.pairsCount = 0;
        this.pairs = new ArrayList<>();
        this.rankMap = new HashMap<>();
        this.isMyTurn = false;
        this.isFirstTurn = true;
    }

    public synchronized void addCard(Card card) {
        hand.add(card);
        rankMap.computeIfAbsent(card.getRank(), k -> new ArrayList<>()).add(card);
    }

    public synchronized boolean hasThreePairs() {
        return pairsCount >= 3;
    }

    private synchronized boolean checkForPair(Card newCard) {
        List<Card> sameRankCards = rankMap.getOrDefault(newCard.getRank(), new ArrayList<>());
        if (sameRankCards.size() >= 1) {
            Card first = sameRankCards.get(0);
            if (first != newCard) {
                pairs.add(Arrays.asList(first, newCard));
                pairsCount++;
                hand.removeAll(Arrays.asList(first, newCard));
                rankMap.get(newCard.getRank()).removeAll(Arrays.asList(first, newCard));
                System.out.println(name + " formed a pair: " + first + ", " + newCard);
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        while (!Game.isGameOver()) {
            synchronized (this) {
                while (!isMyTurn) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            takeTurn();
            checkForWin();

            synchronized (Game.class) {
                Game.nextTurn();
                Game.class.notifyAll(); // Notify other players to check if it's their turn
            }
        }
    }

    private void takeTurn() {
        System.out.println("\n" + name + "'s Turn:");

        Card pickedCard = null;

        if (isFirstTurn) {
            pickedCard = deck.drawCard();
            System.out.println(name + " draws " + pickedCard + " from the deck.");
            addCard(pickedCard);
            System.out.println(name + " discards " + pickedCard + " immediately.");
            discardPile.addCard(pickedCard);
            isFirstTurn = false;
        } else {
            if (discardPile.getTopCard() != null && new Random().nextBoolean()) {
                pickedCard = discardPile.getTopCard();
                System.out.println(name + " picks up " + pickedCard + " from discard pile.");
                addCard(pickedCard);
                discardPile.addCard(hand.remove(0));
            } else {
                pickedCard = deck.drawCard();
                System.out.println(name + " draws " + pickedCard + " from the deck.");
                addCard(pickedCard);
                Card cardToDiscard = hand.get(new Random().nextInt(hand.size()));
                hand.remove(cardToDiscard);
                discardPile.addCard(cardToDiscard);
                System.out.println(name + " discards " + cardToDiscard);
            }
        }
        checkForPair(pickedCard);
        Game.displayStatus();
    }

    private void checkForWin() {
        if (hasThreePairs()) {
            System.out.println("🎉 " + name + " wins with 3 pairs! 🎉");
            System.out.println("Pairs formed: " + pairs);
            Game.stopGame();
        }
    }

    public void setMyTurn(boolean turn) {
        this.isMyTurn = turn;
    }
}
