package Simulate;

import Game.*;

// Create a hand based on Game.Hand relative to trump.  Copy each card in the hand and make it
// a Simulate.Card also relative to trump
public class SimHand extends Hand {
	public SimHand(Hand hand, int trump) {
		super();

		for (int i=0; i<hand.getCardCount(); i++) {
			SimCard card = new SimCard(hand.getCard(i), trump);
			addCard(card);
		}
	}
	public SimHand() {
		super();
	}

	// This is unique to every possible hand
	public int hashCode() {
		int code = 0;
		for (int i=0; i<getCardCount(); i++) {
			code += getCard(i).hashCode();
		}
		return code;
	}

	public boolean equals(SimHand hand) {
		int handValue = 0;
		int thisValue = 0;

		for (int i=0; i<getCardCount(); i++) {
			handValue += hand.getCard(i).getValue();
			thisValue += getCard(i).getValue();
		}

		return (handValue == thisValue);
	}

	public String toString() {
		String name = "";
		for (int i=0; i<getCardCount(); i++) {
			name += getCard(i).toString();
			if ( i < getCardCount()-1 ) {
				name += " ";
			}
		}
		return name;
	}
}

