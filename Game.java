package Playing_Cards_Game;

import java.util.*;

public class Game {
    private static boolean gameOver = false;
    private static List<Player> players = new ArrayList<>();
    private static int currentPlayerIndex = 0;

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void stopGame() {
        gameOver = true;
        System.out.println("Game Over!");
    }

    public static void displayStatus() {
        System.out.println("\n--- Current Status ---");
        for (Player player : players) {
            System.out.println(player.name + "'s Hand: " + player.hand);
        }
        System.out.println("----------------------\n");
    }
    public static void nextTurn() {
        players.get(currentPlayerIndex).setMyTurn(false);
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        players.get(currentPlayerIndex).setMyTurn(true);

        synchronized (players.get(currentPlayerIndex)) {
            players.get(currentPlayerIndex).notify();  // Notify the current player to take their turn
        }
    }


    public static void main(String[] args) {
        Deck deck = new Deck();
        DiscardPile discardPile = new DiscardPile();

        Player player1 = new Player("Player 1", deck, discardPile, true);
        Player player2 = new Player("Player 2", deck, discardPile, false);
        Player player3 = new Player("Player 3", deck, discardPile, false);
        Player player4 = new Player("Player 4", deck, discardPile, false);

        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        for (int i = 0; i < 5; i++) {
            player1.addCard(deck.drawCard());
            player2.addCard(deck.drawCard());
            player3.addCard(deck.drawCard());
            player4.addCard(deck.drawCard());
        }

        System.out.println("Initial Hands:");
        displayStatus();

        player1.setMyTurn(true);
        for (Player player : players) {
            player.start();
        }

        synchronized (player1) {
            player1.notify();  // Start Player 1's turn
        }
    }}

