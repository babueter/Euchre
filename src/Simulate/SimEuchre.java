package Simulate;

import Game.*;
import java.io.*;
import java.util.*;

// Use Simulated gameplay results for AI decision making instead of the omniscient AI in Game.Euchre
public class SimEuchre extends Euchre {
	public static HashMap<String, Integer> pctWin = null;
	Integer[] playerConfidence;

	public SimEuchre() {
		super();

		pctWin = SimEuchre.getData();

		// Randomly create player confidence for each of the 4 players
		playerConfidence = new Integer[4];
		playerConfidence[0] = (int)(Math.random()*40)+10; // Always call > 90% win
		playerConfidence[1] = (int)(Math.random()*40)+10;
		playerConfidence[2] = (int)(Math.random()*40)+10;
		playerConfidence[3] = (int)(Math.random()*40)+10;
	}

	// If win percentage of the hand + player confidence is greater then 100, call up, otherwise pass
	public boolean aiOrderUp(int player) {
		if ( this.getState() != ORDERING_TRUMP ) { return false; }
		if ( this.getCurrentPlayer() != player ) { return false; }

		Euchre game = new Euchre(this);
		game.playerOrderUp(player, true);

		SimHand hand = new SimHand(game.getPlayerHand(player), game.getTrump());
		String handText = new Integer(hand.hashCode()).toString();

		if ( playerConfidence[player]+pctWin.get(handText) > 100 ) {
			return true;
		}
		return false;
	}

	// If win percentage of the hand + player confidence is greater then 100, for any trump in hand,
	// call that trump, otherwise pass
	//
	// Dealer will call the highest win percentage regardless of confidence
	public int aiCallTrump(int player) {
		if ( this.getState() != CALLING_TRUMP ) { return -1; }
		if ( this.getCurrentPlayer() != player ) { return -1; }

		int trump = (getKitty().getSuit() + 1) % 4;
		int bestTrump = trump;
		int bestConfidence = playerConfidence[player];

		// Walk through each trump and determine confidence, keep track of highest value
		while ( trump != getKitty().getSuit() ) {
			SimHand hand = new SimHand(getPlayerHand(player), trump);
			String handText = new Integer(hand.hashCode()).toString();

			if ( pctWin.get(handText)+playerConfidence[player] > bestConfidence ) {
				bestTrump = trump;
				bestConfidence = pctWin.get(handText)+playerConfidence[player];
			}
			trump = (trump+1) % 4;
		}

		// Call trump if we have to, or if we are confident if its chances
		if ( getDealer() == player || bestConfidence > 100 ) {
			return bestTrump;
		}
		return -1;
	}

	// Designed to be initialized at the class level.  Call SimEuchre.getData() and not object.getData().
	// This way loading /Data/HandData.txt only happens once
	public synchronized static HashMap<String, Integer> getData() {
		if ( pctWin == null ) {
			pctWin = new HashMap<String, Integer>();
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(SimEuchre.class.getResourceAsStream("/Data/HandData.txt")));
				Scanner sc = new Scanner(in);
				while ( sc.hasNextInt() ) {
					pctWin.put(new Integer(sc.nextInt()).toString(), new Integer(sc.nextInt()));
				}
				in.close();
			} catch (IOException e) {
				System.err.format("IOException: %s%n", e);
				System.exit(0);
			}
		}
		return pctWin;
	}
}
