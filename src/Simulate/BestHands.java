package Simulate;

import Game.*;
import java.io.*;
import java.util.*;

// These classes run 3,000,000 simulated games (or GAMES #) using the omnisciant AI from Game.Euchre.
// The results are output to STDOUT in plain text

// Class to run in threaded mode
class ThreadBestHands implements Runnable {
	private BestHands bestHand;
	private int count;

	public ThreadBestHands (BestHands bh, int c) {
		bestHand = bh;
		count = c;
	}
	public void run() {
		bestHand.multipleSimulate(count);
	}
}

// BestHands coordinates running the threads, contains the simulation method, and outputs the results
class BestHands {
	private HashMap<String, ResultsData> results;
	private static final int GAMES = 3000000;

	public BestHands() {
		results = new HashMap<String, ResultsData>();
	}

	public void runThreadedSimulate() {
		// Run threaded
		Thread t1 = new Thread(new ThreadBestHands(this, GAMES/4));
		Thread t2 = new Thread(new ThreadBestHands(this, GAMES/4));
		Thread t3 = new Thread(new ThreadBestHands(this, GAMES/4));
		Thread t4 = new Thread(new ThreadBestHands(this, GAMES/4));

		t1.start(); t2.start(); t3.start(); t4.start();
		try {
			t1.join(); t2.join(); t3.join(); t4.join();
		} catch (InterruptedException e) {
			System.out.println("Interrupted with threads running\n");
			System.exit(1);
		}

		Iterator it = results.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			System.out.println(pairs.getKey()+"/"+((ResultsData)pairs.getValue()).toString());
			it.remove();
		}
	}
	public void multipleSimulate(int count) {
		while ( count > 0 ) {
			simulate();
			count--;
		}
	}
	public void simulate() {
		Euchre game = newGame();

		// Deal and determine trump
		while ( game.getState() != Euchre.GAME_OVER ) {
			game.deal();
			while ( game.getState() == Euchre.ORDERING_TRUMP ) {
				game.playerOrderUp(game.getCurrentPlayer(), game.aiOrderUp(game.getCurrentPlayer()));
			}
			if ( game.getState() == Euchre.DEALER_DISCARDING ) {
				game.dealerDiscard(game.aiDiscard(game.getDealer()));
			}
			while ( game.getState() == Euchre.CALLING_TRUMP ) {
				game.playerCallTrump(game.getCurrentPlayer(), game.aiCallTrump(game.getCurrentPlayer()));
			}

			// Capture the hands
			SimHand[] playerHands = new SimHand[4];
			for (int player=0; player<4; player++) {
				playerHands[player] = new SimHand(game.getPlayerHand(player), game.getTrump());
				playerHands[player].sortByValue();
			}

			// Play the round
			game.startPlaying();
			while ( game.getState() != Euchre.GAME_OVER && game.getState() != Euchre.DEALING ) {
				while ( game.getState() == Euchre.PLAYING_HAND ) {
					game.aiPlayCard(game.getCurrentPlayer());
				}
				game.pickUpTrick();
			}

			// Store the results
			addHandResults(game, playerHands[0], 0);
			addHandResults(game, playerHands[1], 1);
			addHandResults(game, playerHands[2], 2);
			addHandResults(game, playerHands[3], 3);
		}
	}
	public synchronized void addHandResults(Euchre game, SimHand hand, int player) {
		hand.sortByValue();
		ResultsData data = results.get( hand.toString() );
		if ( data == null ) {
			data = new ResultsData();
			results.put(hand.toString(), data);
		}

		data.incPlayed();
		if ( game.getPlayerCalledTrump() == player ) { data.incCalled(); }
		if ( player%2 == game.getHandWinner() ) {
			data.incWon();
			if ( game.getPlayerCalledTrump()%2 != game.getHandWinner() ) {
				data.incEuchred();
			}
		} else {
			data.incLost();
			if ( game.getPlayerCalledTrump() == player ) {
				data.incSet();
			}
		}
	}
	public Euchre newGame() {
		return new Euchre();
	}

	public static void main(String args[]) {
		new BestHands().runThreadedSimulate();
	}
}
